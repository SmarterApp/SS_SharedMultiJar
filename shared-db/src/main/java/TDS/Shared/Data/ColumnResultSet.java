/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Data;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

import TDS.Shared.Exceptions.ReturnStatusException;
import AIR.Common.Helpers.Constants;

public class ColumnResultSet implements ResultSet {

	private ResultSet _resultSet = null;
	private boolean _fixNulls = false; // works for Boolean, Integer, Long,
	// Byte, Short, String, Date and UUID
	private boolean _fixMissing = false;
	private boolean _isBeforeFirst = true; // indicates whether cursor
	// positioned before the first
	// record
	private boolean _isStatus = false; // indicates that current record

	// information has been used for status
	// check

	public static ColumnResultSet getColumnResultSet(ResultSet reader)
			throws SQLException {
		// check if the reader is null
		if (reader == null)
			return null;

		return new ColumnResultSet(reader);
	}

	private ColumnResultSet(ResultSet rs) {
		this._resultSet = rs;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this._resultSet.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this._resultSet.isWrapperFor(iface);
	}

	@Override
	public boolean next() throws SQLException {
		// Advance the cursor only if it is before the first position
		// or previous advance was not associated with status read
		if (!_isStatus || _isBeforeFirst) {
			_isBeforeFirst = false;
			_isStatus = false;
			return this._resultSet.next();
		}

		_isStatus = false;
		return true;
	}

	@Override
	public void close() throws SQLException {
		this._resultSet.close();
	}

	@Override
	public boolean wasNull() throws SQLException {
		return this._resultSet.wasNull();
	}

	@Override
	public String getString(int columnIndex) throws SQLException {
		String sValue = this._resultSet.getString(columnIndex);
		if (this._fixNulls && sValue == null)
			return "";
		return sValue;
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		return this._resultSet.getBoolean(columnIndex);
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		return this._resultSet.getByte(columnIndex);
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		return this._resultSet.getShort(columnIndex);
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		return this._resultSet.getInt(columnIndex);
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		return this._resultSet.getLong(columnIndex);
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		return this._resultSet.getFloat(columnIndex);
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		return this._resultSet.getDouble(columnIndex);
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale)
			throws SQLException {
		return this._resultSet.getBigDecimal(columnIndex);
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		return this._resultSet.getBytes(columnIndex);
	}

	@Override
	public Date getDate(int columnIndex) throws SQLException {
		Date dValue = this._resultSet.getDate(columnIndex);
		if (this._fixNulls && dValue == null)
			return new Date(0);
		return dValue;
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		return this._resultSet.getTime(columnIndex);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return this._resultSet.getTimestamp(columnIndex);
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return this._resultSet.getAsciiStream(columnIndex);
	}

