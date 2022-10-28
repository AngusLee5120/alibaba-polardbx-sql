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

package com.alibaba.polardbx.optimizer.config;

import com.alibaba.polardbx.common.utils.Assert;
import com.alibaba.polardbx.optimizer.config.table.statistic.MockStatisticDatasource;
import com.alibaba.polardbx.optimizer.config.table.statistic.StatisticManager;
import com.alibaba.polardbx.optimizer.config.table.statistic.TopN;
import com.alibaba.polardbx.optimizer.config.table.statistic.inf.StatisticService;
import com.alibaba.polardbx.optimizer.core.datatype.IntegerType;
import com.alibaba.polardbx.optimizer.core.function.calc.scalar.filter.Row;
import com.google.common.collect.Lists;
import org.junit.Test;

public class StatisticTest {
    @Test
    public void testTopNSmallSampleSize() {
        TopN topN = prepareTopN();
        topN.build(15, 3);
        if (!"{\"countArr\":[3766,156,69,342,106,267,291,62,85,117,77,277,98,187],\"valueArr\":[22,58,307,534,2357,2525,4286,5018,5404,6077,6452,7821,8433,8610],\"type\":\"Int\",\"sampleRate\":0.2}"
            .equalsIgnoreCase(TopN.serializeToJson(topN))) {
            Assert.fail(TopN.serializeToJson(topN));
        }
    }

    @Test
    public void testTopNEqualSampleSize() {
        TopN topN = prepareTopN();
        topN.build(334, 3);
        if (!"{\"countArr\":[3766,5,49,156,13,6,4,5,4,18,36,6,7,6,22,69,4,23,25,4,8,21,25,16,8,6,4,14,16,16,342,5,61,14,11,36,5,6,6,39,53,10,19,4,6,4,17,13,18,27,26,29,6,5,4,4,5,14,6,20,13,6,22,17,6,4,4,5,6,4,4,9,4,7,5,5,14,106,9,7,13,28,4,267,12,58,4,12,5,13,7,5,29,4,11,13,5,27,34,40,34,4,14,7,4,19,14,12,8,24,7,7,12,4,13,4,5,7,9,13,5,6,6,20,5,16,24,14,6,5,10,55,45,6,4,8,8,6,5,19,5,6,6,291,7,6,11,21,47,4,14,47,42,9,8,6,6,5,17,4,6,4,6,52,21,9,12,6,5,4,62,30,29,6,10,44,17,11,85,26,4,6,4,7,30,6,6,21,4,117,31,4,4,8,28,11,8,4,6,77,6,5,4,6,16,5,13,38,21,5,13,6,6,5,7,4,11,4,4,4,48,23,4,4,277,13,7,13,5,13,98,10,9,4,187,21,25,52,24,13,4,14,9,18,12,5,7,13,5,9,6,28,8,5,9,10],\"valueArr\":[22,35,39,58,107,142,148,157,166,178,179,210,223,280,305,307,323,344,345,350,376,407,411,451,452,466,485,493,516,522,534,578,612,683,708,738,838,851,869,935,976,993,1014,1275,1308,1326,1327,1426,1521,1544,1567,1574,1633,1635,1676,1734,1735,1741,1765,1774,1780,1804,1868,1869,1892,1933,1940,1947,1953,1985,1999,2042,2207,2220,2223,2250,2263,2357,2360,2458,2490,2496,2503,2525,2539,2554,2555,2562,2687,2723,2737,2774,2794,2820,2823,2872,2905,2913,2961,2962,3000,3007,3016,3098,3133,3139,3160,3220,3318,3398,3442,3443,3461,3475,3482,3506,3587,3669,3687,3701,3707,3739,3754,3777,3778,3805,3806,3823,3847,3879,3935,3943,3953,3995,4009,4037,4067,4083,4116,4153,4248,4258,4284,4286,4303,4329,4350,4375,4382,4393,4431,4438,4475,4549,4560,4593,4598,4604,4669,4680,4749,4802,4816,4838,4857,4904,4911,4919,4924,5015,5018,5025,5030,5076,5107,5184,5194,5257,5404,5502,5555,5628,5642,5671,5734,5966,5996,6019,6025,6077,6110,6199,6221,6261,6276,6291,6316,6400,6401,6452,6454,6466,6483,6490,6589,6600,6718,6720,6724,6744,6872,6930,6990,7061,7073,7090,7210,7300,7314,7371,7498,7503,7574,7698,7821,8020,8093,8116,8345,8426,8433,8451,8517,8527,8610,8611,8655,8973,8982,9064,9133,9157,9223,9251,9363,9404,9488,9563,9733,9920,9925,10189,10199,10263,10275,10510],\"type\":\"Int\",\"sampleRate\":0.2}"
            .equalsIgnoreCase(TopN.serializeToJson(topN))) {
            Assert.fail(TopN.serializeToJson(topN));
        }
    }

