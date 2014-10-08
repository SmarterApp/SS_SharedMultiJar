/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Security;

import java.util.HashMap;
import java.util.Map;

import TDS.Shared.Configuration.TDSSettings;

import AIR.Common.Utilities.SpringApplicationContext;
import AIR.Common.Web.CookieHelper;
import AIR.Common.Web.Session.MultiValueCookie;

/**
 * @author mpatel
 * 
 */
// TODO mpatel/Shiva Find out the way to implement FormsAuthentication
public class FormsAuthentication
{

  // As in .NET we create the cookie without setting it in the resopnse.
  public static MultiValueCookie getAuthCookie (String userName, boolean createPersistentCookie) {
    FormsAuthenticationTicket ticket = new FormsAuthenticationTicket (userName, new HashMap<String, String> ());
    return ticket.getCookie ();
  }

  public static String encrypt (FormsAuthenticationTicket ticket) {
    return SpringApplicationContext.getBean ("iEncryption", IEncryption.class).scrambleText (ticket.getUserData ());
  }

  public static String decrypt (String ticket) {
    return SpringApplicationContext.getBean ("iEncryption", IEncryption.class).unScrambleText (ticket);
  }

  public static String getLoginUrl () {
    return "LoginShell.xhtml";
  }

  public static String getDefaultUrl () {
    return "Default.xhtml";
  }

  public static void signOut () {
    CookieHelper.deleteCookie (getAuthCookieName ());
  }

  // TODO Shiva: This was hardcoded to be able to use only student cookie name.
  // right now we will create one from the clientname and the application name.
  // in the future we
  // need to be able to get it from config.
  public static String getAuthCookieName () {
    TDSSettings tdsSettings = SpringApplicationContext.getBean ("tdsSettings", TDSSettings.class);
    return String.format ("%s-%s-Auth", tdsSettings.getAppName (), tdsSettings.getClientName ());
  }

}
