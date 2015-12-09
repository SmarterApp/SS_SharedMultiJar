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
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import TDS.Shared.Exceptions.ReturnStatusException;

public class DataBaseTable
{
  private boolean                      _alreadyCreated = false;
  private boolean                      _IsOnDisk       = true;
  private String                       _tableName;
  private Map<String, TableColumnType> _columnInfo     = new HashMap<String, TableColumnType> ();
  private SQLConnection                _connection     = null;
  private DATABASE_TYPE                _dbType         = null;

  public DataBaseTable (String name, DATABASE_TYPE dbType) {
    // create unique table name. Dashes are not allowed in tbl names.
    _tableName = String.format ("%s%s", name, UUID.randomUUID ().toString ().replace ('-', 'z'));
    this._IsOnDisk = true;
    this._dbType = dbType;
  }

  public String generateInsertColumnsNamesStatement () {
    boolean firstColumn = true;
    StringBuilder insertStatement = new StringBuilder (String.format (" insert into %s (", _tableName) );
    for (Map.Entry<String, TableColumnType> entry : _columnInfo.entrySet ())
    {
      if (!firstColumn)
        insertStatement.append (" , ");
      else
        firstColumn = false;

      insertStatement.append (String.format (" %s ", entry.getKey ()));
    }
    insertStatement.append (" ) values ");

    return insertStatement.toString ();
  }
  
  public String generateInsertColumnsMapStatement() {
    boolean firstColumn = true;
    StringBuilder insertStatement = new StringBuilder(" (");
    for (Map.Entry<String, TableColumnType> entry : _columnInfo.entrySet ())
    {
      if (!firstColumn)
        insertStatement.append (" , ");
      else
        firstColumn = false;

      insertStatement.append (String.format (" ${%s} ", entry.getKey ()));
    }

    insertStatement.append (" ) ");
    return insertStatement.toString ();
  }
  
  public String generateInsertStatement () {
    boolean firstColumn = true;
    StringBuilder insertStatement = new StringBuilder (String.format (" insert into %s (", _tableName));
    for (Map.Entry<String, TableColumnType> entry : _columnInfo.entrySet ())
    {
      if (!firstColumn)
        insertStatement.append (" , ");
      else
        firstColumn = false;

      insertStatement.append (String.format (" %s ", entry.getKey ()));
    }
    insertStatement.append (" ) values (");

    firstColumn = true;
    for (Map.Entry<String, TableColumnType> entry : _columnInfo.entrySet ())
    {
      if (!firstColumn)
        insertStatement.append (" , ");
      else
        firstColumn = false;

      insertStatement.append (String.format (" ${%s} ", entry.getKey ()));
    }

    insertStatement.append (" ) ");
    return insertStatement.toString ();
  }

  public boolean matchesName (String name) {
    if (_dbType == DATABASE_TYPE.SQLSERVER) {
      // TODO Shiva
      // this first check is a safety precaution. we do not want to drop any
      // permanent table.
      if (_tableName.indexOf ("@") > -1 || _tableName.indexOf ("#") > -1)
        return StringUtils.equalsIgnoreCase (name, _tableName);
      else
        return false;
    } else if (_dbType == DATABASE_TYPE.MYSQL) {
      return StringUtils.equalsIgnoreCase (_tableName, name);
    }
    else
      throw new InvalidDataBaseTypeSpecification (String.format ("Database of type %s is not recognized", _dbType));
  }

  // Gets the final table name e.g. SQL server temporary tables have a '#'
  // whereas
  // table variables have a '@'.
  public String getTableName () {
    return _tableName;
  }

  public DataBaseTable addColumn (String columnName, SQL_TYPE_To_JAVA_TYPE columnType, Integer size) {
    _columnInfo.put (columnName, new TableColumnType (columnType, size));
    return this;
  }

  public DataBaseTable addColumn (String columnName, SQL_TYPE_To_JAVA_TYPE columnType) {
    return addColumn (columnName, columnType, null);
  }

  protected void dropTable () throws ReturnStatusException {
    String query = null;
    if (_dbType == DATABASE_TYPE.SQLSERVER) {
      query = String.format ("drop table %s", _tableName);
    } else if (_dbType == DATABASE_TYPE.MYSQL) {
      query = String.format ("drop temporary table %s", _tableName);
      // throw new InvalidDataBaseTypeSpecification
      // ("MySQL has not been implemented for dropTable.");
    }
    try (Statement st = _connection.createStatement ()) {
      st.executeUpdate (query);
    } catch (SQLException exp) {
      throw new ReturnStatusException (exp);
    }
  }

