/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Configuration;

public interface ITDSSettingsSource
{

  // / <summary>
  // / Get the session type name from the web.config.
  // / </summary>
  // / <remarks>
  // / 0 = Normal
  // / 1 = ProctorAsStudent
  // / </remarks>
  public abstract int getSessionType ();

  // / <summary>
  // / Store client name in a cookie. Use NULL to delete client name.
  // / </summary>
  public abstract void setClientName (String clientName);

  // / <summary>
  // / Get the current client name.
  // / </summary>
  public abstract String getClientName ();

  public abstract String getTDSConfigsDBName();
  
  public abstract String getItembankDBName();
  
  public abstract String getTDSArchiveDBName();
  
  public abstract String getTDSSessionDBName (); 
  
  public abstract String getDBDialect();
  
  public abstract String getAppName();
  
  public abstract boolean isFirebugLiteEnabled();
  
  public abstract String getTDSReportsRootDirectory();
  
  public abstract boolean isTestScoringLogDebug();
  
  public abstract boolean isTestScoringLogError();
  
}