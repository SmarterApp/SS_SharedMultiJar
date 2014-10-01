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

import javax.sql.DataSource;

public class AbstractConnectionManager
{
  
  private DATABASE_TYPE _dbType = null;
  
  private DataSource _dataSource = null;
  
  
  
  public DATABASE_TYPE getDatabaseDialect()
  {
    return _dbType;
  }
  
  public void setDatabaseDialect( DATABASE_TYPE dialect ) {
    _dbType = dialect;
  }
  
  public void setDataSource( DataSource dataSource ) {
    _dataSource = dataSource;
  }
  
  public DataSource getDataSource() {
    return _dataSource;
  }
  
  public SQLConnection getConnection () throws SQLException {
    return new SQLConnection( _dataSource.getConnection () );
  }

  public void closeConnection (SQLConnection conn) throws SQLException {
    conn.close();
  }
 
}
