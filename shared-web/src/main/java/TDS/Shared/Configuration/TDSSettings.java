/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Configuration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import AIR.Common.Configuration.ConfigurationSection;
import AIR.Common.Web.CookieHelper;
import AIR.Common.Web.Session.HttpContext;
import TDS.Shared.Configuration.TDSDataAccessPropertyNames;

public class TDSSettings implements ITDSSettingsSource
{
  

  @Autowired
  @Qualifier ("appSettings")
  private ConfigurationSection appSettings = null;
  
  @Override
  public String getTDSConfigsDBName () {
    
    return appSettings.get (TDSDataAccessPropertyNames.CONFIGS_DB_NAME).trim ();
  }

  @Override
  public String getItembankDBName () {
    return appSettings.get (TDSDataAccessPropertyNames.ITEMBANK_DB_NAME).trim ();
    
  }

  @Override
  public String getTDSArchiveDBName () {
    return appSettings.get (TDSDataAccessPropertyNames.ARCHIVE_DB_NAME).trim ();
    
  }

  @Override
  public String getTDSSessionDBName () {
    return appSettings.get (TDSDataAccessPropertyNames.SESSION_DB_NAME).trim ();
    
  }
  @Override
  public String getDBDialect () {
    return appSettings.get (TDSDataAccessPropertyNames.DB_DIALECT).trim ();
    
  }

  // / <summary>
  // / Get the current application name.
  // / </summary>
  public String getAppName () {
    return appSettings.get ("AppName").trim ();
  }

  @Override
  public String getTDSReportsRootDirectory () {
    return appSettings.get (TDSDataAccessPropertyNames.TDS_REPORTS_ROOT_DIRECTORY).trim ();
  }
  
  // / <summary>
  // / Get the session type name from the web.config.
  // / </summary>
  // / <remarks>
  // / 0 = Normal
  // / 1 = ProctorAsStudent
  // / </remarks>
  /*
   * (non-Javadoc)
   * 
   * @see TDS.Shared.Configuration.ITDSSettingsSource#getSessionType()
   */
  @Override
  public int getSessionType () {
    return appSettings.getInt32 ("SessionType");
  }

  public String getCookieName (String typeName) {
    String appName = getAppName ();
    return String.format ("TDS-%s-%s", appName, typeName);
  }

  // / <summary>
  // / Store client name in a cookie. Use NULL to delete client name.
  // / </summary>
  /*
   * (non-Javadoc)
   * 
   * @see
   * TDS.Shared.Configuration.ITDSSettingsSource#setClientName(java.lang.String
   * )
   */
  @Override
  public void setClientName (String clientName) {
    String cookieName = getCookieName ("Client");
    CookieHelper.setValue (cookieName, clientName);
  }

  // / <summary>
  // / Get the client name from the querystring.
  // / </summary>
  public String getClientNameFromQueryString () {
    HttpServletRequest currentRequest = HttpContext.getCurrentContext ()
        .getRequest ();
    if (currentRequest != null) {
      String clientName = currentRequest.getParameter ("c") == null ? currentRequest
          .getParameter ("client") : currentRequest.getParameter ("c");
      if (StringUtils.isNotEmpty (clientName))
        return clientName;
    }
    return null;
  }

  // / <summary>
  // / Get the client name from the cookie.
  // / </summary>
  public String getClientNameFromCookie () {

    String cookieName = getCookieName ("Client"); // e.x., TDS-Student-Client
    return CookieHelper.getString (cookieName);
  }

  // / <summary>
  // / Get the client name from the web.config.
  // / </summary>
  public String getClientNameFromConfig () {
    return appSettings.get ("ClientName");
  }

  // / <summary>
  // / Get the current client name.
  // / </summary>
  /*
   * (non-Javadoc)
   * 
   * @see TDS.Shared.Configuration.ITDSSettingsSource#getClientName()
   */
  @Override
  public String getClientName () {
    boolean useClientQueryString = appSettings.getBoolean (
        "ClientQueryString", false);
    boolean useClientCookie = appSettings.getBoolean ("ClientCookie",
        false);

    // first check the querystring
    if (useClientQueryString) {
      // first check the querystring
      String clientName = getClientNameFromQueryString ();
      if (!StringUtils.isEmpty (clientName))
        return clientName;
    }

    // second check the cookie
    if (useClientCookie) {
      String clientName = getClientNameFromCookie ();
      if (!StringUtils.isEmpty (clientName))
        return clientName;
    }

    // finally check the web.config
    return getClientNameFromConfig ();
  }
  
  @Override
  public boolean isFirebugLiteEnabled() {
    return appSettings.getBoolean (TDSCommonPropertyNames.IS_FIREBUG_LITE_ENABLED);
  }

  public ConfigurationSection getAppSettings () {
    return appSettings;
  }

  public void setAppSettings (ConfigurationSection appSettings) {
    this.appSettings = appSettings;
  }

  // public AbstractConnectionManager getAdaptiveServiceDatasource ()
  // {
  // return (AbstractConnectionManager) ResourceManager.getInstance
  // ().getResource ("AdaptiveServiceDb");
  // }

  @Override
  public boolean isTestScoringLogDebug() {
    return appSettings.getBoolean ("testScoring.logDebug");
  }
  
  @Override
  public boolean isTestScoringLogError() {
    return appSettings.getBoolean ("testScoring.logError");
  }
}
