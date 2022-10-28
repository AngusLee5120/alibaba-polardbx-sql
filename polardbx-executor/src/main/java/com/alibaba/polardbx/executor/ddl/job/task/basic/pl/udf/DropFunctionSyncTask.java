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

package com.alibaba.polardbx.executor.ddl.job.task.basic.pl.udf;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.polardbx.common.TddlConstants;
import com.alibaba.polardbx.executor.ddl.job.task.BaseSyncTask;
import com.alibaba.polardbx.executor.ddl.job.task.util.TaskName;
import com.alibaba.polardbx.executor.sync.DropStoredFunctionSyncAction;
import com.alibaba.polardbx.executor.sync.SyncManagerHelper;
import com.alibaba.polardbx.gms.sync.SyncScope;
import com.alibaba.polardbx.optimizer.context.ExecutionContext;
import lombok.Getter;

@Getter
@TaskName(name = "DropFunctionSyncTask")
public class DropFunctionSyncTask extends BaseSyncTask {

    private String functionName;

    @JSONCreator
    public DropFunctionSyncTask(String schemaName, String functionName) {
        super(schemaName);
        this.functionName = functionName;
    }

    @Override
    protected void executeImpl(ExecutionContext executionContext) {
        SyncManagerHelper.sync(new DropStoredFunctionSyncAction(functionName),
            TddlConstants.INFORMATION_SCHEMA, SyncScope.ALL);
    }

}

