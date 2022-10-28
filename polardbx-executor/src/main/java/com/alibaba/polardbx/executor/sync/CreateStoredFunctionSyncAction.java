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

package com.alibaba.polardbx.executor.sync;

import com.alibaba.polardbx.executor.cursor.ResultCursor;
import com.alibaba.polardbx.executor.pl.StoredFunctionManager;
import com.alibaba.polardbx.executor.pl.UdfUtils;

public class CreateStoredFunctionSyncAction implements ISyncAction {
    String functionName;
    String createFunction;
    boolean canPush;

    public CreateStoredFunctionSyncAction() {
    }

    public CreateStoredFunctionSyncAction(String functionName, String createFunction, boolean canPush) {
        this.functionName = functionName;
        this.createFunction = createFunction;
        this.canPush = canPush;
    }

    @Override
    public ResultCursor sync() {
        UdfUtils.registerSqlUdf(createFunction, canPush);
        StoredFunctionManager.getInstance().register(functionName);
        return null;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getCreateFunction() {
        return createFunction;
    }

    public void setCreateFunction(String createFunction) {
        this.createFunction = createFunction;
    }

    public boolean isCanPush() {
        return canPush;
    }

    public void setCanPush(boolean canPush) {
        this.canPush = canPush;
    }
}