    @Test
    public void testTopNEqualSampleSize1() {
        TopN topN = prepareTopN();
        topN.build(299, 3);
        if (!"{\"countArr\":[3766,5,49,156,13,6,4,5,4,18,36,6,7,6,22,69,4,23,25,4,8,21,25,16,8,6,4,14,16,16,342,5,61,14,11,36,5,6,6,39,53,10,19,4,6,4,17,13,18,27,26,29,6,5,4,4,5,14,6,20,13,6,22,17,6,4,4,5,6,4,4,9,4,7,5,5,14,106,9,7,13,28,4,267,12,58,4,12,5,13,7,5,29,4,11,13,5,27,34,40,34,4,14,7,4,19,14,12,8,24,7,7,12,4,13,4,5,7,9,13,5,6,6,20,5,16,24,14,6,5,10,55,45,6,4,8,8,6,5,19,5,6,6,291,7,6,11,21,47,4,14,47,42,9,8,6,6,5,17,4,6,4,6,52,21,9,12,6,5,4,62,30,29,6,10,44,17,11,85,26,4,6,4,7,30,6,6,21,4,117,31,4,4,8,28,11,8,4,6,77,6,5,4,6,16,5,13,38,21,5,13,6,6,5,7,4,11,4,4,4,48,23,4,4,277,13,7,13,5,13,98,10,9,4,187,21,25,52,24,13,4,14,9,18,12,5,7,13,5,9,6,28,8,5,9,10],\"valueArr\":[22,35,39,58,107,142,148,157,166,178,179,210,223,280,305,307,323,344,345,350,376,407,411,451,452,466,485,493,516,522,534,578,612,683,708,738,838,851,869,935,976,993,1014,1275,1308,1326,1327,1426,1521,1544,1567,1574,1633,1635,1676,1734,1735,1741,1765,1774,1780,1804,1868,1869,1892,1933,1940,1947,1953,1985,1999,2042,2207,2220,2223,2250,2263,2357,2360,2458,2490,2496,2503,2525,2539,2554,2555,2562,2687,2723,2737,2774,2794,2820,2823,2872,2905,2913,2961,2962,3000,3007,3016,3098,3133,3139,3160,3220,3318,3398,3442,3443,3461,3475,3482,3506,3587,3669,3687,3701,3707,3739,3754,3777,3778,3805,3806,3823,3847,3879,3935,3943,3953,3995,4009,4037,4067,4083,4116,4153,4248,4258,4284,4286,4303,4329,4350,4375,4382,4393,4431,4438,4475,4549,4560,4593,4598,4604,4669,4680,4749,4802,4816,4838,4857,4904,4911,4919,4924,5015,5018,5025,5030,5076,5107,5184,5194,5257,5404,5502,5555,5628,5642,5671,5734,5966,5996,6019,6025,6077,6110,6199,6221,6261,6276,6291,6316,6400,6401,6452,6454,6466,6483,6490,6589,6600,6718,6720,6724,6744,6872,6930,6990,7061,7073,7090,7210,7300,7314,7371,7498,7503,7574,7698,7821,8020,8093,8116,8345,8426,8433,8451,8517,8527,8610,8611,8655,8973,8982,9064,9133,9157,9223,9251,9363,9404,9488,9563,9733,9920,9925,10189,10199,10263,10275,10510],\"type\":\"Int\",\"sampleRate\":0.2}"
            .equalsIgnoreCase(TopN.serializeToJson(topN))) {
            Assert.fail(TopN.serializeToJson(topN));
        }
    }

