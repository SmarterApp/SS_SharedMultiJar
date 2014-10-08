/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import AIR.Common.Web.Session.CookieHolder;
import AIR.Common.Web.Session.HttpContext;
import AIR.Common.Web.Session.MultiValueCookie;

// todo
public class CookieHelper
{
  // / <summary>
  // / Set the value in a cookie. Use NULL to delete cookie.
  // / </summary>
  public static void setValue (String name, String value) {
    HttpContext context = HttpContext.getCurrentContext ();
    CookieHolder cookies = context.getCookies ();
    MultiValueCookie cookie = cookies.findCookie (name);
    if (value != null) {
      // set value name in cookie
      if (cookie == null)
        cookie = new MultiValueCookie (name, value);
      else
        cookie.setValue (value);
    } else {
      // delete the cookie
      cookie = cookies.findCookie (name);
      if (cookie != null) {
        cookie.setMaxAge (0);
      }
    }
    if (cookie != null)
      cookies.add (cookie);
  }

  // / <summary>
  // / Get the value from the cookie.
  // / </summary>
  public static String getString (String name) {
    MultiValueCookie cookie = HttpContext.getCurrentContext ().getCookies ().findCookie (name);
    if (cookie != null)
      return cookie.getValue ();
    return null;
  }

  // TODO test this really well.
  @SuppressWarnings ("unchecked")
  public static <T> T getValue (String key, T defaultValue) {
    /*
     * todo: test this logic really well.
     * 
     * todo: may have to come up with alternate design.
     * 
     * we do not know how to handle templated type here as there is no
     * corresponding "default" operator in Java. hack!!! instead for all
     * primitive types we will look up the type and invoke the appropriate Get
     * method.
     */
    String cookieValue = getString (key);
    if (cookieValue != null) {
      if (defaultValue instanceof String)
        return (T) cookieValue;
      if (defaultValue instanceof Integer)
        return (T) (new Integer (Integer.parseInt (cookieValue)));
      else if (defaultValue instanceof Double)
        return (T) (new Double (Double.parseDouble (cookieValue)));
      else if (defaultValue instanceof Long)
        return (T) (new Long (Long.parseLong (cookieValue)));
      else if (defaultValue instanceof Boolean)
        return (T) (new Boolean (Boolean.parseBoolean (cookieValue)));
      else if (defaultValue instanceof Character)
        return (T) (new Character (cookieValue.charAt (0)));
      else if (defaultValue instanceof Float)
        return (T) (new Float (Float.parseFloat (cookieValue)));
      else if (defaultValue instanceof Short)
        return (T) (new Short (Short.parseShort (cookieValue)));
      else if (defaultValue instanceof Byte)
        return (T) (new Byte (Byte.parseByte (cookieValue)));
      return null;
    } else
      return defaultValue;
  }

  public static MultiValueCookie getCookieByName (String name) {
    MultiValueCookie cookie = HttpContext.getCurrentContext ().getCookies ().findCookie (name);
    return cookie;
  }

  public static void deleteCookie (String name) {
    MultiValueCookie cookie = HttpContext.getCurrentContext ().getCookies ().findCookie (name);
    deleteCookie (cookie);
  }

  public static void deleteCookie (MultiValueCookie cookie) {
    if (cookie != null) {
      cookie.setMaxAge (0);
      cookie.setValue ("");
      HttpContext.getCurrentContext ().getCookies ().add (cookie);
    }
  }
}
