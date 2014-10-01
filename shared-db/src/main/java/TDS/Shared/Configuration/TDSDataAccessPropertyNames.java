/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Configuration;

/**
 * A collection of constants for well-known property names in the test delivery
 * system relating to data access
 * 
 * @author temp_dmenes
 * 
 */
public interface TDSDataAccessPropertyNames
{

  public static final String DB_DIALECT          = "DBDialect";
  public static final String SQL_COMMAND_TIMEOUT = "SqlCommandTimeout";
  public static final String ARCHIVE_DB_NAME     = "TDSArchiveDBName";
  public static final String ITEMBANK_DB_NAME    = "ItembankDBName";
  public static final String CONFIGS_DB_NAME     = "TDSConfigsDBName";
  public static final String SESSION_DB_NAME     = "TDSSessionDBName";
  public static final String TDS_REPORTS_ROOT_DIRECTORY = "TDSReportsRootDirectory";

}
