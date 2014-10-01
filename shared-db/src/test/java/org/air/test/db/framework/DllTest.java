/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package org.air.test.db.framework;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import AIR.Common.DB.AbstractDAO;
import AIR.Common.DB.AbstractDataResultExecutor;
import AIR.Common.DB.DataBaseTable;
import AIR.Common.DB.SQLConnection;
import AIR.Common.DB.SQL_TYPE_To_JAVA_TYPE;
import AIR.Common.DB.SqlParametersMaps;
import AIR.Common.DB.results.DbResultRecord;
import AIR.Common.DB.results.MultiDataResultSet;
import AIR.Common.DB.results.SingleDataResultSet;
import AIR.test.framework.AbstractDBTest;
import TDS.Shared.Exceptions.ReturnStatusException;

@ContextConfiguration (locations = "classpath:db-test-context.xml")
public class DllTest extends AbstractDBTest
{
  private static final Logger _logger = LoggerFactory.getLogger (DllTest.class);

  @Autowired
  private HelperAbstractDLL   dll     = null;

  @Test
  @Ignore
  public void testExecuteMethodAndInsertIntoTemporaryTable () throws SQLException, ReturnStatusException {
    // these two are just parameters to this function.
    final String clientName = "Oregon";
    final String firstName = "FirstName";

    // actual sampple code from here on.
    // Step 1: Open a connection. When the connection is closed we will drop all
    // temporary
    // tables that have been created in this session automatically.
    try (SQLConnection connection = dll.getSQLConnection ()) {

      // SQL query: create table @xyzTableName (temp_ColumnName_tds_id
      // varchar(32), temp_ColumnName_label varchar(32),
      // temp_ColumnName_SortOrder int);
      // Create a table definition. At this point the table has not been
      // created.
      DataBaseTable table = new DataBaseTable ("xyzTableName", getDBDialect ())
          .addColumn ("temp_ColumnName_tds_id", SQL_TYPE_To_JAVA_TYPE.VARCHAR, 32)
          .addColumn ("temp_ColumnName_label", SQL_TYPE_To_JAVA_TYPE.VARCHAR, 32)
          .addColumn ("temp_ColumnName_SortOrder", SQL_TYPE_To_JAVA_TYPE.INT);

      // Execute the method whose return values need to be inserted into the
      // temporary table. You need to specify if you are appending or doing a
      // clear insertion.
      // Example SQL Query here :
      // insert into @xyzTableName (temp_ColumnName_tds_id,
      // temp_ColumnName_label, temp_ColumnName_SortOrder)
      // exec T_LoginRequirements_SP @clientname;
      dll.executeMethodAndInsertIntoTemporaryTable (connection, new AbstractDataResultExecutor ()
      {
        @Override
        public SingleDataResultSet execute (SQLConnection connection) throws ReturnStatusException {
          // Step 1: Any SP/Function whose results are shoved into a temporary
          // table.
          SingleDataResultSet resultSet = dll.T_LoginRequirements_SP (connection, clientName);
          // Rename table columns to the ones in the temporary table. You are
          // basically indicating which column index in resultSet
          // needs to be mapped to which column name in the temporary table. it
          // is easier to do this by index.
          resultSet.resetColumnName (1, "temp_ColumnName_tds_id");
          resultSet.resetColumnName (2, "temp_ColumnName_label");
          resultSet.resetColumnName (3, "temp_ColumnName_SortOrder");
          // return the reset resultSet
          return resultSet;
        }
      }, table, true); // set this false to append to an existing temporary
                       // table.

      // now use that temporary table in a query.
      final String SQL_QUERY_USING_TEMP = "select * from ${temporaryTableName} where temp_ColumnName_tds_id = ${firstName}";
      // first set the temporary table name - do not use a hardcoded string. get
      // it from table.getTableName() because the final table name may be
      // different.
      Map<String, String> tableNames = new HashMap<String, String> ();
      tableNames.put ("temporaryTableName", table.getTableName ()); // notice
                                                                    // how we
                                                                    // got the
                                                                    // name of
                                                                    // the
                                                                    // temporary
                                                                    // tabloe
                                                                    // here.
      String finalQuery = dll.fixDataBaseNames (SQL_QUERY_USING_TEMP, tableNames);

      // get the final result.
      SqlParametersMaps parameters = (new SqlParametersMaps ()).put ("firstName", firstName);
      SingleDataResultSet result = dll.executeStatement (connection, finalQuery, parameters, false).getResultSets ().next ();

      // now based on circumstances drop the temporary table. you may skip this
      // step if you do not use
      // another table by the same name. all temporary tables will be dropped
      // when SQLConnection closes anyways.
      connection.dropTemporaryTable (table);

      // methods with @Test modifier must return void, so let's read thru result
      // set here
      Iterator<DbResultRecord> records = result.getRecords ();
      while (records.hasNext ()) {
        // Step 7: Notice you are not casting it yourself or calling getLong,
        // getInteger etc. You know what column data type you are expecting
        // and you invoke the <T>get method.
        DbResultRecord record = records.next ();
        System.err.println ("===================Record=======================");
        System.err.println (record.<String> get ("temp_ColumnName_tds_id"));
        System.err.println (record.<String> get ("temp_ColumnName_label"));
        System.err.println (record.<Integer> get ("temp_ColumnName_SortOrder"));
      }

    } catch (SQLException exp) {
      _logger.error (exp.getMessage ());
      throw exp;
    }
  }