	@SuppressWarnings("deprecation")
	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return this._resultSet.getUnicodeStream(columnIndex);
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return this._resultSet.getBinaryStream(columnIndex);
	}

	@Override
	public String getString(String columnLabel) throws SQLException {
		String sValue = this._resultSet.getString(columnLabel);
		if (this._fixNulls && sValue == null)
			return "";
		return sValue;
	}

	public Boolean getBOOLEAN(String columnLabel) throws SQLException {
		boolean bValue = this._resultSet.getBoolean(columnLabel);
		// nulls are fixed by default, so return null explicitly
		if (!this._fixNulls && this._resultSet.wasNull())
			return null;
		return bValue;
	}

	@Override
	public boolean getBoolean(String columnLabel) throws SQLException {
		return this._resultSet.getBoolean(columnLabel);
	}

	public Byte getBYTE(String columnLabel) throws SQLException {
		byte bValue = this._resultSet.getByte(columnLabel);
		// nulls are fixed by default, so return null explicitly
		if (!this._fixNulls && this._resultSet.wasNull())
			return null;
		return bValue;
	}

	@Override
	public byte getByte(String columnLabel) throws SQLException {
		return this._resultSet.getByte(columnLabel);
	}

	public Short getSHORT(String columnLabel) throws SQLException {
		short sValue = this._resultSet.getShort(columnLabel);
		// nulls are fixed by default, so return null explicitly
		if (!this._fixNulls && this._resultSet.wasNull())
			return null;
		return sValue;
	}

	@Override
	public short getShort(String columnLabel) throws SQLException {
		return this._resultSet.getShort(columnLabel);
	}

	public Integer getINT(String columnLabel) throws SQLException {
		int iValue = this._resultSet.getInt(columnLabel);
		// nulls are fixed by default, so return null explicitly
		if (!this._fixNulls && this._resultSet.wasNull())
			return null;
		return iValue;
	}

	@Override
	public int getInt(String columnLabel) throws SQLException {
		return this._resultSet.getInt(columnLabel);
	}

	public Long getLONG(String columnLabel) throws SQLException {
		long lValue = this._resultSet.getLong(columnLabel);
		// nulls are fixed by default, so return null explicitly
		if (!this._fixNulls && this._resultSet.wasNull())
			return null;
		return lValue;
	}

	@Override
	public long getLong(String columnLabel) throws SQLException {
		return this._resultSet.getLong(columnLabel);
	}

	public Float getFLOAT(String columnLabel) throws SQLException {
		float fValue = this._resultSet.getFloat(columnLabel);
		// nulls are fixed by default, so return null explicitly
		if (!this._fixNulls && this._resultSet.wasNull())
			return null;
		return fValue;
	}

	@Override
	public float getFloat(String columnLabel) throws SQLException {
		return this._resultSet.getFloat(columnLabel);
	}

	public Double getDOUBLE(String columnLabel) throws SQLException {
		double dValue = this._resultSet.getDouble(columnLabel);
		// nulls are fixed by default, so return null explicitly
		if (!this._fixNulls && this._resultSet.wasNull())
			return null;
		return dValue;
	}

	@Override
	public double getDouble(String columnLabel) throws SQLException {
		return this._resultSet.getDouble(columnLabel);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BigDecimal getBigDecimal(String columnLabel, int scale)
			throws SQLException {
		return this._resultSet.getBigDecimal(columnLabel, scale);
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		return this._resultSet.getBytes(columnLabel);
	}

	@Override
	public Date getDate(String columnLabel) throws SQLException {
		Date dValue = this._resultSet.getDate(columnLabel);
		if (this._fixNulls && dValue == null)
			return new Date(0);
		return dValue;
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		return this._resultSet.getTime(columnLabel);
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return this._resultSet.getTimestamp(columnLabel);
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		return this._resultSet.getAsciiStream(columnLabel);
	}

	@SuppressWarnings("deprecation")
	@Override
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return this._resultSet.getUnicodeStream(columnLabel);
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		return this._resultSet.getBinaryStream(columnLabel);
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return this._resultSet.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		this._resultSet.clearWarnings();
	}

	@Override
	public String getCursorName() throws SQLException {
		return this._resultSet.getCursorName();
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return this._resultSet.getMetaData();
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		return this._resultSet.getObject(columnIndex);
	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		return this._resultSet.getObject(columnLabel);
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		return this._resultSet.findColumn(columnLabel);
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return this._resultSet.getCharacterStream(columnIndex);
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		return this._resultSet.getCharacterStream(columnLabel);
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return this._resultSet.getBigDecimal(columnIndex);
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return this._resultSet.getBigDecimal(columnLabel);
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		return this._resultSet.isBeforeFirst();
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		return this._resultSet.isAfterLast();
	}

	@Override
	public boolean isFirst() throws SQLException {
		return this._resultSet.isFirst();
	}

	@Override
	public boolean isLast() throws SQLException {
		return this._resultSet.isLast();
	}

	@Override
	public void beforeFirst() throws SQLException {
		this._resultSet.beforeFirst();
	}

	@Override
	public void afterLast() throws SQLException {
		this._resultSet.afterLast();
	}

	@Override
	public boolean first() throws SQLException {
		return this._resultSet.first();
	}

	@Override
	public boolean last() throws SQLException {
		return this._resultSet.last();
	}

	@Override
	public int getRow() throws SQLException {
		return this._resultSet.getRow();
	}

	@Override
	public boolean absolute(int row) throws SQLException {
		return this._resultSet.absolute(row);
	}

	@Override
	public boolean relative(int rows) throws SQLException {
		return this._resultSet.relative(rows);
	}

	@Override
	public boolean previous() throws SQLException {
		return this._resultSet.previous();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		this._resultSet.setFetchDirection(direction);
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return this._resultSet.getFetchDirection();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		this._resultSet.setFetchSize(rows);
	}

	@Override
	public int getFetchSize() throws SQLException {
		return this._resultSet.getFetchSize();
	}

	@Override
	public int getType() throws SQLException {
		return this._resultSet.getType();
	}

	@Override
	public int getConcurrency() throws SQLException {
		return this._resultSet.getConcurrency();
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		return this._resultSet.rowUpdated();
	}

	@Override
	public boolean rowInserted() throws SQLException {
		return this._resultSet.rowInserted();
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		return this._resultSet.rowDeleted();
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		this._resultSet.updateNull(columnIndex);
	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		this._resultSet.updateBoolean(columnIndex, x);
	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		this._resultSet.updateByte(columnIndex, x);
	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		this._resultSet.updateShort(columnIndex, x);
	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		this._resultSet.updateInt(columnIndex, x);
	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		this._resultSet.updateLong(columnIndex, x);
	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		this._resultSet.updateFloat(columnIndex, x);
	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		this._resultSet.updateDouble(columnIndex, x);
	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x)
			throws SQLException {
		this._resultSet.updateBigDecimal(columnIndex, x);
	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		this._resultSet.updateString(columnIndex, x);
	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		this._resultSet.updateBytes(columnIndex, x);
	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		this._resultSet.updateDate(columnIndex, x);
	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		this._resultSet.updateTime(columnIndex, x);
	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x)
			throws SQLException {
		this._resultSet.updateTimestamp(columnIndex, x);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		this._resultSet.updateAsciiStream(columnIndex, x, length);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		this._resultSet.updateBinaryStream(columnIndex, x, length);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length)
			throws SQLException {
		this._resultSet.updateCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scaleOrLength)
			throws SQLException {
		this._resultSet.updateObject(columnIndex, x, scaleOrLength);
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		this._resultSet.updateObject(columnIndex, x);
	}

	@Override
	public void updateNull(String columnLabel) throws SQLException {
		this._resultSet.updateNull(columnLabel);
	}

	@Override
	public void updateBoolean(String columnLabel, boolean x)
			throws SQLException {
		this._resultSet.updateBoolean(columnLabel, x);
	}

	@Override
	public void updateByte(String columnLabel, byte x) throws SQLException {
		this._resultSet.updateByte(columnLabel, x);
	}

	@Override
	public void updateShort(String columnLabel, short x) throws SQLException {
		this._resultSet.updateShort(columnLabel, x);
	}

	@Override
	public void updateInt(String columnLabel, int x) throws SQLException {
		this._resultSet.updateInt(columnLabel, x);
	}

	@Override
	public void updateLong(String columnLabel, long x) throws SQLException {
		this._resultSet.updateLong(columnLabel, x);
	}

	@Override
	public void updateFloat(String columnLabel, float x) throws SQLException {
		this._resultSet.updateFloat(columnLabel, x);
	}

	@Override
	public void updateDouble(String columnLabel, double x) throws SQLException {
		this._resultSet.updateDouble(columnLabel, x);
	}

	@Override
	public void updateBigDecimal(String columnLabel, BigDecimal x)
			throws SQLException {
		this._resultSet.updateBigDecimal(columnLabel, x);

	}

	@Override
	public void updateString(String columnLabel, String x) throws SQLException {
		this._resultSet.updateString(columnLabel, x);

	}

	@Override
	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		this._resultSet.updateBytes(columnLabel, x);

	}

	@Override
	public void updateDate(String columnLabel, Date x) throws SQLException {
		this._resultSet.updateDate(columnLabel, x);
	}

	@Override
	public void updateTime(String columnLabel, Time x) throws SQLException {
		this._resultSet.updateTime(columnLabel, x);

	}

	@Override
	public void updateTimestamp(String columnLabel, Timestamp x)
			throws SQLException {
		this._resultSet.updateTimestamp(columnLabel, x);

	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, int length)
			throws SQLException {
		this._resultSet.updateAsciiStream(columnLabel, x, length);
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, int length)
			throws SQLException {
		this._resultSet.updateBinaryStream(columnLabel, x, length);
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader,
			int length) throws SQLException {
		this._resultSet.updateCharacterStream(columnLabel, reader, length);
	}

	@Override
	public void updateObject(String columnLabel, Object x, int scaleOrLength)
			throws SQLException {
		this._resultSet.updateObject(columnLabel, x, scaleOrLength);

	}

	@Override
	public void updateObject(String columnLabel, Object x) throws SQLException {
		this._resultSet.updateObject(columnLabel, x);
	}

	@Override
	public void insertRow() throws SQLException {
		this._resultSet.insertRow();

	}

	@Override
	public void updateRow() throws SQLException {
		this._resultSet.updateRow();

	}

	@Override
	public void deleteRow() throws SQLException {
		this._resultSet.deleteRow();
	}

	@Override
	public void refreshRow() throws SQLException {
		this._resultSet.refreshRow();

	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		this._resultSet.cancelRowUpdates();
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		this._resultSet.moveToInsertRow();
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		this._resultSet.moveToCurrentRow();
	}

	@Override
	public Statement getStatement() throws SQLException {
		return this._resultSet.getStatement();
	}

	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map)
			throws SQLException {
		return this._resultSet.getObject(columnIndex, map);
	}

	@Override
	public Ref getRef(int columnIndex) throws SQLException {
		return this._resultSet.getRef(columnIndex);
	}

	@Override
	public Blob getBlob(int columnIndex) throws SQLException {
		return this._resultSet.getBlob(columnIndex);
	}

	@Override
	public Clob getClob(int columnIndex) throws SQLException {
		return this._resultSet.getClob(columnIndex);
	}

	@Override
	public Array getArray(int columnIndex) throws SQLException {
		return this._resultSet.getArray(columnIndex);
	}

	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map)
			throws SQLException {

		return this._resultSet.getObject(columnLabel, map);
	}

	@Override
	public Ref getRef(String columnLabel) throws SQLException {
		return this._resultSet.getRef(columnLabel);
	}

	@Override
	public Blob getBlob(String columnLabel) throws SQLException {
		return this._resultSet.getBlob(columnLabel);
	}

	@Override
	public Clob getClob(String columnLabel) throws SQLException {
		return this._resultSet.getClob(columnLabel);
	}

	@Override
	public Array getArray(String columnLabel) throws SQLException {
		return this._resultSet.getArray(columnLabel);
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return this._resultSet.getDate(columnIndex, cal);
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return this._resultSet.getDate(columnLabel, cal);
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return this._resultSet.getTime(columnIndex, cal);
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return this._resultSet.getTime(columnLabel, cal);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal)
			throws SQLException {
		return this._resultSet.getTimestamp(columnIndex, cal);
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal)
			throws SQLException {
		return this._resultSet.getTimestamp(columnLabel, cal);
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		return this._resultSet.getURL(columnIndex);
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		return this._resultSet.getURL(columnLabel);
	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		this._resultSet.updateRef(columnIndex, x);
	}

	@Override
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		this._resultSet.updateRef(columnLabel, x);
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		this._resultSet.updateBlob(columnIndex, x);
	}

	@Override
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		this._resultSet.updateBlob(columnLabel, x);
	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		this._resultSet.updateClob(columnIndex, x);
	}

	@Override
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		this._resultSet.updateClob(columnLabel, x);
	}

	@Override
	public void updateArray(int columnIndex, Array x) throws SQLException {
		this._resultSet.updateArray(columnIndex, x);
	}

	@Override
	public void updateArray(String columnLabel, Array x) throws SQLException {
		this._resultSet.updateArray(columnLabel, x);
	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		return this._resultSet.getRowId(columnIndex);
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		return this._resultSet.getRowId(columnLabel);
	}

	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		this._resultSet.updateRowId(columnIndex, x);
	}

	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		this._resultSet.updateRowId(columnLabel, x);
	}

	@Override
	public int getHoldability() throws SQLException {
		return this._resultSet.getHoldability();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return this._resultSet.isClosed();
	}

	@Override
	public void updateNString(int columnIndex, String nString)
			throws SQLException {
		this._resultSet.updateNString(columnIndex, nString);
	}

	@Override
	public void updateNString(String columnLabel, String nString)
			throws SQLException {
		this._resultSet.updateNString(columnLabel, nString);
	}

	@Override
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		this._resultSet.updateNClob(columnIndex, nClob);
	}

	@Override
	public void updateNClob(String columnLabel, NClob nClob)
			throws SQLException {
		this._resultSet.updateNClob(columnLabel, nClob);
	}

	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		return this._resultSet.getNClob(columnIndex);
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		return this._resultSet.getNClob(columnLabel);
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return this._resultSet.getSQLXML(columnIndex);
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return this._resultSet.getSQLXML(columnLabel);
	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xmlObject)
			throws SQLException {
		this._resultSet.updateSQLXML(columnIndex, xmlObject);
	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML xmlObject)
			throws SQLException {
		this._resultSet.updateSQLXML(columnLabel, xmlObject);
	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		return this._resultSet.getNString(columnIndex);
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		return this._resultSet.getNString(columnLabel);
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return this._resultSet.getNCharacterStream(columnIndex);
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return this._resultSet.getNCharacterStream(columnLabel);
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		this._resultSet.updateNCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		this._resultSet.updateNCharacterStream(columnLabel, reader, length);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		this._resultSet.updateAsciiStream(columnIndex, x, length);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		this._resultSet.updateBinaryStream(columnIndex, x, length);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		this._resultSet.updateCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length)
			throws SQLException {
		this._resultSet.updateAsciiStream(columnLabel, x, length);
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x,
			long length) throws SQLException {
		this._resultSet.updateBinaryStream(columnLabel, x, length);
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		this._resultSet.updateCharacterStream(columnLabel, reader, length);
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length)
			throws SQLException {
		this._resultSet.updateBlob(columnIndex, inputStream, length);
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream,
			long length) throws SQLException {
		this._resultSet.updateBlob(columnLabel, inputStream, length);
	}

	@Override
	public void updateClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		this._resultSet.updateClob(columnIndex, reader, length);
	}

	@Override
	public void updateClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		this._resultSet.updateClob(columnLabel, reader, length);
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		this._resultSet.updateNClob(columnIndex, reader, length);
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		this._resultSet.updateNClob(columnLabel, reader, length);
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		this._resultSet.updateCharacterStream(columnIndex, x);
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		this._resultSet.updateNCharacterStream(columnLabel, reader);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x)
			throws SQLException {
		this._resultSet.updateAsciiStream(columnIndex, x);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x)
			throws SQLException {
		this._resultSet.updateBinaryStream(columnIndex, x);

	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		this._resultSet.updateCharacterStream(columnIndex, x);
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x)
			throws SQLException {
		this._resultSet.updateAsciiStream(columnLabel, x);
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x)
			throws SQLException {
		this._resultSet.updateBinaryStream(columnLabel, x);
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		this._resultSet.updateCharacterStream(columnLabel, reader);
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream)
			throws SQLException {
		this._resultSet.updateBlob(columnIndex, inputStream);
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream)
			throws SQLException {
		this._resultSet.updateBlob(columnLabel, inputStream);
	}

	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		this._resultSet.updateClob(columnIndex, reader);
	}

	@Override
	public void updateClob(String columnLabel, Reader reader)
			throws SQLException {
		this._resultSet.updateClob(columnLabel, reader);
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		this._resultSet.updateNClob(columnIndex, reader);

	}

	@Override
	public void updateNClob(String columnLabel, Reader reader)
			throws SQLException {
		this._resultSet.updateNClob(columnLabel, reader);

	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		T tValue = this._resultSet.getObject(columnIndex, type);
		if (this._fixNulls && tValue == null && type == UUID.class)
			return type.cast(Constants.UUIDEmpty);
		return tValue;
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type)
			throws SQLException {
		T tValue = this._resultSet.getObject(columnLabel, type);
		if (this._fixNulls && tValue == null && type == UUID.class)
			return type.cast(Constants.UUIDEmpty);
		return tValue;
	}

	public UUID getUUID(int columnIndex) throws SQLException {
		Object oValue = this._resultSet.getObject(columnIndex);
		if (this._fixNulls && oValue == null)
			return Constants.UUIDEmpty;
		return ((UUID) oValue);
	}

	public UUID getUUID(String columnLabel) throws SQLException {
		Object oValue = this._resultSet.getObject(columnLabel);
		if (this._fixNulls && oValue == null)
			return Constants.UUIDEmpty;
		return ((UUID) oValue);
	}

	public boolean hasColumn(String columnName) throws SQLException {
		ResultSetMetaData metaData = this._resultSet.getMetaData();
		for (int i = 1; i <= metaData.getColumnCount(); ++i) {
			if (metaData.getColumnName(i).equals(columnName))
				return true;
		}
		return false;
	}

	public boolean getFixNulls() {
		return this._fixNulls;
	}

	public void setFixNulls(boolean fixNulls) {
		this._fixNulls = fixNulls;
	}

	public boolean getFixMissing() {
		return this._fixMissing;
	}

	public void setFixMissing(boolean fixMissing) {
		this._fixMissing = fixMissing;
	}

	public void checkReturnStatus() throws ReturnStatusException {
		// advances the cursor to read status field if present and if
		// isBeforeFirst true
		try {
			// check if the reader has status and reason columns and if not
			// return
			// without advancing the cursor
			if (!hasColumn("status") || !hasColumn("reason"))
				return;

			// if the cursor located before the first line, advance (only once)
			// to
			// read status field
			// this advance will be in place of first next() call from the
			// repository
			// code
			if (_isBeforeFirst) {
				// We need next here because the value in status field is used
				// to determine success or failure scenario of the stored
				// procedure
				_resultSet.next();
				_isBeforeFirst = false;
				_isStatus = true;
			}

			// populate return status fields
			ReturnStatus returnStatus = new ReturnStatus(
					_resultSet.getString("status"));
			if ("failed".equalsIgnoreCase(returnStatus.getStatus())) {
				// get the reason (it always exists if we came this far)
				returnStatus.setReason(_resultSet.getString("reason"));
				if (hasColumn("context")) {
					returnStatus.setContext(_resultSet.getString("context"));
				}
				if (hasColumn("appkey")) {
					returnStatus.setAppKey(_resultSet.getString("appkey"));
				}
				throw new ReturnStatusException(returnStatus);
			}
		} catch (SQLException se) {
			// TODO debug case
			throw new ReturnStatusException(se);
		}
	}

}
