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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractDAO extends AbstractDLL
{

  @Autowired
  @Qualifier ("applicationDataSource")
  private DataSource dataSource = null;

  /**
   * Gets a SQL connection from the autowired {@code applicationDataSource}
   * 
   * @return A {@link SQLConnection} object
   * @throws SQLException
   */
  public SQLConnection getSQLConnection () throws SQLException {
    return this.getSQLConnection (this.dataSource);
  }

  /**
   * Gets a SQL connection from a specific {@link DataSource} object
   * 
   * @author Tongliang LIU [tliu@air.org]
   * 
   * @param src
   *          The datasource
   * @return A {@link SQLConnection} object
   * @throws SQLException
   */
  public SQLConnection getSQLConnection (DataSource src) throws SQLException {
    return new SQLConnection (src.getConnection ());
  }
}
