/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Security;

import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import TDS.Shared.Exceptions.TDSSecurityException;
import AIR.Common.Utilities.SpringApplicationContext;
import AIR.Common.Utilities.UrlEncoderDecoderUtils;
import AIR.Common.Web.CookieHelper;
import AIR.Common.Web.Session.HttpContext;
import AIR.Common.Web.Session.MultiValueCookie;

/**
 * @author mpatel
 * 
 */
// TODO Shiva: revisit this.
public class TDSIdentity
{
  
  private static final Logger _logger = LoggerFactory.getLogger (TDSIdentity.class);
  
  private FormsAuthenticationTicket _ticket;

  public TDSIdentity (FormsAuthenticationTicket ticket) {
    this._ticket = ticket;
  }

  // / <summary>
  // / Adds a ASP.NET cookie with encrypted forms auth ticket along with user
  // data.
  // / </summary>
  public static TDSIdentity createNew (String username, Map<String, String> values) {
    FormsAuthenticationTicket ticket = new FormsAuthenticationTicket (username, values);
    return new TDSIdentity (ticket);
  }

  // the reason this method exists is because it may have been in the .NET code.
  public String get (String name) {
    return getAuthCookieValue (name);
  }

  public String getAuthCookieValue (String name) {
    return _ticket.getValue (name);
  }

  public void setAuthCookieValue (String name, String value) {
    this._ticket.setValue (name, value);
  }

  public void saveAuthCookie () {
    // encrypt the auth data.
    String userData = FormsAuthentication.encrypt (_ticket);
    HttpContext.getCurrentContext ().getCookies ().add (new MultiValueCookie (FormsAuthentication.getAuthCookieName (), userData));
    HttpContext.getCurrentContext ().setIdentity (this);
  }

  public void setAuthenticated (boolean authenticated) {
    setAuthCookieValue ("IS_AUTH", Boolean.toString (authenticated));
  }

  public boolean isAuthenticated () {
    return Boolean.parseBoolean (get ("IS_AUTH"));
  }

  public String getName () {
    return _ticket.getName ();
  }

  public String getPath () {
    if (_ticket.getCookie () != null)
      return _ticket.getCookie ().getPath ();
    else return null;
  }
  
  public String getAuthencticationType () {
    return "TDS";
  }

  public static TDSIdentity getCurrentTDSIdentity () {

    TDSIdentity identity = HttpContext.getCurrentContext ().getIdentity ();
    _logger.info("loadTest: TDSIdentity.getCurrentTDSIdentity identity " + identity);
    if (identity == null) {
      _logger.info("loadTest: TDSIdentity.getCurrentTDSIdentity FormsAuthentication.getAuthCookieName :  " + FormsAuthentication.getAuthCookieName ());
      // First get the auth cookie.
      MultiValueCookie cookie = HttpContext.getCurrentContext ().getCookies ().findCookie (FormsAuthentication.getAuthCookieName ());
      if (cookie != null && !StringUtils.isEmpty (cookie.getValue ())) {

        String decryptedCookieValue = FormsAuthentication.decrypt (cookie.getValue ());
        _logger.info("loadTest: TDSIdentity.getCurrentTDSIdentity decryptedCookieValue :  " + decryptedCookieValue);
        // replace the value in the underlying single value cookie and use that
        // to
        // create a new multi value cookie.
        Cookie underLyingCookie = cookie.getUnderlyingWebCookie ();
        underLyingCookie.setValue (decryptedCookieValue);
        identity = new TDSIdentity (new FormsAuthenticationTicket (new MultiValueCookie (underLyingCookie)));
      } else {
        // so we do not have a AUTH cookie: our user is not authenticated.
        // create a new FormsAuthenticationTicket and mark the user as not
        // authenticated.
        cookie = new MultiValueCookie (FormsAuthentication.getAuthCookieName ());
        identity = new TDSIdentity (new FormsAuthenticationTicket (cookie));
        identity.setAuthenticated (false);
        // even though we are doing a save, the application may clear the
        // cookies on first entering login page.
        identity.saveAuthCookie ();
      }

      HttpContext.getCurrentContext ().setIdentity (identity);
    }

    return identity;
  }
}
