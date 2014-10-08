/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.DB;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// TODO: NotImplementedException was dropped from lang3, but is supposed to be
// restored in 3.2. Until then, we are using lang2
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import AIR.Common.DB.results.DbResultRecord;
import AIR.Common.DB.results.InsertBucket;
import AIR.Common.DB.results.MultiDataResultSet;
import AIR.Common.DB.results.SingleDataResultSet;
import AIR.Common.Helpers.CaseInsensitiveMap;
import AIR.Common.Sql.AbstractDateUtilDll;
import AIR.Common.Utilities.TDSStringUtils;
import TDS.Shared.Configuration.ITDSSettingsSource;
import TDS.Shared.Exceptions.DBLockNotSupportedException;
import TDS.Shared.Exceptions.ReturnStatusException;

/**
 * @author efurman
 * 
 */
public abstract class AbstractDLL
{
  private static Logger      _logger     = LoggerFactory.getLogger (AbstractDLL.class);

  @Autowired
  // @Qualifier ("appSettings")
  // private ConfigurationSection appSettings = null;
  private ITDSSettingsSource tdsSettings = null;

  /**
   * @param db
   *          Name of the database e.g. TDSCore_dev_Session2012_Sandbox
   * @param resource
   *          Could be the name of a table or procedure or function anything.
   * @return will return
   */
  /**
   * @deprecated Please use fixDataBaseNames() instead.
   */
  @Deprecated
  public String createDatabaseName (String db, String resource) {
    DATABASE_TYPE dbDialect = getDatabaseDialect ();
    switch (dbDialect) {
    case MYSQL:
      throw new NotImplementedException (
          "MySQL support does not exist yet.");
    case SQLSERVER:
      if (StringUtils.isEmpty (db))
        return TDSStringUtils.format ("dbo.{0}", resource);
      else
        return TDSStringUtils.format ("{0}.dbo.{1}", db, resource);
    default:
      throw new IllegalArgumentException ("Value " + dbDialect.toString ()
          + " is not supported.");
    }
  }

  protected void setTdsSettings (ITDSSettingsSource tdsSettings) {
    this.tdsSettings = tdsSettings;
  }

  public ITDSSettingsSource getTdsSettings () {
    return this.tdsSettings;
  }

  public String fixDataBaseNames (String queryTemplte,
      Map<String, String> dataBaseNames) {
    StrSubstitutor sub = new StrSubstitutor (dataBaseNames);
    String reformulatedQuery = sub.replace (queryTemplte);
    return reformulatedQuery;
  }

  /*
   * returns true if the first result set has any rows.
   */
  public boolean exists (MultiDataResultSet result) {
    return result.getResultSets ().next ().getCount () > 0;
  }

  /**
   * @param connection
   * @param executor
   *          a wrapper object that wraps the execution of a method that returns
   *          a SingleDateResultSet.
   * @param table
   * @param appendToExistingTemporaryTable
   *          set to true if you are appending to an exeisting temporary table.
   *          if not set it to false for the table to be dropped first and
   *          recreated. if value is set to true and the temporary table does
   *          not exist - it may exist in the database but we do not have any
   *          record of that being created in the current session - then we will
   *          throw an exception.
   * @throws ReturnStatusException
   */
  public void executeMethodAndInsertIntoTemporaryTable (
      SQLConnection connection, AbstractDataResultExecutor executor,
      DataBaseTable table, boolean createNewTable)
      throws ReturnStatusException {
    // execute the wrapped method.
    SingleDataResultSet resultToBeInserted = executor.execute (connection);

    // do we need to create the table?
    if (createNewTable) {
      // yes! we need to create the table.
      connection.createTemporaryTable (table);
    }

    // insert the results from SingleDataResultSet into the temporary table.
    if (resultToBeInserted.getCount () > 0) {
      String statement = table.generateInsertStatement ();
      logQuery (statement);
      insertBatch (connection, table.generateInsertStatement (),
          resultToBeInserted, null);
    }
  }

  public int insertBatch (SQLConnection connection, String insertTemplate,
      List<CaseInsensitiveMap<Object>> values,
      Map<String, String> columnMap) throws ReturnStatusException {
    SingleDataResultSet result = new SingleDataResultSet (values);
    return insertBatch (connection, insertTemplate, result, columnMap);
  }

  public int insertBatch (SQLConnection connection, String insertTemplate,
      SingleDataResultSet result, Map<String, String> columnMap)
      throws ReturnStatusException {
    InsertBucket insertBucket = new InsertBucket (insertTemplate, result, tdsSettings);
    if (columnMap != null) {
      for (Map.Entry<String, String> entry : columnMap.entrySet ()) {
        insertBucket.mapInsertQueryColumns (entry.getKey (),
            entry.getValue ());
      }
    }
    try {
      return insertBucket.executeInsert (connection);
    } catch (SQLException exp) {
      throw new ReturnStatusException (exp);
    }
  }

