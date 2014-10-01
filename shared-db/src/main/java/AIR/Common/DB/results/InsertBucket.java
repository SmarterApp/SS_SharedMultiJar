/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.DB.results;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import AIR.Common.Configuration.ConfigurationSection;
import AIR.Common.DB.AbstractDLL;
import AIR.Common.DB.SQLConnection;
import AIR.Common.DB.SqlParametersMaps;
import TDS.Shared.Configuration.ITDSSettingsSource;
import TDS.Shared.Exceptions.ColumnAlreadyMappedException;
import TDS.Shared.Exceptions.ColumnNotMappedException;

// TODO Shiva turn off auto commit mode.
/*
 * http://javarevisited.blogspot.com/2013/01/jdbc-batch-insert-and-update-example
 * -java-prepared-statement.html
 */
public class InsertBucket extends AbstractDLL
{
  @SuppressWarnings ("unused")
  private static Logger       _logger       = LoggerFactory.getLogger (InsertBucket.class);

  private String              _templateSQL  = null;
  // used by insetBatchAsMulti;  should be in this format: "values (${mycol1}, ${mycol2}...)"
  // _templateSQL in such case should be like this: "insert into <tablename> (colname1, colname2, ... )  "
  private String              _columnsList = null; 
  private SingleDataResultSet _insertBucket = null;
  private Map<String, String> _columnMap    = new HashMap<String, String> ();

  public InsertBucket (String sqlTemplate, SingleDataResultSet values, ITDSSettingsSource tdsSettings) {
    _insertBucket = values;
    _templateSQL = sqlTemplate;
    setTdsSettings (tdsSettings);
  }


//  public InsertBucket (String sqlTemplate, List<CaseInsensitiveMap<Object>> values, ConfigurationSection appSettings) {
//    _insertBucket = new SingleDataResultSet (values);
//    _templateSQL = sqlTemplate;
//    setAppSettings (appSettings);
//  }


  
  public InsertBucket (String sqlTemplate, String sqlColumnsList, SingleDataResultSet values, ITDSSettingsSource tdsSettings) {
    _insertBucket = values;
    _templateSQL = sqlTemplate;
    _columnsList = sqlColumnsList;
    setTdsSettings (tdsSettings);
  }

  // So that we can chain these calls on a single line.
  /*
   * this inserts a mapping for the column position in the insert template query
   * to the key we need to use to get that value from the _insertBucket records.
   */
  public InsertBucket mapInsertQueryColumns (String oldColumn, String newColumn) {
    if (!_insertBucket.hasColumn (oldColumn))
      throw new ColumnNotMappedException (String.format ("Column %s does not existing the the data set.", oldColumn));
    if (_columnMap.containsKey (oldColumn))
      throw new ColumnAlreadyMappedException (String.format ("A mapping for column name %s in query template %s has already been created. Previously mapped column name is %s.", oldColumn,
          _templateSQL,
          _columnMap.get (oldColumn)));
    _columnMap.put (oldColumn, newColumn);
    return this;
  }

  public int executeInsertAsMulti (SQLConnection connection) throws SQLException {
    int updateCount = 0;
    try (Statement st = connection.createStatement ()) {
      StringBuilder sb = new StringBuilder (_templateSQL);
      Iterator<DbResultRecord> records = _insertBucket.getRecords ();
      while (records.hasNext ()) {
        DbResultRecord record = records.next ();
        String tmp = formatOneInsert ( record);
        sb.append (tmp).append (',');
      }
      //remove the last comma
      sb.deleteCharAt (sb.length () - 1);
      updateCount = st.executeUpdate (sb.toString ());
    }
    return updateCount;
  }
  
  private String formatOneInsert (DbResultRecord record) {
    SqlParametersMaps parameters = new SqlParametersMaps ();
    Iterator<String> columnNames = record.getColumnNames ();
    while (columnNames.hasNext ()) {
      String oldColumnName = columnNames.next ();
      String newColumnName = oldColumnName;
      // did the user enter an alias for this column name?
      if (_columnMap.containsKey (oldColumnName))
        newColumnName = _columnMap.get (oldColumnName);
      parameters.put (newColumnName, record.get (oldColumnName));
    }

    String reformulatedColumnList = reformulateQueryWithParametersSubstitution (_columnsList, parameters);
    return reformulatedColumnList;
  }
  
  public int executeInsert (SQLConnection connection) throws SQLException {
    int updateCount = 0;
    boolean preexistingAutoCommitMode = connection.getAutoCommit ();
    connection.setAutoCommit (false);
    try (Statement st = connection.createStatement ()) {
      Iterator<DbResultRecord> records = _insertBucket.getRecords ();
      while (records.hasNext ()) {
        DbResultRecord record = records.next ();
        insertSingleStatement (st, record);
      }
      // execute batch update.
      int[] updateCounts = st.executeBatch ();
      for (int data : updateCounts)
        updateCount = updateCount + data;
    }
    connection.commit ();
    // reset autocommit.
    connection.setAutoCommit (preexistingAutoCommitMode);
    return updateCount;
  }

  // TODO Shiva because I wanted to keep the SingleDataResultSet unmodified, I
  // did not remap the columns.
  // but this method of creating a new map may be slow.
  private void insertSingleStatement (Statement st, DbResultRecord record) throws SQLException {

    SqlParametersMaps parameters = new SqlParametersMaps ();
    Iterator<String> columnNames = record.getColumnNames ();
    while (columnNames.hasNext ()) {
      String oldColumnName = columnNames.next ();
      String newColumnName = oldColumnName;
      // did the user enter an alias for this column name?
      if (_columnMap.containsKey (oldColumnName))
        newColumnName = _columnMap.get (oldColumnName);
      parameters.put (newColumnName, record.get (oldColumnName));
    }

    String reformulatedQuery = reformulateQueryWithParametersSubstitution (_templateSQL, parameters);
    // add batch insert.
    st.addBatch (reformulatedQuery);
  }

}
