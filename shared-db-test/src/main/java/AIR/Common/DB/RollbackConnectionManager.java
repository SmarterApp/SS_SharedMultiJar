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

import org.opentestsystem.shared.test.api.DBDataCorruptionReporter;
import org.opentestsystem.shared.test.api.LifecycleResource;

public class RollbackConnectionManager extends AbstractConnectionManager
    implements DBDataCorruptionReporter, LifecycleResource
{

  private boolean dataCorrupt = false;

  @Override
  public SQLConnection getConnection () throws SQLException {
    RollbackConnection con = new RollbackConnection (super.getConnection ());
    return con;
  }

  @Override
  public void closeConnection (SQLConnection conn) throws SQLException {
    try {
      conn.rollback ();
    } catch (SQLException e) {
      dataCorrupt = true;
    }
    super.closeConnection (conn);
  }

  @Override
  public boolean isDataCorrupt () {
    return dataCorrupt;
  }

  @Override
  public void startupBeforeDependencies () throws Exception {
    // do nothing
  }

  @Override
  public void startupAfterDependencies () throws Exception {
    // do nothing
  }

  @Override
  public void afterTestBeforeDependencies () throws Exception {
    // do nothing
  }

  @Override
  public void afterTestAfterDependencies () throws Exception {
    // do nothing
  }

  @Override
  public void betweenTestsBeforeDependencies () throws Exception {
    // do nothing
  }

  @Override
  public void betweenTestsAfterDependencies () throws Exception {
    dataCorrupt = false;
  }

  @Override
  public void shutdownBeforeDependencies () throws Exception {
    // do nothing
  }

  @Override
  public void shutdownAfterDependencies () throws Exception {
    // do nothing
  }

}
