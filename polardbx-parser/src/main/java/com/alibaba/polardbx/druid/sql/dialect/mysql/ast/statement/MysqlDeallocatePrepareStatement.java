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
package com.alibaba.polardbx.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.polardbx.druid.sql.ast.SQLName;
import com.alibaba.polardbx.druid.sql.ast.SQLObject;
import com.alibaba.polardbx.druid.sql.ast.SqlType;
import com.alibaba.polardbx.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.Collections;
import java.util.List;

public class MysqlDeallocatePrepareStatement extends MySqlStatementImpl {

    private SQLName statementName;

    public SQLName getStatementName() {
        return statementName;
    }

    public void setStatementName(SQLName statementName) {
        this.statementName = statementName;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, statementName);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>singletonList(statementName);
    }

    @Override
    public SqlType getSqlType() {
        return null;
    }
}
