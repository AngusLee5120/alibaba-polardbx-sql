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

package com.alibaba.polardbx.executor.calc.aggfunctions;

import com.alibaba.polardbx.executor.chunk.Block;
import com.alibaba.polardbx.optimizer.core.datatype.DataType;

public class Double2DoubleAvg extends SpecificType2DoubleAvgV2 {
    public Double2DoubleAvg(int index, boolean isDistict, DataType inputType, DataType outputType, int filterArg) {
        super(index, isDistict, inputType, outputType, filterArg);
    }

    @Override
    protected double getDouble(Block block, int position) {
        return block.getDouble(position);
    }
}