    @Test
    public void testTopNGreaterThanSampleSize() {
        TopN topN = prepareTopN();
        topN.build(500, 3);
        if (!"{\"countArr\":[3766,5,49,3,156,13,3,3,3,6,4,5,4,18,36,6,7,6,3,22,69,3,4,23,25,4,3,8,21,25,16,8,3,6,4,14,16,3,16,342,3,5,61,14,11,3,36,3,5,6,3,6,3,39,53,10,19,3,4,3,6,4,17,3,3,13,3,3,3,18,27,26,29,3,6,5,3,3,4,4,5,14,6,20,13,6,22,17,6,4,3,4,5,6,4,4,9,3,3,4,7,5,3,5,14,106,9,7,13,28,4,3,3,267,12,58,4,12,3,5,13,7,5,29,4,11,13,5,27,34,40,34,3,4,14,3,7,4,19,14,3,12,3,8,3,24,7,7,12,4,13,4,5,7,3,9,13,5,6,6,20,5,16,24,14,6,5,3,3,10,55,45,6,4,8,8,6,5,3,3,19,3,5,6,6,291,7,3,6,11,3,21,47,4,14,47,42,3,9,8,6,6,5,3,17,4,6,3,3,4,6,52,21,9,12,6,5,4,62,30,29,3,6,10,3,44,17,11,3,85,3,26,4,3,6,4,7,30,3,6,3,3,6,3,3,21,4,117,31,4,4,3,8,28,11,8,3,3,4,6,77,6,5,4,6,3,16,5,3,13,38,21,3,5,13,3,6,3,6,3,5,7,4,11,4,4,4,48,23,3,4,4,277,3,13,3,7,13,5,13,98,10,9,4,187,21,3,25,52,24,13,3,4,3,3,14,9,18,3,12,5,3,7,13,5,3,9,6,28,8,3,5,9,10],\"valueArr\":[22,35,39,52,58,107,112,129,140,142,148,157,166,178,179,210,223,280,291,305,307,317,323,344,345,350,362,376,407,411,451,452,464,466,485,493,516,518,522,534,573,578,612,683,708,733,738,764,838,851,857,869,873,935,976,993,1014,1266,1275,1282,1308,1326,1327,1382,1412,1426,1470,1478,1499,1521,1544,1567,1574,1614,1633,1635,1661,1668,1676,1734,1735,1741,1765,1774,1780,1804,1868,1869,1892,1933,1936,1940,1947,1953,1985,1999,2042,2097,2183,2207,2220,2223,2243,2250,2263,2357,2360,2458,2490,2496,2503,2509,2511,2525,2539,2554,2555,2562,2581,2687,2723,2737,2774,2794,2820,2823,2872,2905,2913,2961,2962,3000,3006,3007,3016,3026,3098,3133,3139,3160,3167,3220,3309,3318,3378,3398,3442,3443,3461,3475,3482,3506,3587,3669,3686,3687,3701,3707,3739,3754,3777,3778,3805,3806,3823,3847,3879,3910,3925,3935,3943,3953,3995,4009,4037,4067,4083,4116,4134,4146,4153,4236,4248,4258,4284,4286,4303,4325,4329,4350,4363,4375,4382,4393,4431,4438,4475,4493,4549,4560,4593,4598,4604,4625,4669,4680,4749,4752,4791,4802,4816,4838,4857,4904,4911,4919,4924,5015,5018,5025,5030,5038,5076,5107,5124,5184,5194,5257,5263,5404,5428,5502,5555,5602,5628,5642,5671,5734,5961,5966,5967,5984,5996,6000,6018,6019,6025,6077,6110,6199,6221,6222,6261,6276,6291,6316,6333,6343,6400,6401,6452,6454,6466,6483,6490,6567,6589,6600,6705,6718,6720,6724,6743,6744,6872,6912,6930,6931,6990,7038,7061,7073,7090,7210,7300,7314,7371,7498,7503,7508,7574,7698,7821,7860,8020,8081,8093,8116,8345,8426,8433,8451,8517,8527,8610,8611,8650,8655,8973,8982,9064,9119,9133,9134,9147,9157,9223,9251,9262,9363,9404,9470,9488,9563,9733,9913,9920,9925,10189,10199,10249,10263,10275,10510],\"type\":\"Int\",\"sampleRate\":0.2}"
            .equalsIgnoreCase(TopN.serializeToJson(topN))) {
            Assert.fail(TopN.serializeToJson(topN));
        }
    }