  public int insertBatchAsMulti (SQLConnection connection, String insertTemplatePartial, String insertColumnsPartial, SingleDataResultSet result) throws ReturnStatusException {
    InsertBucket insertBucket = new InsertBucket (insertTemplatePartial, insertColumnsPartial, result, getTdsSettings ());

    try {
      return insertBucket.executeInsertAsMulti (connection);
    } catch (SQLException exp) {
      throw new ReturnStatusException (exp);
    }
  }

  public MultiDataResultSet executeStatement (SQLConnection connection,
      String queryTemplate, SqlParametersMaps parameters,
      boolean useNoLock) throws ReturnStatusException {
    final String messageTemplate = "Exception %1$s executing query. Template is \"%2$s\". Final query is \"%3$s\". Exception message: %4$s.";

    String reformulatedQuery = reformulateQueryWithParametersSubstitution (
        queryTemplate, parameters);

    MultiDataResultSet results = null;
    logQuery (reformulatedQuery);
    int currentTransactionIsolation = -1;
    PreparedStatement st = null;
    try {
      st = connection.prepareStatement (reformulatedQuery);

      if (useNoLock) {

        // if (not supported )then throw DBLockNotSupportedException
        DatabaseMetaData dbMetaData = connection.getMetaData ();

        if (dbMetaData
            .supportsTransactionIsolationLevel (Connection.TRANSACTION_READ_UNCOMMITTED) == false)
          throw new DBLockNotSupportedException (String.format (
              "Select NoLock DB unsupported on %1$s",
              connection.getCatalog ()));

        // TODO shiva confirm if nolock() is same as
        // TRANSACTION_READ_UNCOMMITTED.
        currentTransactionIsolation = connection
            .getTransactionIsolation ();
        connection
            .setTransactionIsolation (Connection.TRANSACTION_READ_UNCOMMITTED);
      }

      @SuppressWarnings ("unused")
      boolean gotResultSet = st.execute ();
      results = new MultiDataResultSet (st);
    } catch (SQLException ex) {
      _logger.error (String.format (messageTemplate, "SQLException",
          queryTemplate, reformulatedQuery, ex.getMessage ()));
      throw new ReturnStatusException (ex);
      // TODO Shiva throw a ReturnStatusException here instead. Discuss
      // first.
    } catch (Exception ex) {
      _logger.error (String.format (messageTemplate, ex.getClass ()
          .getName (), queryTemplate, reformulatedQuery, ex
          .getMessage ()));
      throw new ReturnStatusException (ex);
    } finally {
      try {
        if (currentTransactionIsolation != -1)
          connection.setTransactionIsolation (currentTransactionIsolation);
      } catch (SQLException ex) {
        _logger.error (String.format (messageTemplate, "SQLException",
            queryTemplate, reformulatedQuery, ex.getMessage ()));
        throw new ReturnStatusException (ex);
      } finally {
        try {
          st.close ();
        } catch (Throwable t) {

        }
      }
    }

    return results;
  }

  // instead of doing _isToday we call GetToday to get the start of the 24
  // hour
  // period and in our queries implement that instead.
  public Date[] getTodayWithTimeZoneOffsetForClient (SQLConnection connection,
      String clientName) throws ReturnStatusException {
    int offset = 0;
    final String SQL_QUERY = "select top 1 timeZoneOffset from Externs where clientName = ${clientName}";
    SqlParametersMaps parameters = (new SqlParametersMaps ()).put (
        "clientName", clientName);

    DbResultRecord record = executeStatement (connection, SQL_QUERY,
        parameters, false).getResultSets ().next ().getRecords ().next ();
    if (record != null) {
      offset = record.<Integer> get ("timeZoneOffset");
    }

    // TODO Shiva will this cause timezone bugs?
    AbstractDateUtilDll dateUtil = new AbstractDateUtilDll ()
    {

      @Override
      public Date getDate (Connection connection) throws SQLException {
        return Calendar.getInstance ().getTime ();
      }

      @Override
      public Date getDateWRetStatus (Connection connection)
          throws ReturnStatusException {
        return Calendar.getInstance ().getTime ();
      }
    };

    try {
      dateUtil.calculateMidnights (connection, offset);
      return new Date[] { dateUtil.getMidnightAM (),
          dateUtil.getMidnightPM () };
    } catch (SQLException exp) {
      // We will never throw SQLException here unless there is a bug in
      // the
      // code.
      throw new RuntimeException (
          "SQLException should never have been throw here.");
    }
  }

