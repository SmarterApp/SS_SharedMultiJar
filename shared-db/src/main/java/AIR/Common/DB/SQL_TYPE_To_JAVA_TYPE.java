/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.DB;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;

import TDS.Shared.Data.ColumnResultSet;

/**
 * We decided to port UUID to MySql as VARBINARY(16). Our sources:
 * http://kekoav.com/posts/uuid-primary-key-mysql
 * 
 */
public enum SQL_TYPE_To_JAVA_TYPE implements ReaderMethod<Object> {
  BIGINT("bigint", Long.class, new LongReaderMethod ()),
  BIT("bit", Boolean.class, new BooleanReaderMethod ()),
  DATETIME("datetime", Date.class, new DatetimeReader ()),
  INT("int", Integer.class, new IntegerReaderMethod ()),
  UNIQUEIDENTIFIER("uniqueidentifier", UUID.class, new UniqueIdentifierReader ()),
  VARCHAR("varchar", String.class, new StringReaderMethod ()),
  TIMESTAMP("timestamp", Date.class, new DatetimeReader ()),
  NVARCHAR("nvarchar", String.class, new StringReaderMethod ()),
  FLOAT("float", Float.class, new FloatReaderMethod ()),
  TINYINT("tinyint", Boolean.class, new BooleanReaderMethod ()),
  VARBINARY("varbinary", UUID.class, new VarBinaryUniqueIdentifierReader ()),
  DATE("date", Date.class, new DatetimeReader ()),
  CHAR("char", String.class, new StringReaderMethod ()),
  DOUBLE("double", Double.class, new FloatReaderMethod());
  
  String          _sqlType      = null;
  Class<?>        _javaType     = null;
  ReaderMethod<?> _readerMethod = null;

  SQL_TYPE_To_JAVA_TYPE (String sqlTypeName, Class<?> javaClass,
      ReaderMethod<?> valueReader) {
    this._sqlType = sqlTypeName;
    this._javaType = javaClass;
    this._readerMethod = valueReader;
  }

  public static SQL_TYPE_To_JAVA_TYPE getType (String sqlType) {
    for (SQL_TYPE_To_JAVA_TYPE type : SQL_TYPE_To_JAVA_TYPE.values ()) {
      // if (StringUtils.equals (type._sqlType, sqlType))
      if (StringUtils.equalsIgnoreCase (type._sqlType, sqlType))
        return type;
    }
    throw new InvalidDataBaseTypeSpecification (String.format (
        "The sql type %1$s is not recognized", sqlType));
  }

  public boolean matchesMyJavaType (Class<?> javatype)
  {
    String t1 = javatype.getCanonicalName ();
    String t2 = _javaType.getCanonicalName ();
    if (StringUtils.equals (t1, t2))
    {
      return true;
    }
    return false;
  }

  @Override
  public Object read (ColumnResultSet reader, int columnIndex)
      throws SQLException {
    return _readerMethod.read (reader, columnIndex);
  }

  @Override
  public Object read (ColumnResultSet reader, String columnName)
      throws SQLException {
    return _readerMethod.read (reader, columnName);
  }
}

interface ReaderMethod<T>
{
  public T read (ColumnResultSet reader, int columnIndex) throws SQLException;

  public T read (ColumnResultSet reader, String columnName)
      throws SQLException;
}

class DatetimeReader implements ReaderMethod<Date>
{
  // SimpleDateFormat formatter = new SimpleDateFormat
  // (AbstractDateUtilDll.DB_DATETIME_FORMAT);
  @Override
  public Date read (ColumnResultSet reader, int columnIndex)
      throws SQLException {
    return reader.getTimestamp (columnIndex);
    // return reader.getDate (columnIndex);
  }

  @Override
  public Date read (ColumnResultSet reader, String columnName)
      throws SQLException {
    return reader.getTimestamp (columnName);
    // return reader.getDate (columnName);
  }
}

