/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Utilities;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import AIR.Common.Web.MultiValueCookieEncodingException;

public class UrlEncoderDecoderUtils
{
  public static String encode (byte[] input) throws MultiValueCookieEncodingException {
    return encode (new String (input));
  }

  public static String encode (String value) throws MultiValueCookieEncodingException {
    return encode (value, "UTF-8");
  }

  public static String decode (byte[] input) throws MultiValueCookieEncodingException {
    return decode (new String (input));
  }

  public static String decode (String value) throws MultiValueCookieEncodingException {
    return decode (value, "UTF-8");
  }

  public static String encode (String value, String encoding) throws MultiValueCookieEncodingException {
    try {
      return URLEncoder.encode (value, encoding);
    } catch (UnsupportedEncodingException exp) {
      throw new MultiValueCookieEncodingException ("Problem encoding cookie.", exp);
    }
  }

  public static String decode (String value, String encoding) throws MultiValueCookieEncodingException {
    try {
      return URLDecoder.decode (value, encoding);
    } catch (UnsupportedEncodingException exp) {
      throw new MultiValueCookieEncodingException ("Problem decoding cookie.", exp);
    }
  }

}