  /*
   * in here we will replace any parameter e.g. ${ConfigDB} and ${ItemBankDB}
   * and ${ArchiveDB} with actual values.
   */
  public String fixDataBaseNames (String templateQuery) {
    Map<String, String> tableNames = new CaseInsensitiveMap<String> ();
    tableNames.put ("ConfigDB", getConfigDB ());
    tableNames.put ("ItemBankDB", getItemBankDB ());
    tableNames.put ("ArchiveDB", getArchiveDB ());

    StrSubstitutor substitutor = new StrSubstitutor (tableNames);
    return substitutor.replace (templateQuery);
  }

  protected String reformulateQueryWithParametersSubstitution (
      String queryTemplate, SqlParametersMaps map) {
    String reformulatedQuery = queryTemplate;
    if (map != null) {
      Map<String, Object> parameters = replaceWithStrings (map.getMap ());
      StrSubstitutor sub = new StrSubstitutor (parameters);
      reformulatedQuery = sub.replace (queryTemplate);
    }
    return reformulatedQuery;
  }

  /*
   * so that we can keep the queries exactly as is, we will convert the thre
   * types : Date, UUID and String to have single quotes around them.
   */
  private Map<String, Object> replaceWithStrings (
      Map<String, Object> currentMap) {
    Map<String, Object> reformattedMap = new CaseInsensitiveMap<Object> ();
    DATABASE_TYPE dbDialect = getDatabaseDialect ();
    for (Map.Entry<String, Object> entry : currentMap.entrySet ()) {
      Object value = entry.getValue ();
      Object reformattedValue = value;
      if (value == null) {
        reformattedValue = handleNull (dbDialect);
      } else if (value instanceof String) {
        // TODO Elena/Shiva verify for MySQL

        String newValue = ((String) entry.getValue ())
            .replace ("'", "''");
        reformattedValue = handleString (dbDialect, newValue);
      } else if (value instanceof UUID) {
        reformattedValue = handleUUID (dbDialect,
            (UUID) entry.getValue ());
      } else if (value instanceof Date) {
        reformattedValue = handleDate (dbDialect,
            (Date) entry.getValue ());
      } else if (value instanceof Boolean) {
        reformattedValue = handleBoolean (dbDialect,
            (Boolean) entry.getValue ());
        // if ((Boolean)entry.getValue() == true)
        // reformattedValue = 1;
        // else
        // reformattedValue = 0;
      }
      reformattedMap.put (entry.getKey (), reformattedValue);
    }
    return reformattedMap;
  }

  private Object handleBoolean (DATABASE_TYPE dbDialect, Boolean booleanValue) {
    switch (dbDialect) {
    case MYSQL:
    case SQLSERVER:
      return (booleanValue == true ? 1 : 0);
    default:
      throw new InvalidDataBaseTypeSpecification (
          String.format (
              "Not clear how to handle db type %s in AbstractDLL.replaceWithStrings",
              dbDialect));
    }
  }

  private Object handleNull (DATABASE_TYPE dbDialect) {
    switch (dbDialect) {
    case MYSQL:
    case SQLSERVER:
      return "null";
    default:
      throw new InvalidDataBaseTypeSpecification (
          String.format (
              "Not clear how to handle db type %s in AbstractDLL.replaceWithStrings",
              dbDialect));
    }

  }

  private Object handleString (DATABASE_TYPE dbDialect, String stringValue) {
    switch (dbDialect) {
    case MYSQL:
    case SQLSERVER:
      return String.format ("'%s'", stringValue);
    default:
      throw new InvalidDataBaseTypeSpecification (
          String.format (
              "Not clear how to handle db type %s in AbstractDLL.replaceWithStrings",
              dbDialect));
    }
  }

  private Object handleDate (DATABASE_TYPE dbDialect, Date existingDate) {
    switch (dbDialect) {
    // TODO mysql does not pay attention to millesec
    case MYSQL:
    case SQLSERVER:
      // return String.format ("'%s'",
      // AbstractDateUtilDll.getDateAsFormattedString (existingDate));
      return String.format ("'%s'", AbstractDateUtilDll
          .getDateAsFormattedMillisecondsString (existingDate));
    default:
      throw new InvalidDataBaseTypeSpecification (
          String.format (
              "Not clear how to handle db type %s in AbstractDLL.replaceWithStrings",
              dbDialect));
    }
  }