class UniqueIdentifierReader implements ReaderMethod<UUID>
{
  @Override
  public UUID read (ColumnResultSet reader, int columnIndex)
      throws SQLException {
    // return UUID.fromString (reader.getString (columnIndex));
    String ret = reader.getString (columnIndex);
    if (ret == null)
      return null;
    else
      return UUID.fromString (ret);
  }

  @Override
  public UUID read (ColumnResultSet reader, String columnName)
      throws SQLException {
    // return UUID.fromString (reader.getString (columnName));
    String ret = reader.getString (columnName);
    if (ret == null)
      return null;
    else
      return UUID.fromString (ret);
  }
}

class VarBinaryUniqueIdentifierReader implements ReaderMethod<UUID>
{
  private UUID convertToUUID (byte[] rt) {

    String rtStr = DatatypeConverter.printHexBinary (rt);
    if (rtStr.length () != 32) {
      return null; // throw some exception?
    }
    // "BFC5216AFD8A459084AF68C1962E494A" -->
    // "BFC5216A-FD8A-4590-84AF-68C1962E494A"
    String uuidStr = String.format ("%s-%s-%s-%s-%s", rtStr.substring (0, 8), rtStr.substring (8, 12), rtStr.substring (12, 16),
        rtStr.substring (16, 20), rtStr.substring (20));

    return UUID.fromString (uuidStr);
  }

  @Override
  public UUID read (ColumnResultSet reader, int columnIndex)
      throws SQLException {

    byte[] rt = reader.getBytes (columnIndex);
    if (rt == null)
      return null;
    else {
      return convertToUUID (rt);
    }
  }

  @Override
  public UUID read (ColumnResultSet reader, String columnName)
      throws SQLException {

    byte[] rt = reader.getBytes (columnName);
    if (rt == null)
      return null;
    else {
      return convertToUUID (rt);
    }
  }
}

class BooleanReaderMethod implements ReaderMethod<Boolean>
{
  @Override
  public Boolean read (ColumnResultSet reader, int columnIndex)
      throws SQLException {
    boolean ret = reader.getBoolean (columnIndex);
    if (reader.wasNull ())
      return null;
    return ret;
  }

  @Override
  public Boolean read (ColumnResultSet reader, String columnName)
      throws SQLException {
    Boolean ret = reader.getBoolean (columnName);
    if (reader.wasNull ())
      return null;
    return ret;
  }
}

class StringReaderMethod implements ReaderMethod<String>
{
  @Override
  public String read (ColumnResultSet reader, int columnIndex)
      throws SQLException {
    return reader.getString (columnIndex);
  }

  @Override
  public String read (ColumnResultSet reader, String columnName)
      throws SQLException {
    return reader.getString (columnName);
  }
}

class IntegerReaderMethod implements ReaderMethod<Integer>
{
  @Override
  public Integer read (ColumnResultSet reader, int columnIndex)
      throws SQLException {
    int ret = reader.getInt (columnIndex);
    if (reader.wasNull ())
      return null;
    return ret;
  }

  @Override
  public Integer read (ColumnResultSet reader, String columnName)
      throws SQLException {
    int ret = reader.getInt (columnName);
    if (reader.wasNull ())
      return null;
    return ret;
  }
}

class LongReaderMethod implements ReaderMethod<Long>
{
  @Override
  public Long read (ColumnResultSet reader, int columnIndex)
      throws SQLException {
    long ret = reader.getLong (columnIndex);
    if (reader.wasNull ())
      return null;
    return ret;
  }

  @Override
  public Long read (ColumnResultSet reader, String columnName)
      throws SQLException {
    long ret = reader.getLong (columnName);
    if (reader.wasNull ())
      return null;
    return ret;
  }
}

class FloatReaderMethod implements ReaderMethod<Float>
{
  @Override
  public Float read (ColumnResultSet reader, int columnIndex)
      throws SQLException {
    float ret = reader.getFloat (columnIndex);
    if (reader.wasNull ())
      return null;
    return ret;
  }

  @Override
  public Float read (ColumnResultSet reader, String columnName)
      throws SQLException {
    float ret = reader.getFloat (columnName);
    if (reader.wasNull ())
      return null;
    return ret;
  }
}
