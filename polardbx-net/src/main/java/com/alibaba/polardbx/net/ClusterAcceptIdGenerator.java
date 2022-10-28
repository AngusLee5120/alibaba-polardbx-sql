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

package com.alibaba.polardbx.net;

import com.alibaba.polardbx.common.exception.TddlRuntimeException;
import com.alibaba.polardbx.gms.node.GmsNodeManager;

public class ClusterAcceptIdGenerator {

    private final Object lock1 = new Object();
    private long acceptId = 0;
    private static final long MAX_VALUE = 0xefffffffL;

    private final Object lock2 = new Object();
    private long acceptManageId = MAX_VALUE + 1;
    private static final long MANGER_MAX_VALUE = 0xffffffffL;

    private static final ClusterAcceptIdGenerator INSTANCE = new ClusterAcceptIdGenerator();

    private ClusterAcceptIdGenerator() {
    }

    public static ClusterAcceptIdGenerator getInstance() {
        return INSTANCE;
    }

    public long nextId() {
        int nodeIndex = 0;
        int nodeCount = 0;
        nodeCount = GmsNodeManager.getInstance().getAllNodes().size();
        nodeIndex = GmsNodeManager.getInstance().getCurrentIndex();
        if (nodeIndex == -1) {
            throw new TddlRuntimeException(com.alibaba.polardbx.common.exception.code.ErrorCode.ERR_CONFIG,
                "local node not found in nodes list");
        }

        if (nodeCount == 0) {
            nodeCount = 1;
        }

        synchronized (lock1) {
            if (acceptId * nodeCount + nodeIndex >= MAX_VALUE) {
                acceptId = 0L;
            }
            return (++acceptId) * nodeCount + nodeIndex;
        }
    }

    // ManagerConnection IDs are generated by AcceptIdGenerator,
    // ServerConnection IDs are generated by ClusterAcceptIdGenerator,
    // Both Manager Connections and Server Connections are put into
    // frontends(a map whose key is connection ID) of NIOProcessor.java.
    // So ManagerConnection ID should start from an offset to make sure no
    // Manager Connection ID would be same as ServerConnection ID
    public long nextManageId() {
        synchronized (lock2) {
            if (acceptManageId >= MANGER_MAX_VALUE) {
                acceptManageId = MAX_VALUE + 1;
            }
            return ++acceptManageId;
        }
    }

}
