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

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import AIR.Common.Utilities.UrlEncoderDecoderUtils;

public class MultiValueCookie
{
  private String              _encodedValue = null;
  private String              _name         = null;
  private Integer             _maxAge       = null;
  private String              _path         = "/";
  private String              _comment      = null;
  private String              _domain       = null;
  private Boolean             _isSecure     = null;
  private Integer             _version      = 1;
  private Cookie              _cookie       = null;
  private Map<String, String> _valuesMap    = new HashMap<String, String> ();

  
  public MultiValueCookie (Cookie cookie) {
    this._name = cookie.getName ();
    //Shiva: we can limit the code to the else part rather than have 
    //the "if" part as well. The if part is there just for safety.
    if (StringUtils.isEmpty (cookie.getPath ()))
      this._path = Server.getContextPath ();
    else
      this._path = cookie.getPath ();   
    this._comment = cookie.getComment ();
    this._domain = cookie.getDomain ();
    this._isSecure = cookie.getSecure ();
    this._encodedValue = cookie.getValue ();
    this._cookie = cookie;
    deserializeCookieValue ();
  }

  public MultiValueCookie (String name, String value) {
    //set the cookie path to the context path: we only need to do it in this constructor.
    this._path = Server.getContextPath ();
    this._name = name;
    setValue (value);
  }

  public MultiValueCookie (String name) {
    this (name, "");
  }
  
  public MultiValueCookie (String name, Map<String, String> values) {
    this (name, "");
    this._valuesMap.putAll (values);
  }

  public String getName () {
    return _name;
  }

  // Gets a value indicating whether a cookie has subkeys.
  public boolean hasKeys () {
    return _valuesMap.keySet ().size () > 1;
  }

  // Should we return _cookie.getValue(_cookie.getName()) instead?
  public String getValue () {
    return _valuesMap.get (_name);
  }

  public String getValue (String name) {
    return _valuesMap.get (name);
  }

  public void setValue (String value) {
    setValue (_name, value);
  }

  public void setValue (String name, String value) {
    _valuesMap.put (name, value);
  }

  // start wrapper methods for the Cookie class
  public String getComment () {
    return _comment;
  }

  public String getDomain () {
    return _domain;
  }

  public Integer getMaxAge () {
    return _maxAge;
  }

  public String getPath () {
    return _path;
  }

  public boolean getSecure () {
    return _isSecure;
  }

  public Integer getVersion () {
    return _version;
  }

  public void setComment (String comment) {
    this._comment = comment;
  }

  public void setDomain (String domain) {
    this._domain = domain;
  }

  public void setMaxAge (int expiry) {
    this._maxAge = new Integer (expiry);
  }

  public void setPath (String path) {
    this._path = path;
  }

  public void setSecure (boolean flag) {
    this._isSecure = flag;
  }

  public void setVersion (int version) {
    this._version = version;
  }

  // end wrapper methods for the Cookie class

  @Override
  public boolean equals (Object o) {
    if (o instanceof MultiValueCookie) {
      MultiValueCookie cookieObject = (MultiValueCookie) o;
      return StringUtils.equals (this.getName (), cookieObject.getName ());
    } else
      return false;
  }

  public Cookie getUnderlyingWebCookie () {

    serializeCookieValue ();
    if (_cookie == null) {
      _cookie = new Cookie (this._name, this._encodedValue);
    } else
      _cookie.setValue (_encodedValue);

    if (_maxAge != null)
      _cookie.setMaxAge (_maxAge);

    if (StringUtils.isNotEmpty (_path))
      _cookie.setPath (_path);

    if (StringUtils.isNotEmpty (_comment))
      _cookie.setComment (_comment);

    if (StringUtils.isNotEmpty (_domain))
      _cookie.setDomain (_domain);

    if (_isSecure != null)
      _cookie.setSecure (_isSecure);

    if (_version != null)
      _cookie.setVersion (_version);

    return _cookie;
  }

  public void copyValuesFromAnotherCookie (MultiValueCookie cookie) {
    this._encodedValue = cookie._encodedValue;
    this._maxAge = cookie._maxAge;
    this._path = cookie._path;
    this._comment = cookie._comment;
    this._domain = cookie._domain;
    this._isSecure = cookie._isSecure;
    this._version = cookie._version;
  }

  public boolean isCookieExists (String name) {
    if (name == null) {
      return false;
    }
    return (this._name.equals (name) || this._valuesMap.containsKey (name));
  }

  public void clear () {
    _valuesMap.clear ();
    this._encodedValue = "";
    setValue (_name, "");
  }

  private void serializeCookieValue () {
    // we need to do double encoding: each value needs to be encoded and the
    // whole string then needs to be encoded as well.
    StringBuilder builder = new StringBuilder ();
    int totalCookies = _valuesMap.keySet ().size ();
    for (Map.Entry<String, String> entry : _valuesMap.entrySet ()) {
      String key = entry.getKey ();
      if (!StringUtils.equals (key, this.getName ())) {
        builder.append (entry.getKey ());
        builder.append ("=");
      }
      builder.append (encodeValue(entry.getValue()));
      if (totalCookies > 1) {
        builder.append ("&");
      } 
    }
     _encodedValue = UrlEncoderDecoderUtils.encode (builder.toString ());
  }

  // TODO shiva revisit this logic. We should not be messing around with system
  // cookies.
  private void deserializeCookieValue () {
    // first decode the whole string.
    String decodedString = UrlEncoderDecoderUtils.decode (_encodedValue);

    // now split the string along
    String[] pairs = StringUtils.split (decodedString, '&');
    if (pairs.length > 1) {
      for (String pair : pairs) {
        String[] keyValue = StringUtils.split (pair, '=');
        if (keyValue.length == 1) {
            _valuesMap.put (_name,  decodeValue(keyValue[0]));
        } else {
            _valuesMap.put (keyValue[0], decodeValue(keyValue[1]));
        }

      }
    } else {
      _valuesMap.put(_name, decodeValue(decodedString));
    }
  }
  
  private String encodeValue(String value) {
    return StringUtils.replace(StringUtils.replace(StringUtils.replace(value, "%", "%25"), "&", "%26"), "=", "%3D");
  }

  private String decodeValue(String value) {
    return StringUtils.replace(StringUtils.replace(StringUtils.replace(value, "%26", "&"), "%3D", "="),  "%25", "%");
  }
  
}
