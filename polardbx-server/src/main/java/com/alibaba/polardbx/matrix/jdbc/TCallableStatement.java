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

package com.alibaba.polardbx.matrix.jdbc;

import com.alibaba.polardbx.druid.sql.parser.ByteString;
import com.alibaba.polardbx.common.exception.NotSupportException;
import com.alibaba.polardbx.common.jdbc.Parameters;
import com.alibaba.polardbx.optimizer.context.ExecutionContext;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * 支持下存储过程的调用
 *
 * @author jianghang 2014-3-19 下午3:52:08
 * @since 5.0.2
 */
public class TCallableStatement extends TPreparedStatement implements CallableStatement {

    // 连接到 Default Group 的 CallableStatement
    private CallableStatement targetStatement;

    public TCallableStatement(TDataSource ds, TConnection conn, String sql, ExecutionContext ec,
                              CallableStatement targetStatement) {
        // NOTE: `ByteString.from(sql)` is bad, but CallableStatement is not supported in PolarDB-X now
        super(ds, conn, ByteString.from(sql), ec);
        this.targetStatement = targetStatement;
    }

    @Override
    public boolean execute() throws SQLException {
        // 不支持batch
        Parameters.setParameters(targetStatement, params.getCurrentParameter());
        return ((CallableStatement) targetStatement).execute();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        Parameters.setParameters(targetStatement, params.getCurrentParameter());
        return ((CallableStatement) targetStatement).executeQuery();
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException {
        Parameters.setParameters(targetStatement, params.getCurrentParameter());
        return ((CallableStatement) targetStatement).executeUpdate();
    }

    @Override
    protected boolean executeInternal(ByteString sql, int autoGeneratedKeys, int[] columnIndexes, String[] columnNames)
        throws SQLException {
        return execute();
    }

    @Override
    protected int executeUpdateInternal(ByteString sql, int autoGeneratedKeys, int[] columnIndexes,
                                        String[] columnNames)
        throws SQLException {
        return executeUpdate();
    }

    public Array getArray(int i) throws SQLException {
        return ((CallableStatement) targetStatement).getArray(i);
    }

    public Array getArray(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getArray(parameterName);
    }

    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getBigDecimal(parameterIndex);
    }

    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getBigDecimal(parameterName);
    }

    @Deprecated
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        return ((CallableStatement) targetStatement).getBigDecimal(parameterIndex, scale);
    }

    public Blob getBlob(int i) throws SQLException {
        return ((CallableStatement) targetStatement).getBlob(i);
    }

    public Blob getBlob(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getBlob(parameterName);
    }

    public boolean getBoolean(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getBoolean(parameterIndex);
    }

    public boolean getBoolean(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getBoolean(parameterName);
    }

    public byte getByte(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getByte(parameterIndex);
    }

    public byte getByte(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getByte(parameterName);
    }

    public byte[] getBytes(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getBytes(parameterIndex);
    }

    public byte[] getBytes(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getBytes(parameterName);
    }

    public Clob getClob(int i) throws SQLException {
        return ((CallableStatement) targetStatement).getClob(i);
    }

    public Clob getClob(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getClob(parameterName);
    }

    public Date getDate(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getDate(parameterIndex);
    }

    public Date getDate(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getDate(parameterName);
    }

    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        return ((CallableStatement) targetStatement).getDate(parameterIndex, cal);
    }

    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        return ((CallableStatement) targetStatement).getDate(parameterName, cal);
    }

    public double getDouble(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getDouble(parameterIndex);
    }

    public double getDouble(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getDouble(parameterName);
    }

    public float getFloat(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getFloat(parameterIndex);
    }

    public float getFloat(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getFloat(parameterName);
    }

    public int getInt(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getInt(parameterIndex);
    }

    public int getInt(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getInt(parameterName);
    }

    public long getLong(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getLong(parameterIndex);
    }

    public long getLong(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getLong(parameterName);
    }

    public Object getObject(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getObject(parameterIndex);
    }

    public Object getObject(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getObject(parameterName);
    }

    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        return ((CallableStatement) targetStatement).getObject(i, map);
    }

    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        return ((CallableStatement) targetStatement).getObject(parameterName, map);
    }

    public Ref getRef(int i) throws SQLException {
        return ((CallableStatement) targetStatement).getRef(i);
    }

    public Ref getRef(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getRef(parameterName);
    }

    public short getShort(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getShort(parameterIndex);
    }

    public short getShort(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getShort(parameterName);
    }

    public String getString(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getString(parameterIndex);
    }

    public String getString(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getString(parameterName);
    }

    public Time getTime(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getTime(parameterIndex);
    }

    public Time getTime(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getTime(parameterName);
    }

    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        return ((CallableStatement) targetStatement).getTime(parameterIndex, cal);
    }

    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        return ((CallableStatement) targetStatement).getTime(parameterName, cal);
    }

    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getTimestamp(parameterIndex);
    }

    public Timestamp getTimestamp(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getTimestamp(parameterName);
    }

    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        return ((CallableStatement) targetStatement).getTimestamp(parameterIndex, cal);
    }

    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        return ((CallableStatement) targetStatement).getTimestamp(parameterName, cal);
    }

    public URL getURL(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getURL(parameterIndex);
    }

    public URL getURL(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getURL(parameterName);
    }

    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        ((CallableStatement) targetStatement).registerOutParameter(parameterIndex, sqlType);
    }

    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        ((CallableStatement) targetStatement).registerOutParameter(parameterName, sqlType);
    }

    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        ((CallableStatement) targetStatement).registerOutParameter(parameterIndex, sqlType, scale);
    }

    public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
        ((CallableStatement) targetStatement).registerOutParameter(paramIndex, sqlType, typeName);
    }

    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        ((CallableStatement) targetStatement).registerOutParameter(parameterName, sqlType, scale);
    }

    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        ((CallableStatement) targetStatement).registerOutParameter(parameterName, sqlType, typeName);
    }

    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        ((CallableStatement) targetStatement).setAsciiStream(parameterName, x, length);
    }

    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        ((CallableStatement) targetStatement).setBigDecimal(parameterName, x);
    }

    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        ((CallableStatement) targetStatement).setBinaryStream(parameterName, x, length);
    }

    public void setBoolean(String parameterName, boolean x) throws SQLException {
        ((CallableStatement) targetStatement).setBoolean(parameterName, x);
    }

    public void setByte(String parameterName, byte x) throws SQLException {
        ((CallableStatement) targetStatement).setByte(parameterName, x);
    }

    public void setBytes(String parameterName, byte[] x) throws SQLException {
        ((CallableStatement) targetStatement).setBytes(parameterName, x);
    }

    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        ((CallableStatement) targetStatement).setCharacterStream(parameterName, reader, length);
    }

    public void setDate(String parameterName, Date x) throws SQLException {
        ((CallableStatement) targetStatement).setDate(parameterName, x);
    }

    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        ((CallableStatement) targetStatement).setDate(parameterName, x, cal);
    }

    public void setDouble(String parameterName, double x) throws SQLException {
        ((CallableStatement) targetStatement).setDouble(parameterName, x);
    }

    public void setFloat(String parameterName, float x) throws SQLException {
        ((CallableStatement) targetStatement).setFloat(parameterName, x);
    }

    public void setInt(String parameterName, int x) throws SQLException {
        ((CallableStatement) targetStatement).setInt(parameterName, x);
    }

    public void setLong(String parameterName, long x) throws SQLException {
        ((CallableStatement) targetStatement).setLong(parameterName, x);
    }

    public void setNull(String parameterName, int sqlType) throws SQLException {
        ((CallableStatement) targetStatement).setNull(parameterName, sqlType);
    }

    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        ((CallableStatement) targetStatement).setNull(parameterName, sqlType, typeName);
    }

    public void setObject(String parameterName, Object x) throws SQLException {
        ((CallableStatement) targetStatement).setObject(parameterName, x);
    }

    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        ((CallableStatement) targetStatement).setObject(parameterName, x, targetSqlType);
    }

    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        ((CallableStatement) targetStatement).setObject(parameterName, x, targetSqlType);
    }

    public void setShort(String parameterName, short x) throws SQLException {
        ((CallableStatement) targetStatement).setShort(parameterName, x);
    }

    public void setString(String parameterName, String x) throws SQLException {
        ((CallableStatement) targetStatement).setString(parameterName, x);
    }

    public void setTime(String parameterName, Time x) throws SQLException {
        ((CallableStatement) targetStatement).setTime(parameterName, x);
    }

    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        ((CallableStatement) targetStatement).setTime(parameterName, x, cal);
    }

    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        ((CallableStatement) targetStatement).setTimestamp(parameterName, x);
    }

    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        ((CallableStatement) targetStatement).setTimestamp(parameterName, x, cal);
    }

    public void setURL(String parameterName, URL val) throws SQLException {
        ((CallableStatement) targetStatement).setURL(parameterName, val);
    }

    public boolean wasNull() throws SQLException {
        return ((CallableStatement) targetStatement).wasNull();
    }

    public RowId getRowId(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getRowId(parameterIndex);
    }

    public RowId getRowId(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getRowId(parameterName);
    }

    public void setRowId(String parameterName, RowId x) throws SQLException {
        ((CallableStatement) targetStatement).setRowId(parameterName, x);
    }

    public void setNString(String parameterName, String value) throws SQLException {
        ((CallableStatement) targetStatement).setNString(parameterName, value);
    }

    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        ((CallableStatement) targetStatement).setNCharacterStream(parameterName, value, length);
    }

    public void setNClob(String parameterName, NClob value) throws SQLException {
        ((CallableStatement) targetStatement).setNClob(parameterName, value);
    }

    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        ((CallableStatement) targetStatement).setClob(parameterName, reader, length);
    }

    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        ((CallableStatement) targetStatement).setBlob(parameterName, inputStream);
    }

    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        ((CallableStatement) targetStatement).setNClob(parameterName, reader, length);
    }

    public NClob getNClob(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getNClob(parameterIndex);
    }

    public NClob getNClob(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getNClob(parameterName);
    }

    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        ((CallableStatement) targetStatement).setSQLXML(parameterName, xmlObject);
    }

    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getSQLXML(parameterIndex);
    }

    public SQLXML getSQLXML(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getSQLXML(parameterName);
    }

    public String getNString(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getNString(parameterIndex);
    }

    public String getNString(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getNString(parameterName);
    }

    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getNCharacterStream(parameterIndex);
    }

    public Reader getNCharacterStream(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getNCharacterStream(parameterName);
    }

    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        return ((CallableStatement) targetStatement).getCharacterStream(parameterIndex);
    }

    public Reader getCharacterStream(String parameterName) throws SQLException {
        return ((CallableStatement) targetStatement).getCharacterStream(parameterName);
    }

    public void setBlob(String parameterName, Blob x) throws SQLException {
        ((CallableStatement) targetStatement).setBlob(parameterName, x);
    }

    public void setClob(String parameterName, Clob x) throws SQLException {
        ((CallableStatement) targetStatement).setClob(parameterName, x);
    }

    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        ((CallableStatement) targetStatement).setAsciiStream(parameterName, x, length);
    }

    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        ((CallableStatement) targetStatement).setBinaryStream(parameterName, x, length);
    }

    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        ((CallableStatement) targetStatement).setCharacterStream(parameterName, reader, length);
    }

    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        ((CallableStatement) targetStatement).setAsciiStream(parameterName, x);
    }

    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        ((CallableStatement) targetStatement).setBinaryStream(parameterName, x);
    }

    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        ((CallableStatement) targetStatement).setCharacterStream(parameterName, reader);
    }

    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        ((CallableStatement) targetStatement).setNCharacterStream(parameterName, value);
    }

    public void setClob(String parameterName, Reader reader) throws SQLException {
        ((CallableStatement) targetStatement).setClob(parameterName, reader);
    }

    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        ((CallableStatement) targetStatement).setBlob(parameterName, inputStream);
    }

    public void setNClob(String parameterName, Reader reader) throws SQLException {
        ((CallableStatement) targetStatement).setNClob(parameterName, reader);
    }

    public void closeOnCompletion() throws SQLException {
        // ((CallableStatement) targetStatement).closeOnCompletion();
        throw new NotSupportException("closeOnCompletion");
    }

    public boolean isCloseOnCompletion() throws SQLException {
        // return ((CallableStatement) targetStatement).isCloseOnCompletion();
        throw new NotSupportException("isCloseOnCompletion");
    }

    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        // return ((CallableStatement)
        // targetStatement).getObject(parameterIndex, type);
        throw new NotSupportException("getObject");
    }

    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        // return ((CallableStatement) targetStatement).getObject(parameterName,
        // type);
        throw new NotSupportException("getObject");
    }
}