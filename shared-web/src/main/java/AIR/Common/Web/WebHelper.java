/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
/**
 *
 */
package AIR.Common.Web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import AIR.Common.Web.Session.HttpContext;

/**
 * @author Shiva BEHERA [sbehera@air.org]
 *
 */
public class WebHelper
{
  public static String getQueryString (String name) {
    return HttpContext.getCurrentContext ().getRequest ().getParameter (name);
  }

  public static int getQueryValueInt (String name) {
    String value = getQueryString (name);
    if (StringUtils.isEmpty (value))
      return 0;
    return Integer.parseInt (value);
  }
  
  public static long getQueryValueLong (String name) {
    String value = getQueryString (name);
    if (StringUtils.isEmpty (value))
      return 0;
    return Long.parseLong (value);
  }

  public static boolean getQueryBoolean (String name) {
    return getQueryBoolean(name, false);
  }
  
  public static boolean getQueryBoolean (String name, boolean defaultValue) {
    String value = getQueryString (name);
    if (value != null) {
      return Boolean.parseBoolean (value);
    }
    return defaultValue;
  }

  public static List<String> getQueryValues (String name) {
    String[] values = HttpContext.getCurrentContext ().getRequest ().getParameterValues (name);
    if (values != null) {
      return Arrays.<String> asList (values);
    }
    return new ArrayList<String> ();
  }

  /**
   * Set Content Type in HttpServletResponse
   *
   * @param contentType
   */
  public static void setContentType (String contentType)
  {
    HttpContext.getCurrentContext ().getResponse ().setContentType (contentType);
  }

  /**
   * Set Content Type in HttpServletResponse based in Enum ContentType value
   *
   * @param contentType
   */
  public static void setContentType (ContentType contentType)
  {
    switch (contentType)
    {
    case Text:
      setContentType ("text/plain");
      break;
    case Xml:
      setContentType ("text/xml");
      break;
    case Html:
      setContentType ("text/html");
      break;
    case Json:
      setContentType ("application/json");
      break;
    case Javascript:
      setContentType ("text/javascript");
      break;
    }
  }

  // / <summary>
  // / Write out a raw string to HTTP output stream.
  // / </summary>
  public static void writeString (String value) throws IOException
  {
    HttpContext.getCurrentContext ().getResponse ().getWriter ().write (value);
  }

  // / <summary>
  // / Write out a raw string to HTTP output stream.
  // / </summary>
  public static void writeString (String value, Object[] values) throws IOException
  {
    writeString (String.format (value, values));
  }

}
