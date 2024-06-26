/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.polardbx.druid.sql.ast.statement;

import com.alibaba.polardbx.druid.DbType;
import com.alibaba.polardbx.druid.sql.ast.SQLExpr;
import com.alibaba.polardbx.druid.sql.ast.SQLStatementImpl;
import com.alibaba.polardbx.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLShowPartitionsStmt extends SQLStatementImpl implements SQLShowStatement {

    private SQLExprTableSource tableSource;

    private List<SQLAssignItem> partition = new ArrayList<SQLAssignItem>();

    private SQLExpr where;

    private boolean showPartMeta = true;
    private SQLExpr indexName = null;
    private boolean showIndexPartMeta = false;

    public void setPartition(List<SQLAssignItem> partition) {
        this.partition = partition;
    }

    public SQLExpr getIndexName() {
        return indexName;
    }

    public void setIndexName(SQLExpr indexName) {
        this.indexName = indexName;
    }

    public boolean isShowIndexPartMeta() {
        return showIndexPartMeta;
    }

    public void setShowIndexPartMeta(boolean showIndexPartMeta) {
        this.showIndexPartMeta = showIndexPartMeta;
    }

    public SQLShowPartitionsStmt() {
        super(DbType.mysql);
    }

    public SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExpr table) {
        if (table == null) {
            return;
        }

        setTableSource(new SQLExprTableSource(table));
    }

    public void setTableSource(SQLExprTableSource tableSource) {
        this.tableSource = tableSource;
    }

    public List<SQLAssignItem> getPartition() {
        return partition;
    }

    public SQLExpr getWhere() {
        return where;
    }

    public void setWhere(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.where = x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, partition);
            acceptChild(visitor, where);
        }
        visitor.endVisit(this);
    }

    public boolean isShowPartMeta() {
        return showPartMeta;
    }

    public void setShowPartMeta(boolean showPartMeta) {
        this.showPartMeta = showPartMeta;
    }
}
