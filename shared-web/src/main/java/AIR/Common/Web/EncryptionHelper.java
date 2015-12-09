/*******************************************************************************
 * Educational Online Test Delivery System Copyright (c) 2014 American
 * Institutes for Research
 * 
 * Distributed under the AIR Open Source License, Version 1.0 See accompanying
 * file AIR-License-1_0.txt or at http://www.smarterapp.org/documents/
 * American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
/**
 * 
 */
package AIR.Common.Web;

import TDS.Shared.Security.IEncryption;
import TDS.Shared.Web.Encryption;
import AIR.Common.Utilities.SpringApplicationContext;
import AIR.Common.Utilities.UrlEncoderDecoderUtils;

/**
 * @author Shiva BEHERA [sbehera@air.org]
 * 
 */
// TODO Shiva encryption decryption has not been accomplished yet.
public class EncryptionHelper
{

  // / <summary>
  // / Encode bytes to BASE64
  // / </summary>
  public static String EncodeToBase64 (String s) {
    return UrlEncoderDecoderUtils.encode (s);
  }

  // / <summary>
  // / Decode string from BASE64
  // / </summary>
  public static String DecodeFromBase64 (String s) {
    return UrlEncoderDecoderUtils.decode (s);
  }

  // / <summary>
  // / Encrypt a string and return a web safe base64 string
  // / </summary>
  // / <param name="data"></param>
  // / <returns></returns>
  public static String EncryptToBase64 (String data) {
    return EncodeToBase64 (SpringApplicationContext.getBean ("iEncryption", IEncryption.class).scrambleText (data));
  }

  public static String DecryptFromBase64 (String data) {
    return SpringApplicationContext.getBean ("iEncryption", IEncryption.class).unScrambleText (DecodeFromBase64 (data));
  }

}
