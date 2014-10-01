/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import javax.servlet.http.Cookie;

import AIR.Common.Web.Session.HttpContext;

/**
 * @author mpatel
 * 
 */
public class CookieContainer
{
  private String                  _key;

  private boolean                 _isDirty = false;
  private HashMap<String, String> _values  = new HashMap<String, String> ();

  // / <summary>
  // / Cookies key.
  // / </summary>
  public String getKey ()
  {
    return _key;
  }

  // / <summary>
  // / Has a change been made to a value.
  // / </summary>
  public boolean isDirty ()
  {
    return _isDirty;
  }

  public boolean isEmpty ()
  {
    return _values.isEmpty ();
  }

  public void Clear ()
  {
    _values.clear ();
  }

  public CookieContainer (String key)
  {
    _key = key;
  }

  // / <summary>
  // / Load the cookie from the HTTP request.
  // / </summary>
  public boolean load ()
  {
    // clear internal collection
    _values.clear ();
    if (HttpContext.getCurrentContext () == null)
      return false;

    Cookie[] cookies = HttpContext.getCurrentContext ().getRequest ().getCookies ();

    if (cookies == null)
      return false;

    for (Cookie cookie : cookies) {
      if (cookie.getName ().equalsIgnoreCase (_key)) {
        // _values.add(cookie.getValues());
        _values.put (cookie.getName (), cookie.getValue ());
        _isDirty = false;
        return true;
      }
    }
    return false;
  }

  // / <summary>
  // / Save the cookie to the HTTP response.
  // / </summary>
  public boolean save ()
  {
    if (HttpContext.getCurrentContext () == null)
      return false;
    /*
     * HttpCookie cookie = new HttpCookie(_key); cookie.Values.Add(_values);
     */
    Cookie cookie = new Cookie (_key, _values.get (_key));
    // HttpContext.getCurrentContext().Response.Cookies.Set(cookie);
    HttpContext.getCurrentContext ().getResponse ().addCookie (cookie);
    _isDirty = false;
    return true;
  }

  // / <summary>
  // / Set a name/value on the cookie.
  // / </summary>
  public void set (String name, Object value)
  {
    if (value == null)
      return;
    String strValue = value.toString ();
    strValue = encode (strValue);
    _values.put (name, strValue);
    _isDirty = true;
  }

  // / <summary>
  // / Add a name/value to the cookie.
  // / </summary>
  public void add (String name, Object value)
  {
    if (value == null)
      return;
    String strValue = value.toString ();
    strValue = encode (strValue);
    _values.put (name, strValue);
    _isDirty = true;
  }

  // / <summary>
  // / Check if a name/value exists.
  // / </summary>
  public boolean exists (String name)
  {
    return _values.containsKey (name);
  }

  // / <summary>
  // / Get the String value for a name.
  // / </summary>
 /* public String get (String name)
  {
    String value = _values.get (name);
    return decode (value);
  }*/

  // / <summary>
  // / Get the String value for a name.
  // / </summary>
  /*
   * public String[] GetValues(String name) { String[] values =
   * _values.GetValues(name);
   * 
   * if (values != null) { for (int i = 0; i < values.Length; i++) { values[i] =
   * Decode(values[i]); } }
   * 
   * return values; }
   */

  /// <summary>
  /// Get the strongly typed value for a name.
  /// </summary>
  @SuppressWarnings ("unchecked")
  public <T> T get(String name)  
  {
      String value = _values.get (name);
      if (value == null) return null;
      value = decode(value);
      return (T)value;
  }


  private static String encode (String data)
  {
    if (data == null || data.isEmpty ()) {
      return null;
    }
    return HttpContext.getCurrentContext ().getResponse ().encodeURL (data);
    // return HttpContext.getCurrentContext().Server.UrlEncode(data);
  }

  private static String decode (String data)
  {
    if (data == null || data.isEmpty ()) {
      return null;
    }
    try {
      data = URLDecoder.decode (data, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace ();
    }
    return data;
    // return HttpContext.getCurrentContext().Server.UrlDecode(data);
  }

  // public String ComputeHash()
  // {
  // // get the sorted cookie values
  // Dictionary<String, String> sortedData = _values.ToDictionary();
  //
  // // create a salt
  // byte[] data = Encoding.UTF8.GetBytes(sortedData.Values.Join("-"));
  // String salt = MachineKey.Encode(data, MachineKeyProtection.Encryption);
  // sortedData.Add("salt", salt);
  //
  // // compute the hash
  // HMACSHA1 hmacsha1 = new HMACSHA1();
  // String hash = hmacsha1.ComputeHash(sortedData);
  //
  // return hash;
  // }

}
