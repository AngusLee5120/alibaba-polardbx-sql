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

package com.alibaba.polardbx.executor.accumulator;

import com.alibaba.polardbx.executor.accumulator.state.NullableDoubleGroupState;
import com.alibaba.polardbx.executor.chunk.Block;
import com.alibaba.polardbx.executor.chunk.BlockBuilder;
import com.alibaba.polardbx.optimizer.core.datatype.DataType;
import com.alibaba.polardbx.optimizer.core.datatype.DataTypes;

public class DoubleSumAccumulator extends AbstractAccumulator {

    private static final DataType[] INPUT_TYPES = new DataType[] {DataTypes.DoubleType};

    private final NullableDoubleGroupState state;

    DoubleSumAccumulator(int capacity) {
        this.state = new NullableDoubleGroupState(capacity);
    }

    @Override
    public void appendInitValue() {
        state.appendNull();
    }

    @Override
    void accumulate(int groupId, Block block, int position) {
        if (block.isNull(position)) {
            return;
        }

        final double processValue = block.getDouble(position);
        if (state.isNull(groupId)) {
            state.set(groupId, processValue);
        } else {
            double beforeValue = state.get(groupId);
            double afterValue = beforeValue + processValue;
            state.set(groupId, afterValue);
        }
    }

    @Override
    public DataType[] getInputTypes() {
        return INPUT_TYPES;
    }

    @Override
    public void writeResultTo(int groupId, BlockBuilder bb) {
        if (state.isNull(groupId)) {
            bb.appendNull();
        } else {
            bb.writeDouble(state.get(groupId));
        }
    }

    @Override
    public long estimateSize() {
        return state.estimateSize();
    }
}

