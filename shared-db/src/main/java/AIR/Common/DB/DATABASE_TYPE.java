/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.DB;

import java.util.HashMap;

public enum DATABASE_TYPE {
  MYSQL("com.mysql.jdbc.Driver", (new MySqlServerType ())),
  SQLSERVER("com.microsoft.sqlserver.jdbc.SQLServerDriver", (new SqlServerType ()));

  private String            _myKnownDrivers = null;
  private SqlDialectTypeMap _sqlDialectMap  = null;

  DATABASE_TYPE (String drivers, SqlDialectTypeMap map) {
    _myKnownDrivers = drivers;
    _sqlDialectMap = map;
  }

  public String getSqlDialectTypeFromJdbcType (SQL_TYPE_To_JAVA_TYPE jdbcType) {
    return _sqlDialectMap.getSqlDialectTypeFromJdbcType (jdbcType);
  }

  public static DATABASE_TYPE getType (String driverBeingLoaded) throws InvalidDataBaseTypeSpecification {
    for (DATABASE_TYPE type : DATABASE_TYPE.values ()) {
      if (type._myKnownDrivers.indexOf (driverBeingLoaded) > -1)
        return type;
    }

    throw new InvalidDataBaseTypeSpecification (String.format ("%1$s type driver is not of know SQL driver types.", driverBeingLoaded));
  }

}

interface SqlDialectTypeMap
{
  public String getSqlDialectTypeFromJdbcType (SQL_TYPE_To_JAVA_TYPE jdbcType);
}

class MySqlServerType implements SqlDialectTypeMap
{
  private static HashMap<SQL_TYPE_To_JAVA_TYPE, String> _map = null;

  @Override
  public String getSqlDialectTypeFromJdbcType (SQL_TYPE_To_JAVA_TYPE jdbcType) {

    return _map.get (jdbcType);
  }

  static {

    _map = new HashMap<SQL_TYPE_To_JAVA_TYPE, String> ();
    _map.put (SQL_TYPE_To_JAVA_TYPE.BIGINT, "bigint");
    _map.put (SQL_TYPE_To_JAVA_TYPE.BIT, "bit");
    // _map.put (SQL_TYPE_To_JAVA_TYPE.BIT, "tinyint");
    _map.put (SQL_TYPE_To_JAVA_TYPE.TINYINT, "tinyint");
    _map.put (SQL_TYPE_To_JAVA_TYPE.DATETIME, "datetime(3)");
    _map.put (SQL_TYPE_To_JAVA_TYPE.INT, "int");
    _map.put (SQL_TYPE_To_JAVA_TYPE.UNIQUEIDENTIFIER, "varbinary(16)");
    _map.put (SQL_TYPE_To_JAVA_TYPE.VARCHAR, "varchar");
    _map.put (SQL_TYPE_To_JAVA_TYPE.TIMESTAMP, "timestamp(3)");
    _map.put (SQL_TYPE_To_JAVA_TYPE.FLOAT, "float");
    _map.put (SQL_TYPE_To_JAVA_TYPE.TEXT, "text");
  }
}

class SqlServerType implements SqlDialectTypeMap
{
  private static HashMap<SQL_TYPE_To_JAVA_TYPE, String> _map = null;

  @Override
  public String getSqlDialectTypeFromJdbcType (SQL_TYPE_To_JAVA_TYPE jdbcType) {
    return _map.get (jdbcType);
  }

  static {
    _map = new HashMap<SQL_TYPE_To_JAVA_TYPE, String> ();
    _map.put (SQL_TYPE_To_JAVA_TYPE.BIGINT, "bigint");
    _map.put (SQL_TYPE_To_JAVA_TYPE.BIT, "bit");
    _map.put (SQL_TYPE_To_JAVA_TYPE.DATETIME, "datetime");
    _map.put (SQL_TYPE_To_JAVA_TYPE.INT, "int");
    _map.put (SQL_TYPE_To_JAVA_TYPE.UNIQUEIDENTIFIER, "uniqueidentifier");
    _map.put (SQL_TYPE_To_JAVA_TYPE.VARCHAR, "varchar");
    _map.put (SQL_TYPE_To_JAVA_TYPE.TIMESTAMP, "timestamp");
    _map.put (SQL_TYPE_To_JAVA_TYPE.NVARCHAR, "nvarchar");
    _map.put (SQL_TYPE_To_JAVA_TYPE.FLOAT, "float");
  }
}