    public TopN prepareTopN() {
        int[] countArr = {
            3766, 5, 49, 3, 156, 13, 3, 3, 3, 6, 4, 5, 4, 18, 36, 6, 7, 6, 3, 22, 69, 3, 4, 23, 25, 4, 3, 8, 21, 25, 16,
            8, 3, 6, 4, 14, 16, 3, 16, 342, 3, 5, 61, 14, 11, 3, 36, 3, 5, 6, 3, 6, 3, 39, 53, 10, 19, 3, 4, 3, 6, 4,
            17, 3, 3, 13, 3, 3, 3, 18, 27, 26, 29, 3, 6, 5, 3, 3, 4, 4, 5, 14, 6, 20, 13, 6, 22, 17, 6, 4, 3, 4, 5, 6,
            4, 4, 9, 3, 3, 4, 7, 5, 3, 5, 14, 106, 9, 7, 13, 28, 4, 3, 3, 267, 12, 58, 4, 12, 3, 5, 13, 7, 5, 29, 4, 11,
            13, 5, 27, 34, 40, 34, 3, 4, 14, 3, 7, 4, 19, 14, 3, 12, 3, 8, 3, 24, 7, 7, 12, 4, 13, 4, 5, 7, 3, 9, 13, 5,
            6, 6, 20, 5, 16, 24, 14, 6, 5, 3, 3, 10, 55, 45, 6, 4, 8, 8, 6, 5, 3, 3, 19, 3, 5, 6, 6, 291, 7, 3, 6, 11,
            3, 21, 47, 4, 14, 47, 42, 3, 9, 8, 6, 6, 5, 3, 17, 4, 6, 3, 3, 4, 6, 52, 21, 9, 12, 6, 5, 4, 62, 30, 29, 3,
            6, 10, 3, 44, 17, 11, 3, 85, 3, 26, 4, 3, 6, 4, 7, 30, 3, 6, 3, 3, 6, 3, 3, 21, 4, 117, 31, 4, 4, 3, 8, 28,
            11, 8, 3, 3, 4, 6, 77, 6, 5, 4, 6, 3, 16, 5, 3, 13, 38, 21, 3, 5, 13, 3, 6, 3, 6, 3, 5, 7, 4, 11, 4, 4, 4,
            48, 23, 3, 4, 4, 277, 3, 13, 3, 7, 13, 5, 13, 98, 10, 9, 4, 187, 21, 3, 25, 52, 24, 13, 3, 4, 3, 3, 14, 9,
            18, 3, 12, 5, 3, 7, 13, 5, 3, 9, 6, 28, 8, 3, 5, 9, 10};
        int[] valueArr = {
            22, 35, 39, 52, 58, 107, 112, 129, 140, 142, 148, 157, 166, 178, 179, 210, 223, 280, 291, 305, 307, 317,
            323, 344, 345, 350, 362, 376, 407, 411, 451, 452, 464, 466, 485, 493, 516, 518, 522, 534, 573, 578, 612,
            683, 708, 733, 738, 764, 838, 851, 857, 869, 873, 935, 976, 993, 1014, 1266, 1275, 1282, 1308, 1326, 1327,
            1382, 1412, 1426, 1470, 1478, 1499, 1521, 1544, 1567, 1574, 1614, 1633, 1635, 1661, 1668, 1676, 1734, 1735,
            1741, 1765, 1774, 1780, 1804, 1868, 1869, 1892, 1933, 1936, 1940, 1947, 1953, 1985, 1999, 2042, 2097, 2183,
            2207, 2220, 2223, 2243, 2250, 2263, 2357, 2360, 2458, 2490, 2496, 2503, 2509, 2511, 2525, 2539, 2554, 2555,
            2562, 2581, 2687, 2723, 2737, 2774, 2794, 2820, 2823, 2872, 2905, 2913, 2961, 2962, 3000, 3006, 3007, 3016,
            3026, 3098, 3133, 3139, 3160, 3167, 3220, 3309, 3318, 3378, 3398, 3442, 3443, 3461, 3475, 3482, 3506, 3587,
            3669, 3686, 3687, 3701, 3707, 3739, 3754, 3777, 3778, 3805, 3806, 3823, 3847, 3879, 3910, 3925, 3935, 3943,
            3953, 3995, 4009, 4037, 4067, 4083, 4116, 4134, 4146, 4153, 4236, 4248, 4258, 4284, 4286, 4303, 4325, 4329,
            4350, 4363, 4375, 4382, 4393, 4431, 4438, 4475, 4493, 4549, 4560, 4593, 4598, 4604, 4625, 4669, 4680, 4749,
            4752, 4791, 4802, 4816, 4838, 4857, 4904, 4911, 4919, 4924, 5015, 5018, 5025, 5030, 5038, 5076, 5107, 5124,
            5184, 5194, 5257, 5263, 5404, 5428, 5502, 5555, 5602, 5628, 5642, 5671, 5734, 5961, 5966, 5967, 5984, 5996,
            6000, 6018, 6019, 6025, 6077, 6110, 6199, 6221, 6222, 6261, 6276, 6291, 6316, 6333, 6343, 6400, 6401, 6452,
            6454, 6466, 6483, 6490, 6567, 6589, 6600, 6705, 6718, 6720, 6724, 6743, 6744, 6872, 6912, 6930, 6931, 6990,
            7038, 7061, 7073, 7090, 7210, 7300, 7314, 7371, 7498, 7503, 7508, 7574, 7698, 7821, 7860, 8020, 8081, 8093,
            8116, 8345, 8426, 8433, 8451, 8517, 8527, 8610, 8611, 8650, 8655, 8973, 8982, 9064, 9119, 9133, 9134, 9147,
            9157, 9223, 9251, 9262, 9363, 9404, 9470, 9488, 9563, 9733, 9913, 9920, 9925, 10189, 10199, 10249, 10263,
            10275, 10510};
        TopN topN = new TopN(new IntegerType(), 0.2D);
        for (int i = 0; i < valueArr.length; i++) {
            for (int j = 0; j < countArr[i]; j++) {
                topN.offer(valueArr[i]);
            }
        }
        return topN;
    }
}
