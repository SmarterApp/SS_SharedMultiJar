/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Web;

import TDS.Shared.Security.IEncryption;
import AIR.Common.Utilities.SpringApplicationContext;
import AIR.Common.Web.Session.HttpContext;
import AIR.Common.Web.Session.MultiValueCookie;

public class UserCookie
{
  private HttpContext      thisContext;
  @SuppressWarnings ("unused")
  private String           cookieName;
  private MultiValueCookie cookie;
  @SuppressWarnings ("unused")
  private long             cookieTimeout;
  private boolean          bEncryption = true;
  @SuppressWarnings ("unused")
  private boolean          bHttpOnly   = true;

  public UserCookie (HttpContext httpContext, String cookieName) {
    this.thisContext = httpContext;
    this.cookieName = cookieName;
    // check if the cookie exists in the request.
    cookie = httpContext.getCookies ().findCookie (cookieName);
    if (cookie == null) {
      cookie = new MultiValueCookie (cookieName);
      httpContext.getCookies ().add (cookie);
    }
  }

  public UserCookie (HttpContext thisContext, String cookieName, long cookieTimeout) {
    this (thisContext, cookieName);
    // TODO Shiva how is this cookieTimeout used?
    this.cookieTimeout = cookieTimeout;
  }

  public UserCookie (HttpContext thisContext, String cookieName, boolean bEncryption) {
    this (thisContext, cookieName);
    this.bEncryption = bEncryption;
    thisContext.getCookies ().add (cookie);
  }

  public UserCookie (HttpContext thisContext, String cookieName, boolean bEncryption, boolean bHttpOnly) {
    this (thisContext, cookieName, bEncryption);
    this.bHttpOnly = bHttpOnly;
    thisContext.getCookies ().add (cookie);
  }

  public MultiValueCookie getCookie () {
    setHttpOnlyAttribute ();
    thisContext.getCookies ().add (cookie);
    return this.cookie;
  }

  public void setCookie (MultiValueCookie cookie) {
    this.cookie = cookie;
    setHttpOnlyAttribute ();
    thisContext.getCookies ().add (cookie);
  }

  public boolean getHasValues () {
    return cookie.hasKeys ();
  }

  // / <summary>
  // / Response.Cookies
  // / </summary>
  public void ExpiresCookie () {
    if (this.cookie != null) {
      cookie.setMaxAge (0);
      thisContext.getCookies ().add (cookie);
      this.cookie = null;
    }
  }

  public static void ExpiresCookie (HttpContext thisContext, String cookieName) {
    MultiValueCookie cookie = thisContext.getCookies ().findCookie (cookieName);
    if (cookie != null) {
      cookie.setMaxAge (0);
      thisContext.getCookies ().add (cookie);
    }
  }

  public void RemoveCookie () {
    ExpiresCookie ();
  }

  public static void RemoveCookie (HttpContext thisContext, String cookieName) {
    ExpiresCookie (thisContext, cookieName);
  }

  public String GetValue (String key) {
    String value = this.cookie.getValue (key);
    if (value == null)
      return "";
    if (bEncryption)
      return SpringApplicationContext.getBean ("iEncryption", IEncryption.class).unScrambleText (value);
    else
      return value;
  }
  
  public void debug() {
	  System.out.println("User Cookie");
	  if (this.cookieName != null) System.out.println(this.cookieName);
	  if (this.cookie != null) System.out.println(this.cookie.getUnderlyingWebCookie().getValue());
  }

  public void SetValue (String key, String value) {
    if (value == null)
      value = "";
    setHttpOnlyAttribute ();
    if (bEncryption)
      cookie.setValue (key, SpringApplicationContext.getBean ("iEncryption", IEncryption.class).scrambleText (value));
    else
      cookie.setValue (key, value);
    thisContext.getCookies ().add (cookie);
  }

  // TODO shiva this does not seem to be supported by core java libraries unless
  // we can set cookie version.
  private void setHttpOnlyAttribute () {
    // set bHttpOnly attribute.
  }
}