  private Object handleUUID (DATABASE_TYPE dbDialect, UUID existingValue) {
    switch (dbDialect) {
    case MYSQL:
      String uuidStr = existingValue.toString ();
      String newStr = uuidStr.replaceAll ("-", "");
      // byte[] bt = javax.xml.bind.DatatypeConverter.parseHexBinary
      // (uuidStr);
      newStr = String.format ("0x%s", newStr);
      return newStr;

    case SQLSERVER:
      return String.format ("'%s'", existingValue.toString ());
    default:
      throw new InvalidDataBaseTypeSpecification (
          String.format (
              "Not clear how to handle db type %s in AbstractDLL.replaceWithStrings",
              dbDialect));
    }
  }

  private String getConfigDB () {
    String _tdsConfigsDB = tdsSettings.getTDSConfigsDBName ();
    // .get (TDSDataAccessPropertyNames.CONFIGS_DB_NAME);
    return fixDBName (_tdsConfigsDB);
    // return fixDBName ("TDScore_Dev_Configs2012_Sandbox");
  }

  private String getItemBankDB () {
    String _itembankDB = tdsSettings.getItembankDBName ();
    // get (TDSDataAccessPropertyNames.ITEMBANK_DB_NAME);
    return fixDBName (_itembankDB);
    // return fixDBName("TDScore_Dev_ItemBank2012_Sandbox");
  }

  private String getArchiveDB () {
    String _tdsArchiveDBName = tdsSettings.getTDSArchiveDBName ();
    // .get (TDSDataAccessPropertyNames.ARCHIVE_DB_NAME);
    return fixDBName (_tdsArchiveDBName);
    // return fixDBName("TDS_Archive_");
  }

  public DataBaseTable getDataBaseTable (String tblName) {

    return new DataBaseTable (tblName, getDatabaseDialect ());
  }

  public DATABASE_TYPE getDatabaseDialect () {
    return DATABASE_TYPE.valueOf (tdsSettings.getDBDialect ());
    // .get (TDSDataAccessPropertyNames.DB_DIALECT));
  }

  private String fixDBName (String name) {
    DATABASE_TYPE dbDialect = getDatabaseDialect ();
    switch (dbDialect) {
    case MYSQL:
      return name;
    case SQLSERVER:
      return String.format ("%s.dbo", name);
    default:
      throw new IllegalArgumentException ("Value " + dbDialect.toString ()
          + " is not supported.");
    }
  }

  public static boolean hasColumn (ResultSet reader, String columnName)
      throws SQLException {
    ResultSetMetaData metaData = reader.getMetaData ();
    for (int i = 1; i <= metaData.getColumnCount (); ++i) {
      if ((metaData.getColumnName (i).equals (columnName))) {
        return true;
      }
    }
    return false;
  }

  // for debug only
  public void dumpRecord (DbResultRecord record) throws ReturnStatusException {
    System.out.println ();
    String columnName = null;
    Iterator<String> itNames = record.getColumnNames ();
    String resValue = "";
    while (itNames.hasNext ())
    {
      columnName = itNames.next ();
      Object value = record.get (record.getColumnToIndex (columnName).get ());
      if (value != null)
      {
        resValue = value.toString ();
      }
      _logger.info (String.format ("%s: %s", columnName, resValue));
    }
    System.out.println ();
  }

  private static void logQuery (String query) {
    _logger.info ("Query : " + query);
    if (_logger.isDebugEnabled ()) {
      try {
        StringBuilder traceBackMessage = new StringBuilder ("Query traceback:\r\n");
        StackTraceElement[] trace = Thread.currentThread ().getStackTrace ();
        for (int i = 2; i < 9 && i < trace.length; i++) {
          StackTraceElement t = trace[i];
          traceBackMessage.append (String.format ("    %s.%s (%d)\r\n", t.getClassName (), t.getMethodName (), t.getLineNumber ()));
        }
        _logger.debug (traceBackMessage.toString ());
      } catch (Throwable t) {
        // Ignore!!
      }
    }
  }

  public static void main (String[] argv) {
    final String TEMPLATE = "select top 1 clientname from TDSCONFIGS_Client_TestScoreFeatures where clientname = ${client} and TestID = ${testID}  and (ReportToStudent = 1 or ReportToProctor = 1 or ReportToParticipation = 1 or UseForAbility = 1)";
    Map<String, Object> parameters = new CaseInsensitiveMap<Object> ();
    parameters.put ("testID", "ELPA_6-8");
    parameters.put ("client", "Oregon");

    System.err.println (StrSubstitutor.replace (TEMPLATE, parameters));

    SqlParametersMaps parametersSql = new SqlParametersMaps ();
    parametersSql.put ("client", "Oregon");
    parametersSql.put ("testID", "ELPA_6-8");

  }
}
