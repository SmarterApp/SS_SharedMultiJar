/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web.Session;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.NotImplementedException;

import TDS.Shared.Security.TDSIdentity;

public class HttpContext
{

  private HttpServletRequest              _request     = null;
  private HttpServletResponse             _response    = null;
  private CookieHolder                    _cookies     = null;
  private Server                          _server      = new Server ();
  private Map<String, Object>             _cache       = new HashMap<String, Object> ();
  /*
   * singleton instance.
   */
  private static ThreadLocal<HttpContext> _httpContext = new ThreadLocal<HttpContext> ()
                                                       {
                                                         protected HttpContext initialValue () {
                                                           return new HttpContext ();
                                                         }
                                                       };

  public static HttpContext getCurrentContext () {
    return _httpContext.get ();
  }

  public HttpServletRequest getRequest () {
    return _request;
  }
  
  public String getUserAgent() {
    return _request.getHeader("user-agent");
  }

  public boolean isAuthenticated () {
    // todo: needs to be implemented.
    throw new NotImplementedException ();
  }

  public Server getServer () {
    return _server;
  }

  public HttpServletResponse getResponse () {
    return _response;
  }

  public CookieHolder getCookies () {
    return _cookies;
  }
  
  public void setResponse (HttpServletResponse response) {
    _response = response;
  }

  public void setRequest (HttpServletRequest request) {
    _request = request;
    // update cookies list.
    _cookies = new CookieHolder (this, request.getCookies ());
  }

  public void clearContext () {
    this._cache.clear ();
    this._request = null;
    this._response = null;
    this._cookies = null;
    _httpContext.remove ();
  }
  
  public void clearCache () {
    _cache.clear ();
  }

  public void setItem (String name, Object value) {
    this._cache.put (name, value);
  }

  public Object getItem (String name) {
    return this._cache.get (name);
  }

  public void removeItem (String name) {
    this._cache.remove (name);
  }

  public boolean containsItem (String name) {
    return this._cache.containsKey (name);
  }

  // TODO mpatel - Implement Later.
  public boolean isDebuggingEnabled () {
    return true;
  }

  public TDSIdentity getIdentity () {
    return (TDSIdentity) this._cache.get ("TDS_IDENTITY_CACHE_ITEM");
  }

  public void setIdentity (TDSIdentity value) {
    this._cache.put ("TDS_IDENTITY_CACHE_ITEM", value);
  }

  private HttpContext () {
    // just to hide the default constructor.
  }
}
