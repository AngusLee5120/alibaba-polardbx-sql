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
package com.alibaba.polardbx.parser;

import com.alibaba.polardbx.druid.sql.SQLUtils;
import com.alibaba.polardbx.druid.sql.ast.SQLStatement;
import com.alibaba.polardbx.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.polardbx.druid.sql.parser.Token;
import junit.framework.TestCase;
import org.junit.Assert;

public class MTSParserTest extends TestCase {

    public void test_mts_0() throws Exception {
        String sql = "  savepoint xx";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SAVEPOINT xx", output);
    }

    public void test_mts_1() throws Exception {
        String sql = "  savepoint SAVEPOINT";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SAVEPOINT SAVEPOINT", output);
    }

    public void test_mts_2() throws Exception {
        String sql = "  savepoInt `select`";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SAVEPOINT `select`", output);
    }

    public void test_mts_3() throws Exception {
        String sql = "Release sAVEPOINT xx   ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("RELEASE SAVEPOINT xx", output);
    }

    public void test_rollback_0() throws Exception {
        String sql = "rollBack to x1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ROLLBACK TO x1", output);
    }

    public void test_rollback_1() throws Exception {
        String sql = "rollBack work to x1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ROLLBACK TO x1", output);
    }

    public void test_rollback_2() throws Exception {
        String sql = "rollBack work to savepoint x1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ROLLBACK TO x1", output);
    }

    public void test_lockTable() throws Exception {
        String sql = "LOCK TABLES t1 READ;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("LOCK TABLES t1 READ;", output);
    }

    public void test_lockTable_1() throws Exception {
        String sql = "LOCK TABLES t2 READ LOCAL;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("LOCK TABLES t2 READ LOCAL;", output);
    }

    public void test_lockTable_2() throws Exception {
        String sql = "LOCK TABLES tddl5_users as t LOW_PRIORITY write";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("LOCK TABLES tddl5_users t LOW_PRIORITY WRITE", output);
    }

    public void test_lockTable_3() throws Exception {
        String sql = "LOCK TABLES tddl5_users as t1, table3 as t2 LOW_PRIORITY write";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("LOCK TABLES tddl5_users t1, table3 t2 LOW_PRIORITY WRITE", output);
    }

    public void test_lockTable_4() throws Exception {
        String sql = "LOCK TABLES tddl5_users as t1 read, table3 as t2 LOW_PRIORITY write";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("LOCK TABLES tddl5_users t1 READ, table3 t2 LOW_PRIORITY WRITE", output);
    }

    public void test_unlockTable() throws Exception {
        String sql = "UNLOCK TABLES";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("UNLOCK TABLES", output);
    }
}