  protected void createTable (SQLConnection connection, boolean inMemory) throws ReturnStatusException {
    if (_alreadyCreated)
      throw new ReturnStatusException (String.format ("Table %s has already been created.", _tableName));

    try {
      // We only care about in-memory/on-disk temp tables
      // for Mysql version of DLLs,
      // so just leave Sql Server api as is.
      if (_dbType == DATABASE_TYPE.SQLSERVER) {
        createSqlServerTable (connection);
      } else if (_dbType == DATABASE_TYPE.MYSQL) {
        createMySqlTable (connection, inMemory);
      }
    } catch (SQLException exp) {
      throw new ReturnStatusException (exp);
    }
    this.setOwnerConneciton (connection);
  }

  private void setOwnerConneciton (SQLConnection connection) {
    this._connection = connection;
  }

  private void createSqlServerTable (SQLConnection connection) throws SQLException {

    StringBuilder sqlCommand = new StringBuilder ();
    sqlCommand.append ("CREATE TABLE ");

    String finalTableName = null;
    if (_IsOnDisk)
      finalTableName = " #" + _tableName;
    else {
      // finalTableName = " @" + _tableName;
      throw new InvalidDataBaseTypeSpecification ("Tables in memory are not supported yet.");
    }
    sqlCommand.append (finalTableName);
    sqlCommand.append (" ( ");

    boolean firstColumn = true;
    for (Map.Entry<String, TableColumnType> entry : _columnInfo.entrySet ()) {
      if (firstColumn) {
        firstColumn = false;
      } else {
        sqlCommand.append (" , ");
      }

      TableColumnType type = entry.getValue ();
      String dialectColumnType = _dbType.getSqlDialectTypeFromJdbcType (type._dbType);

      if (type._columnSize == null)
        sqlCommand.append (String.format (" %s %s ", entry.getKey (), dialectColumnType));
      else
        sqlCommand.append (String.format (" %s %s(%d) ", entry.getKey (), dialectColumnType, type._columnSize));
    }
    sqlCommand.append (");");

    try (Statement st = connection.createStatement ()) {
      st.executeUpdate (sqlCommand.toString ());
      // there was no exception.
      _tableName = finalTableName;
    }
  }

  private void createMySqlTable (SQLConnection connection, boolean inMemory) throws SQLException {
    // throw new InvalidDataBaseTypeSpecification
    // ("MySql is not supported yet");

    StringBuilder sqlCommand = new StringBuilder ();
    sqlCommand.append ("CREATE TEMPORARY TABLE ");

    String finalTableName = _tableName;

    sqlCommand.append (finalTableName);
    sqlCommand.append (" ( ");

    boolean firstColumn = true;
    for (Map.Entry<String, TableColumnType> entry : _columnInfo.entrySet ()) {
      if (firstColumn) {
        firstColumn = false;
      } else {
        sqlCommand.append (" , ");
      }

      TableColumnType type = entry.getValue ();
      String dialectColumnType = _dbType.getSqlDialectTypeFromJdbcType (type._dbType);

      if (type._columnSize == null)
        sqlCommand.append (String.format (" %s %s ", entry.getKey (), dialectColumnType));
      else
        sqlCommand.append (String.format (" %s %s(%d) ", entry.getKey (), dialectColumnType, type._columnSize));
    }
    if (inMemory)
      sqlCommand.append (") ENGINE = MEMORY;");
    else
      sqlCommand.append (");");     

    try (Statement st = connection.createStatement ()) {
      st.executeUpdate (sqlCommand.toString ());
      // there was no exception.
      _tableName = finalTableName;
    }
  }
}

// TODO Shiva this part is kind of kudgy. May need to revisit.
class TableColumnType
{
  protected SQL_TYPE_To_JAVA_TYPE _dbType;
  protected Integer               _columnSize = -1;

  public TableColumnType (SQL_TYPE_To_JAVA_TYPE type, Integer columnSize) {
    this._dbType = type;
    this._columnSize = columnSize;
  }
}
