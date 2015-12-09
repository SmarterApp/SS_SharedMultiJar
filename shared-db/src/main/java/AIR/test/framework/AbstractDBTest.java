/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.test.framework;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import AIR.Common.DB.AbstractConnectionManager;
import AIR.Common.DB.DATABASE_TYPE;
import TDS.Shared.Configuration.TDSDataAccessPropertyNames;


@ContextConfiguration (locations = "classpath:opentestsystem.shared.test-db-context-module.xml")
public abstract class AbstractDBTest extends AbstractTest
{

  @Autowired
  @Qualifier ("applicationDataSource")
  private DataSource applicationDataSource = null;

  @Autowired
  private AbstractConnectionManager abstractConnectionManager = null;

  public DataSource getDataSource () {
    return applicationDataSource;
  }
  
  public AbstractConnectionManager getConnectionManager() {
    return abstractConnectionManager;
  }
  
  public DATABASE_TYPE getDBDialect () {
    return DATABASE_TYPE.valueOf (getAppSettings ().get (TDSDataAccessPropertyNames.DB_DIALECT));
  }
}
