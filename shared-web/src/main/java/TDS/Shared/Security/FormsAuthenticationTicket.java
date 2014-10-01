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

import AIR.Common.Web.Session.HttpContext;
import AIR.Common.Web.Session.MultiValueCookie;

/**
 * @author mpatel
 * 
 */
// Shiva: As in .NEt we create a ticket but we do not set it on the response.
public class FormsAuthenticationTicket
{
  private static final String USER_NAME_KEY = "userName";
  private MultiValueCookie    _cookie       = null;

  public FormsAuthenticationTicket (String userName, Map<String, String> values) {
    super ();
    this._cookie = new MultiValueCookie (FormsAuthentication.getAuthCookieName (), values);
    this._cookie.setValue (USER_NAME_KEY, userName);
  }

  public FormsAuthenticationTicket (MultiValueCookie cookie) {
    this._cookie = cookie;
  }

  public MultiValueCookie getCookie () {
    return _cookie;
  }

  public String getUserName () {
    return this._cookie.getValue (USER_NAME_KEY);
  }

  public void setUserName (String userName) {
    this._cookie.setValue (USER_NAME_KEY, userName);
  }

  public String getName () {
    return _cookie.getName ();
  }

  public String getUserData () {
    return _cookie.getUnderlyingWebCookie ().getValue ();
  }

  public void setValue (String key, String value) {
    _cookie.setValue (key, value);
  }

  public String getValue (String key) {
    return _cookie.getValue (key);
  }

  @Override
  public String toString () {
    return getUserData ();
  }

}
