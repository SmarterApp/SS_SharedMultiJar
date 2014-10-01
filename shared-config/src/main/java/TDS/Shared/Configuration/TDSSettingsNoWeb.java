/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import AIR.Common.Configuration.ConfigurationSection;


public class TDSSettingsNoWeb implements ITDSSettingsSource
{
  @Autowired
  @Qualifier ("appSettings")
  private ConfigurationSection appSettings = null;
  
  @Override
  public int getSessionType () {
    
    return appSettings.getInt32 ("SessionType");
  }

  @Override
  public void setClientName (String clientName) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String getClientName () {
    
    return appSettings.get ("ClientName");
  }

  @Override
  public String getTDSConfigsDBName () {
    
    return appSettings.get ("TDSConfigsDBName").trim ();   
  }

  @Override
  public String getItembankDBName () {
    
    return appSettings.get ("ItembankDBName").trim ();   

  }

  @Override
  public String getTDSArchiveDBName () {
 
    return appSettings.get ("TDSArchiveDBName").trim ();   

  }

  @Override
  public String getTDSSessionDBName () {
    
    return appSettings.get ("TDSSessionDBName").trim ();
    
  }

  @Override
  public String getDBDialect () {
    
    return appSettings.get ("DBDialect").trim ();
    
  }

  @Override
  public String getAppName () {
    
    return appSettings.get ("AppName").trim ();
  }
  
  @Override
  public String getTDSReportsRootDirectory () {
    return appSettings.get ("TDSReportsRootDirectory").trim ();
  }

  @Override
  public boolean isFirebugLiteEnabled () {
    return false;
  }

  public ConfigurationSection getAppSettings () {
    return appSettings;
  }

  public void setAppSettings (ConfigurationSection appSettings) {
    this.appSettings = appSettings;
  }
  
  @Override
  public boolean isTestScoringLogDebug() {
    return appSettings.getBoolean ("testScoring.logDebug");
  }
  
  @Override
  public boolean isTestScoringLogError() {
    return appSettings.getBoolean ("testScoring.logError");
  }
}
