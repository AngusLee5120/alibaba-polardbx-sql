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

package com.alibaba.polardbx.executor.pl.type;

import com.alibaba.polardbx.druid.sql.ast.SQLDataType;
import org.apache.calcite.sql.type.SqlTypeName;

public class MediumBlobTypeBuilder extends AbstractBasicSqlTypeBuilder {
    public static MediumBlobTypeBuilder INSTANCE = new MediumBlobTypeBuilder();

    private MediumBlobTypeBuilder() {
    }

    @Override
    int getPrecision(SQLDataType dataType) {
        return 16777215;
    }

    @Override
    SqlTypeName getTypeName(SQLDataType dataType) {
        return SqlTypeName.BLOB;
    }
}
