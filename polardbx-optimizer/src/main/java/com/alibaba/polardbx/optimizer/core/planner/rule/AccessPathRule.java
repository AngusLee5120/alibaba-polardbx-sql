/*
 * Copyright [2013-2021], Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.polardbx.optimizer.core.planner.rule;

import com.alibaba.polardbx.common.model.sqljep.Comparative;
import com.alibaba.polardbx.common.properties.ConnectionParams;
import com.alibaba.polardbx.common.utils.GeneralUtil;
import com.alibaba.polardbx.common.utils.TreeMaps;
import com.alibaba.polardbx.gms.metadb.table.IndexVisibility;
import com.alibaba.polardbx.gms.topology.DbInfoManager;
import com.alibaba.polardbx.optimizer.PlannerContext;
import com.alibaba.polardbx.optimizer.config.table.GlobalIndexMeta;
import com.alibaba.polardbx.optimizer.config.table.GlobalIndexMeta.IndexType;
import com.alibaba.polardbx.optimizer.config.table.GsiMetaManager;
import com.alibaba.polardbx.optimizer.config.table.TableMeta;
import com.alibaba.polardbx.optimizer.context.ExecutionContext;
import com.alibaba.polardbx.optimizer.core.DrdsConvention;
import com.alibaba.polardbx.optimizer.core.rel.LogicalIndexScan;
import com.alibaba.polardbx.optimizer.core.rel.LogicalModifyView;
import com.alibaba.polardbx.optimizer.core.rel.LogicalView;
import com.alibaba.polardbx.optimizer.core.rel.MysqlTableScan;
import com.alibaba.polardbx.optimizer.index.IndexUtil;
import com.alibaba.polardbx.optimizer.index.TableScanFinder;
import com.alibaba.polardbx.optimizer.partition.PartitionInfo;
import com.alibaba.polardbx.optimizer.planmanager.LogicalViewFinder;
import com.alibaba.polardbx.optimizer.rule.TddlRuleManager;
import com.alibaba.polardbx.optimizer.sharding.ConditionExtractor;
import com.alibaba.polardbx.optimizer.sql.sql2rel.TddlSqlToRelConverter;
import com.alibaba.polardbx.optimizer.utils.RelUtils;
import com.alibaba.polardbx.rule.TableRule;
import com.alibaba.polardbx.rule.TddlRule;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelOptRuleOperand;
import org.apache.calcite.plan.RelOptSchema;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalTableScan;
import org.apache.calcite.rel.metadata.RelColumnOrigin;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.rules.ProjectMergeRule;
import org.apache.calcite.rel.rules.ProjectRemoveRule;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlIndexHint;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.util.Pair;
import org.apache.calcite.util.Util;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public abstract class AccessPathRule extends RelOptRule {

    public AccessPathRule(RelOptRuleOperand operand, String description) {
        super(operand, "AccessPathRule:" + description);
    }

    /**
     * AccessPathRule should match non-single-group logicalview whenever for two reasons
     * <p>
     * 1. when logicalView shard count > 1 , it can benefits from AccessPathRule
     * 2. when logicalView shard count = 1 , AccessPath may be wrong, for example:
     * <p>
     * LogicalView {
     * Sort
     * Filter  (shard_key = 1)
     * TableScan (sharding key is shard_key)
     * }
     * <p>
     * we should not change TableScan to its index table! (the same reason as full match Agg push case)
     */

    public static final AccessPathRule LOGICALVIEW = new AccessPathLogicalViewRule(
        operand(LogicalView.class, null, LogicalView.NOT_SINGLE_GROUP, none()),
        "LOGICALVIEW");

    enum IndexAbleType {
        UNKNOWN,
        YES,
        NO
    }

    static class AccessPathLogicalViewRule extends AccessPathRule {

        public AccessPathLogicalViewRule(RelOptRuleOperand operand, String description) {
            super(operand, description);
        }

        @Override
        public boolean matches(RelOptRuleCall call) {
            final LogicalView logicalView = call.rel(0);
            PlannerContext plannerContext = PlannerContext.getPlannerContext(logicalView);
            if (!plannerContext.getParamManager().getBoolean(ConnectionParams.ENABLE_INDEX_SELECTION)) {
                return false;
            }

            // only match when all nodes convention are Convention.NONE
            // because after sharding key full match group by key Agg push, we should not change logicalView (which convention will be DrdsConvention) AccessPath
            if (logicalView.getConvention() != Convention.NONE) {
                return false;
            }

            if (logicalView instanceof LogicalIndexScan) {
                return false;
            }

            if (logicalView instanceof LogicalModifyView) {
                return false;
            }

            if (logicalView.isFromMergeIndex()) {
                return false;
            }
            return true;
        }

        @Override
        public void onMatch(RelOptRuleCall call) {
            final LogicalView logicalView = call.rel(0);
            if (logicalView.getScalarList().size() > 0) {
                // apply node stop gsi selection, try hint PUSH_CORRELATE_MATERIALIZED_LIMIT=0
                // to avoid pushing scalar subquery
                return;
            }

            final ExecutionContext ec = PlannerContext.getPlannerContext(call).getExecutionContext();
            final String schemaName = logicalView.getSchemaName();
            final RelOptTable primaryTable = logicalView.getTable();
            final RelNode plan = logicalView.getPushedRelNode();

            List<String> gsiNameList = getGsiNameList(logicalView, ec);

            if (GeneralUtil.isEmpty(gsiNameList)) {
                return;
            }

            if (logicalView.useSelectPartitions()) {
                return;
            }

            SqlSelect.LockMode lockMode = logicalView.getLockMode();
            PlannerContext plannerContext = PlannerContext.getPlannerContext(logicalView);

            final RelMetadataQuery mq = logicalView.getCluster().getMetadataQuery();
            IndexAbleType primaryCanUseIndex = canUseLocalIndex(logicalView, mq);
            for (String gsiName : gsiNameList) {
                final RelOptSchema catalog = RelUtils.buildCatalogReader(schemaName, ec);
                final RelOptTable indexTable = catalog.getTableForMember(ImmutableList.of(schemaName, gsiName));
                if (isCoveringIndex(mq, logicalView, primaryTable, gsiName)
                    && (lockMode == null || lockMode == SqlSelect.LockMode.UNDEF)) {
                    final IndexScanVisitor indexScanVisitor =
                        new IndexScanVisitor(primaryTable, indexTable, call.builder());
                    final LogicalIndexScan logicalIndexScan =
                        new LogicalIndexScan(plan.accept(indexScanVisitor), indexTable, logicalView.getHints(),
                            lockMode, logicalView.getFlashback());

                    logicalIndexScan.rebuildPartRoutingPlanInfo();

                    if (shouldPruneGsi(logicalIndexScan, mq, primaryCanUseIndex, logicalView)) {
                        continue;
                    }
                    // use non convention for match CBO push join rule
                    final RelNode result = logicalIndexScan;
                    call.transformTo(result);
                } else {
                    if (logicalView.getTableNames().size() > 1) {
                        continue;
                    }

                    // TODO: improve skyline here
                    if (plannerContext.getParamManager().getBoolean(ConnectionParams.ENABLE_INDEX_SKYLINE)) {
                        if (!canBenefitFromIndex(logicalView, gsiName)) {
                            continue;
                        }
                    }

                    IndexTableLookupVisitor indexTableLookupVisitor = new IndexTableLookupVisitor(primaryTable,
                        indexTable, lockMode);
                    RelNode node = plan.accept(indexTableLookupVisitor);
                    RelNode expandNode = optimizeByTableLookupRule(node, plannerContext);
                    if (shouldPruneGsi(expandNode, mq, primaryCanUseIndex, logicalView)) {
                        continue;
                    }
                    RelNode convertNode = convert(expandNode,
                        expandNode.getTraitSet().simplify().replace(DrdsConvention.INSTANCE));
                    call.transformTo(convertNode);
                }
            }

            if (plannerContext.getParamManager().getBoolean(ConnectionParams.ENABLE_MERGE_INDEX)) {
                // deal with merge global secondary index
                // we consider each gsi only contain the index as its definition
                if (logicalView.getTableNames().size() > 1) {
                    return;
                }
                // use MergeIndexRule to analyze condition
                MergeIndexRule mergeIndexRule = new MergeIndexRule(schemaName, gsiNameList, lockMode);
                HepProgramBuilder builder = new HepProgramBuilder();
                builder.addGroupBegin();
                builder.addRuleInstance(mergeIndexRule);
                builder.addGroupEnd();
                HepPlanner hepPlanner = new HepPlanner(builder.build(), plannerContext);
                hepPlanner.stopOptimizerTrace();
                hepPlanner.setRoot(plan);
                RelNode output = hepPlanner.findBestExp();
                if (mergeIndexRule.work()) {
                    output = optimizeByTableLookupRule(output, plannerContext);
                    RelNode convertNode = convert(output,
                        output.getTraitSet().replace(DrdsConvention.INSTANCE));
                    call.transformTo(convertNode);
                }
            }
        }

        /**
         * check if the gsi can't use local index and primary can use local index
         *
         * @param node the root of transformed gsi
         * @param mq the metadata query
         * @param originCanUseIndex whether the primary table can use index
         * @param origin origin logicalView
         * @return true if the gsi can be pruned
         */
        private boolean shouldPruneGsi(RelNode node,
                                       RelMetadataQuery mq,
                                       IndexAbleType originCanUseIndex,
                                       LogicalView origin) {
            if (!PlannerContext.getPlannerContext(origin).isGsiPrune()) {
                return false;
            }
            if (!IndexAbleType.YES.equals(originCanUseIndex)) {
                // origin table can't use gsi
                return false;
            }
            // multiple tables
            if (origin.getTableNames().size() > 1) {
                return false;
            }

            return IndexAbleType.NO.equals(canUseLocalIndex(node, mq));
        }

        /**
         * check whether the relNode tree can use local index
         *
         * @param mq the metadata query
         * @return YES if can use local index, NO if not, UNKNOWN otherwise
         */
        private static IndexAbleType canUseLocalIndex(RelNode root, RelMetadataQuery mq) {
            if (!PlannerContext.getPlannerContext(root).isGsiPrune()) {
                return IndexAbleType.UNKNOWN;
            }
            // get the logicalView of relNode
            LogicalViewFinder finder = new LogicalViewFinder();
            root.accept(finder);
            List<LogicalView> lvs = finder.getResult();
            if (CollectionUtils.isEmpty(lvs)) {
                return IndexAbleType.UNKNOWN;
            }
            // get the first logicalView
            LogicalView lv = lvs.get(0);

            // get the mysqlTableScan of logicalView
            TableScanFinder tableScanFinder = new TableScanFinder();
            RelNode mysqlNode = lv.getMysqlNode();
            if (mysqlNode == null) {
                return IndexAbleType.UNKNOWN;
            }
            mysqlNode.accept(tableScanFinder);
            List<TableScan> tables = tableScanFinder.getResult().stream().map(
                com.alibaba.polardbx.common.utils.Pair::getValue).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(tables)) {
                return IndexAbleType.UNKNOWN;
            }
            // get the first tableScan
            TableScan ts = tables.get(0);
            if (!(ts instanceof MysqlTableScan)) {
                return IndexAbleType.UNKNOWN;
            }
            // get the getAccessIndex of mysqlTableScan
            MysqlTableScan mysqlTableScan = (MysqlTableScan) ts;
            return CollectionUtils.isEmpty(mysqlTableScan.getAccessIndexList(mq)) ?
                IndexAbleType.NO : IndexAbleType.YES;
        }

        private boolean canBenefitFromIndex(LogicalView logicalView, String gsiName) {
            final String schemaName = logicalView.getSchemaName();
            final String tableName = logicalView.getLogicalTableName();
            final List<String> shardColumns;

            TddlRuleManager tddlRuleManager =
                PlannerContext.getPlannerContext(logicalView).getExecutionContext().getSchemaManager(schemaName)
                    .getTddlRuleManager();

            if (!logicalView.isNewPartDbTbl()) {
                TddlRule tddlRule = tddlRuleManager.getTddlRule();
                if (tddlRule == null) {
                    return false;
                }

                TableRule tableRule = tddlRule.getTable(gsiName);
                if (tableRule == null) {
                    return false;
                }

                shardColumns = tableRule.getShardColumns();
            } else {
                PartitionInfo partitionInfo = tddlRuleManager.getPartitionInfoManager().getPartitionInfo(gsiName);
                shardColumns = partitionInfo.getPartitionColumns();
            }

            Map<String, Map<String, Comparative>> allComps = new HashMap<>();
            ConditionExtractor.predicateFrom(logicalView).extract().allColumnCondition(allComps, shardColumns);
            if (allComps.isEmpty()) {
                return false;
            }

            Map<String, Comparative> comparativeMap = allComps.get(tableName);
            if (comparativeMap.isEmpty()) {
                return false;
            }
            return true;
        }

        private RelNode optimizeByTableLookupRule(RelNode input, PlannerContext plannerContext) {
            // input should contain [Project] + [Filter] + [TableLookup]
            HepProgramBuilder builder = new HepProgramBuilder();
            // tableLookupTranspose
            builder.addGroupBegin();
            builder.addRuleInstance(FilterTableLookupTransposeRule.INSTANCE);
            builder.addRuleInstance(ProjectTableLookupTransposeRule.INSTANCE);
            builder.addGroupEnd();
            builder.addGroupBegin();
            // push filter
            builder.addRuleInstance(PushFilterRule.LOGICALVIEW);
            builder.addRuleInstance(PushFilterRule.MERGE_SORT);
            builder.addRuleInstance(PushFilterRule.LOGICALUNION);
            // push project
            builder.addRuleInstance(PushProjectRule.INSTANCE);
            builder.addRuleInstance(ProjectMergeRule.INSTANCE);
            builder.addRuleInstance(ProjectRemoveRule.INSTANCE);
            builder.addGroupEnd();
            builder.addGroupBegin();
            builder.addRuleInstance(OptimizeLogicalTableLookupRule.INSTANCE);
            builder.addGroupEnd();
            HepPlanner hepPlanner = new HepPlanner(builder.build(), plannerContext);
            hepPlanner.stopOptimizerTrace();
            hepPlanner.setRoot(input);
            RelNode output = hepPlanner.findBestExp();
            return output;
        }
    }

    public static boolean hasOnlyOneAccessPath(LogicalView logicalView, ExecutionContext ec) {
        List<String> gsiNameList = getGsiNameList(logicalView, ec);
        return GeneralUtil.isEmpty(gsiNameList);
    }

    public static List<String> getGsiNameList(LogicalView logicalView, ExecutionContext ec) {
        final String schemaName = logicalView.getSchemaName();
        List<String> gsiPublishedNameList = null;
        if (logicalView.getTableNames().size() == 1 && !logicalView.isSingleGroup()) {
            // do index selection for single logical table only
            final String logicalTableName = logicalView.getLogicalTableName();
            gsiPublishedNameList = GlobalIndexMeta.getPublishedIndexNames(logicalTableName, schemaName, ec);
            gsiPublishedNameList = filterVisibleIndex(schemaName, logicalTableName, gsiPublishedNameList, ec);
        } else if (logicalView.getTableNames().size() > 1) {
            // do index selection for shard logical table with multi broadcast table
            List<String> tableNames = logicalView.getTableNames();
            String logicalTableName = null;
            for (String tableName : tableNames) {
                if (!ec.getSchemaManager(schemaName).getTddlRuleManager().isBroadCast(tableName)) {
                    if (logicalTableName == null) {
                        logicalTableName = tableName;
                    } else {
                        return null;
                    }
                }
            }
            if (logicalTableName == null) {
                return null;
            }
            gsiPublishedNameList = GlobalIndexMeta.getPublishedIndexNames(logicalTableName, schemaName, ec);
            gsiPublishedNameList = filterVisibleIndex(schemaName, logicalTableName, gsiPublishedNameList, ec);
        }

        if (logicalView.getIndexNode() != null) {
            gsiPublishedNameList = filterUseIgnoreIndex(schemaName, gsiPublishedNameList, logicalView.getIndexNode());
        }
        return gsiPublishedNameList;
    }

    public static List<String> filterUseIgnoreIndex(String schemaName, List<String> gsiNameList, SqlNode indexNode) {
        Set<String> gsiNameSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        if (gsiNameList == null) {
            return new ArrayList<>();
        }

        // force index(PRIMARY)
        Set<String> gsiForceNameSet = IndexUtil.getForceIndex(indexNode);
        if (gsiForceNameSet.size() == 1 && gsiForceNameSet.iterator().next().equalsIgnoreCase("PRIMARY")) {
            return new ArrayList<>();
        }

        gsiNameSet.addAll(gsiNameList);

        Set<String> gsiUseNameSet = IndexUtil.getUseIndex(indexNode);
        Set<String> gsiIgnoreNameSet = IndexUtil.getIgnoreIndex(indexNode);

        final boolean needUnwrap = DbInfoManager.getInstance().isNewPartitionDb(schemaName);

        if (!gsiIgnoreNameSet.isEmpty()) {
            if (needUnwrap) {
                // Remove if name or unwrapped name.
                gsiNameSet.removeIf(
                    idx -> gsiIgnoreNameSet.contains(TddlSqlToRelConverter.unwrapGsiName(idx)) ||
                        gsiIgnoreNameSet.contains(idx));
            } else {
                gsiNameSet.removeAll(gsiIgnoreNameSet);
            }
        }

        if (!gsiUseNameSet.isEmpty()) {
            if (needUnwrap) {
                // Remove only if neither name or unwrapped name.
                gsiNameSet.removeIf(
                    idx -> !gsiUseNameSet.contains(TddlSqlToRelConverter.unwrapGsiName(idx)) &&
                        !gsiUseNameSet.contains(idx));
            } else {
                gsiNameSet.retainAll(gsiUseNameSet);
            }
        }

        ArrayList<String> result = new ArrayList<>();
        result.addAll(gsiNameSet);
        return result;
    }

    /**
     * 返回visible index列表
     * see: https://dev.mysql.com/doc/refman/8.0/en/invisible-indexes.html
     * visible与gsi在online schema change中的状态不同
     */
    private static List<String> filterVisibleIndex(String schemaName, String primaryTable, List<String> gsiNameList,
                                                   ExecutionContext ec) {
        final List<String> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(gsiNameList)) {
            return result;
        }
        if (ec.getSchemaManager(schemaName) == null || ec.getSchemaManager(schemaName).getTable(primaryTable) == null) {
            return gsiNameList;
        }
        final TableMeta table = ec.getSchemaManager(schemaName).getTable(primaryTable);
        final Map<String, GsiMetaManager.GsiIndexMetaBean> gsiPublished = table.getGsiPublished();
        if (gsiPublished == null) {
            return gsiNameList;
        }
        for (String gsiName : gsiNameList) {
            if (gsiPublished.containsKey(gsiName) && gsiPublished.get(gsiName).visibility == IndexVisibility.VISIBLE) {
                result.add(gsiName);
            }
        }

        return result;
    }

    public static void nomoralizeIndexNode(LogicalView logicalView) {
        logicalView.getPushedRelNode().accept(new NormalizedIndexHintVisitor());
    }

    /**
     * preCondition: (satisfy 1 or 2)
     * 1. plan contains single table without any join
     * 2. if plan contains join, at most one of them is non-broadcast table
     * return index of table index covering plan
     */
    public static boolean isCoveringIndex(RelMetadataQuery mq, RelNode plan, RelOptTable table, String index) {
        final List<Set<RelColumnOrigin>> coveringIndex = mq.isCoveringIndex(plan, table, index);
        if (coveringIndex == null) {
            return false;
        }
        return coveringIndex.stream()
            .allMatch(relColumnOrigins -> relColumnOrigins.stream()
                .noneMatch(relColumnOrigin -> relColumnOrigin.getOriginTable().equals(table)));
    }

    private static class NormalizedIndexHintVisitor extends RelShuttleImpl {
        public NormalizedIndexHintVisitor() {
        }

        @Override
        public RelNode visit(TableScan scan) {

            final List<String> qualifiedName = scan.getTable().getQualifiedName();
            final String tableName = Util.last(scan.getTable().getQualifiedName());
            final String schemaName = qualifiedName.size() == 2 ? qualifiedName.get(0) : null;

            Set<String> useIndexNames = IndexUtil.getUseIndex(scan.getIndexNode());
            Set<String> ignoreIndexNames = IndexUtil.getIgnoreIndex(scan.getIndexNode());
            Set<String> forceIndexNames = IndexUtil.getForceIndex(scan.getIndexNode());

            Set<String> normalizedUseIndexNames = normalizeIndexNames(useIndexNames, tableName, schemaName,
                PlannerContext.getPlannerContext(scan).getExecutionContext());
            Set<String> normalizedIgnoreIndexNames = normalizeIndexNames(ignoreIndexNames, tableName, schemaName,
                PlannerContext.getPlannerContext(scan).getExecutionContext());
            Set<String> normalizedForceIndexNames = normalizeIndexNames(forceIndexNames, tableName, schemaName,
                PlannerContext.getPlannerContext(scan).getExecutionContext());

            SqlNodeList normalizedIndexNode = new SqlNodeList(SqlParserPos.ZERO);
            if (!normalizedUseIndexNames.isEmpty()) {
                SqlIndexHint sqlIndexHint =
                    new SqlIndexHint(SqlLiteral.createCharString("USE INDEX", SqlParserPos.ZERO),
                        null,
                        new SqlNodeList(
                            normalizedUseIndexNames.stream().map(x -> SqlLiteral.createCharString(x, SqlParserPos.ZERO))
                                .collect(Collectors.toList()),
                            SqlParserPos.ZERO),
                        SqlParserPos.ZERO);
                normalizedIndexNode.add(sqlIndexHint);
            }

            if (!normalizedIgnoreIndexNames.isEmpty()) {
                SqlIndexHint sqlIndexHint =
                    new SqlIndexHint(SqlLiteral.createCharString("IGNORE INDEX", SqlParserPos.ZERO),
                        null,
                        new SqlNodeList(
                            normalizedIgnoreIndexNames.stream()
                                .map(x -> SqlLiteral.createCharString(x, SqlParserPos.ZERO))
                                .collect(Collectors.toList()),
                            SqlParserPos.ZERO),
                        SqlParserPos.ZERO);
                normalizedIndexNode.add(sqlIndexHint);
            }

            if (!normalizedForceIndexNames.isEmpty()) {
                SqlIndexHint sqlIndexHint =
                    new SqlIndexHint(SqlLiteral.createCharString("FORCE INDEX", SqlParserPos.ZERO),
                        null,
                        new SqlNodeList(
                            normalizedForceIndexNames.stream()
                                .map(x -> SqlLiteral.createCharString(x, SqlParserPos.ZERO))
                                .collect(Collectors.toList()),
                            SqlParserPos.ZERO),
                        SqlParserPos.ZERO);
                normalizedIndexNode.add(sqlIndexHint);
            }

            if (normalizedIndexNode.size() == 0) {
                scan.setIndexNode(null);
            } else {
                scan.setIndexNode(normalizedIndexNode);
            }
            return scan;
        }

        private Set<String> normalizeIndexNames(Set<String> indexNames, String tableName, String schemaName,
                                                ExecutionContext ec) {
            Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            for (String indexName : indexNames) {
                // Only care about local so skip the wrapped name.
                final IndexType indexType = GlobalIndexMeta.getIndexType(tableName, indexName, schemaName, ec);

                switch (indexType) {
                case PUBLISHED_GSI:
                case UNPUBLISHED_GSI:
                case NONE:
                    break;
                case LOCAL:
                    result.add(indexName);
                default:
                    break;
                }
            }
            return result;
        }
    }

    private static class IndexTableLookupVisitor extends RelShuttleImpl {
        private final RelOptTable primaryTable;
        private final RelOptTable indexTable;
        private final SqlSelect.LockMode lockMode;

        private IndexTableLookupVisitor(RelOptTable primaryTable, RelOptTable indexTable, SqlSelect.LockMode lockMode) {
            this.primaryTable = primaryTable;
            this.indexTable = indexTable;
            this.lockMode = lockMode;
        }

        @Override
        public RelNode visit(TableScan scan) {
            if (!scan.getTable().equals(primaryTable)) {
                return super.visit(scan);
            }

            final LogicalView primary = RelUtils.createLogicalView(scan, lockMode);
            final LogicalTableScan indexTableScan =
                LogicalTableScan.create(scan.getCluster(), this.indexTable, scan.getHints(), null, scan.getFlashback(),
                    scan.getFlashbackOperator(),
                    null);
            final LogicalIndexScan index = new LogicalIndexScan(this.indexTable, indexTableScan, lockMode);
            index.setFlashback(scan.getFlashback());
            index.setFlashbackOperator(scan.getFlashbackOperator());
            return RelUtils.createTableLookup(primary, index, index.getTable());
        }
    }

    private static class IndexScanVisitor extends RelShuttleImpl {

        private final RelOptTable primary;
        private final RelOptTable index;
        private final Map<String, Integer> indexColumnRefMap;
        private final RelBuilder relBuilder;

        private IndexScanVisitor(RelOptTable primary, RelOptTable index, RelBuilder relBuilder) {
            this.primary = primary;
            this.index = index;
            this.indexColumnRefMap = index.getRowType().getFieldList()
                .stream()
                .collect(Collectors.toMap(RelDataTypeField::getName,
                    RelDataTypeField::getIndex,
                    (x, y) -> y,
                    TreeMaps::caseInsensitiveMap));
            this.relBuilder = relBuilder;
        }

        @Override
        protected RelNode visitChild(RelNode parent, int i, RelNode child) {
            stack.push(parent);
            try {
                RelNode child2 = child.accept(this);
                if (child2 != child) {
                    final List<RelNode> newInputs = new ArrayList<>(parent.getInputs());
                    newInputs.set(i, child2);

                    RelNode selfNode = parent.copy(parent.getTraitSet(), newInputs).setHints(parent.getHints());
                    if (!(selfNode instanceof LogicalProject)) {
                        RelUtils.changeRowType(selfNode, null);
                    }
                    return selfNode;
                }
                return parent;
            } finally {
                stack.pop();
            }
        }

        @Override
        public RelNode visit(TableScan scan) {
            if (!scan.getTable().equals(primary)) {
                return super.visit(scan);
            }

            final LogicalTableScan indexTableScan =
                LogicalTableScan.create(scan.getCluster(), index, scan.getHints(), null, scan.getFlashback(),
                    scan.getFlashbackOperator(), null);

            indexTableScan.setIndexNode(Optional.ofNullable(scan.getIndexNode())
                .filter(indexNode -> indexNode instanceof SqlNodeList && ((SqlNodeList) indexNode).size() > 0)
                // If more than one index specified, choose first one only
                .map(indexNode -> (SqlIndexHint) ((SqlNodeList) indexNode).get(0))
                // only support force index
                .filter(SqlIndexHint::forceIndex)
                // Dealing with force index(`xxx`), `xxx` will decoded as string.
                .map(indexNode -> {
                    final String indexName = GlobalIndexMeta.getIndexName(
                        RelUtils.lastStringValue(indexNode.getIndexList().get(0)));

                    final Pair<String, String> qn = RelUtils.getQualifiedTableName(index);

                    // Caution: This only care about local index, so GSI unwrapping is no needed.
                    final IndexType indexType = GlobalIndexMeta.getIndexType(qn.right, indexName, qn.left,
                        PlannerContext.getPlannerContext(scan).getExecutionContext());

                    SqlNode result = null;
                    if (indexType == IndexType.LOCAL) {
                        result = SqlNodeList.of(indexNode);
                    }

                    return result;
                })
                .orElse(null));

            final RexBuilder rexBuilder = scan.getCluster().getRexBuilder();
            final RelDataType rowType = index.getRowType();
            final List<RexNode> projects = primary.getRowType().getFieldNames().stream().map(cn -> {
                final Integer ref = indexColumnRefMap.getOrDefault(cn, -1);
                if (ref < 0) {
                    // NULL for columns not int index table
                    return rexBuilder.constantNull();
                } else {
                    final RelDataType type = rowType.getFieldList().get(ref).getType();
                    return rexBuilder.makeInputRef(type, ref);
                }
            }).collect(Collectors.toList());

            relBuilder.push(indexTableScan);
            relBuilder.project(projects, primary.getRowType().getFieldNames());
            return relBuilder.build();
        }
    }
}