  @Ignore
  @Test
  public void testCount () throws SQLException, ReturnStatusException {
    AbstractDAO dll = new HelperAbstractDLL ();
    // Step 1: Open a connection
    try (SQLConnection connection = dll.getSQLConnection ()) {
      // Step 2: Create a final String template query. Notice the named
      // parameter for proctorId. We will use the same trick for calling Stored
      // Procedures.
      // This means that SPs will now be invokes as "call <SP> <Parameters>".
      final String sqlQuery = "select count(*) as count from session where proctorId = ${proctorId} and sessiontype = ${sessionType}";
      // Step 3: Put the parameter values in a HashMap. You do not need to to
      // cast everything to a String. But if the column is of type Datetime,
      // UUID or String then
      // make sure to put single quotes e.g. proctorId.
      SqlParametersMaps parameters = (new SqlParametersMaps ()).put ("proctorId", "zpatel@air.org").put ("sessionType", 0);
      // Step 4: Regardless of wether you are calling stored procedures or your
      // own SQL queries there is just one interface.
      // If you were just expecting a count then there is a
      // result.getUpdateCount(). result will never be null, so please do not do
      // a null check.
      // If an exception happens then we will throw the ReturnStatusException.
      // You will be declaring a throws and not putting try-catch in the DLL
      // layer.
      // AbstractDLL takes care of logging the exception.
      MultiDataResultSet result = dll.executeStatement (connection, sqlQuery, parameters, true);
      System.err.println (result.getResultSets ().next ().getRecords ().next ().<Integer> get ("count"));
    } catch (ReturnStatusException exp) {
      // Step 8: This is a dummy code and that is why I am eating up the
      // exception. You wont be doing it. Because we only throw
      // ReturnStatusException you will just pass it on. it means you will not
      // have a try-catch block. Just a "throws ReturnStatusException"
      // declaration.
      _logger.error (exp.getMessage ());
      exp.printStackTrace ();
    }
  }

  @Ignore
  @Test
  public void testResultSet () throws SQLException, ReturnStatusException {
    AbstractDAO dll = new HelperAbstractDLL ();
    // Step 1: Open a connection
    try (SQLConnection connection = dll.getSQLConnection ()) {
      // Step 2: Create a final String template query. Notice the named
      // parameter for proctorId. We will use the same trick for calling Stored
      // Procedures.
      // This means that SPs will now be invokes as "call <SP> <Parameters>".
      final String sqlQuery = "select * from session where proctorId = ${proctorId} and sessiontype = ${sessionType}";
      // Step 3: Put the parameter values in a HashMap. You do not need to to
      // cast everything to a String. But if the column is of type Datetime,
      // UUID or String then
      // make sure to put single quotes e.g. proctorId.
      SqlParametersMaps parameters = (new SqlParametersMaps ()).put ("proctorId", "zpatel@air.org");
      parameters.put ("sessionType", 0);
      // Step 4: Regardless of wether you are calling stored procedures or your
      // own SQL queries there is just one interface.
      // If you were just expecting a count then there is a
      // result.getUpdateCount(). result will never be null, so please do not do
      // a null check.
      // If an exception happens then we will throw the ReturnStatusException.
      // You will be declaring a throws and not putting try-catch in the DLL
      // layer.
      // AbstractDLL takes care of logging the exception.
      MultiDataResultSet result = dll.executeStatement (connection, sqlQuery, parameters, true);

      // Step 5: Iterate over the result sets. If you know there is only one
      // result set then you could just do "result.getResultSets ().next()". You
      // would not need the iterator in that case.
      Iterator<SingleDataResultSet> resultSetIterator = result.getResultSets ();
      while (resultSetIterator.hasNext ()) {
        SingleDataResultSet resultSet = resultSetIterator.next ();
        // Step 6: In most cases this is all you need to do. Iterate over the
        // records.
        Iterator<DbResultRecord> records = resultSet.getRecords ();
        while (records.hasNext ()) {
          // Step 7: Notice you are not casting it yourself or calling getLong,
          // getInteger etc. You know what column data type you are expecting
          // and you invoke the <T>get method.
          DbResultRecord record = records.next ();
          System.err.println ("===================Record=======================");
          System.err.println (record.<Integer> get ("sessiontype"));
          System.err.println (record.<UUID> get ("_fk_browser"));
          System.err.println (record.<String> get ("clientname"));
          System.err.println (record.<Date> get ("DateChanged"));
          System.err.println (record.<Boolean> get ("sim_abort"));
          System.err.println (record.<Long> get ("_efk_Proctor"));
        }
      }
    } catch (ReturnStatusException exp) {
      // Step 8: This is a dummy code and that is why I am eating up the
      // exception. You wont be doing it. Because we only throw
      // ReturnStatusException you will just pass it on. it means you will not
      // have a try-catch block. Just a "throws ReturnStatusException"
      // declaration.
      _logger.error (exp.getMessage ());
      exp.printStackTrace ();
    }
  }
}
