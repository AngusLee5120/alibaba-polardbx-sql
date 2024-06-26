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

package org.apache.calcite.sql;

import com.alibaba.polardbx.common.utils.TStringUtil;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelDataTypeFieldImpl;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;

import java.util.List;

public class SqlCreateJavaFunction extends SqlDdl {

    private static final SqlSpecialOperator OPERATOR = new SqlCreateJavaFunctionOperator();

    final String funcName;
    protected String javaCode;
    protected String returnType;
    protected List<String> inputTypes;

    private String tableName;

    private boolean noState;
    public SqlCreateJavaFunction(SqlParserPos pos, String funcName,
                                 String returnType, List<String> inputTypes, String javaCode, boolean noState) {
        super(OPERATOR, pos);
        this.funcName = funcName;
        this.returnType = returnType;
        this.inputTypes = inputTypes;
        this.javaCode = javaCode;
        this.noState = noState;
        this.tableName = "_NONE_";
    }

    @Override
    public void unparse(SqlWriter writer, int lefPrec, int rightPrec) {
        writer.keyword("CREATE JAVA FUNCTION");

        writer.literal(funcName);

        if (noState) {
            writer.keyword("NO STATE");
        }

        if (TStringUtil.isNotBlank(returnType)) {
            writer.keyword("RETURN_TYPE");
            writer.literal(returnType);
        }
        if (inputTypes != null && !inputTypes.isEmpty()) {
            writer.keyword("INPUT_TYPES");
            for (int i = 0; i < inputTypes.size() - 1; i++) {
                writer.literal(inputTypes.get(i));
                writer.literal(",");
            }
            writer.literal(inputTypes.get(inputTypes.size() - 1));
        }

        if (TStringUtil.isNotBlank(javaCode)) {
            writer.keyword("CODE");
            writer.literal(javaCode);
            writer.keyword("END_CODE");
        }
    }

    public String getFuncName() {
        return funcName;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<String> getInputTypes() {
        return inputTypes;
    }

    public String getJavaCode() {
        return javaCode;
    }

    public String getTableName() {
        return tableName;
    }

    public boolean isNoState() {
        return noState;
    }

    @Override
    public List<SqlNode> getOperandList() {
        return ImmutableList.of();
    }

    public static class SqlCreateJavaFunctionOperator extends SqlSpecialOperator {

        public SqlCreateJavaFunctionOperator() {
            super("CREATE_JAVA_FUNCTION", SqlKind.CREATE_JAVA_FUNCTION);
        }

        @Override
        public RelDataType deriveType(SqlValidator validator, SqlValidatorScope scope, SqlCall call) {
            final RelDataTypeFactory typeFactory = validator.getTypeFactory();
            final RelDataType columnType = typeFactory.createSqlType(SqlTypeName.CHAR);

            return typeFactory.createStructType(
                ImmutableList.of((RelDataTypeField) new RelDataTypeFieldImpl("CREATE_JAVA_FUNCTION_RESULT",
                    0,
                    columnType)));
        }
    }
}
