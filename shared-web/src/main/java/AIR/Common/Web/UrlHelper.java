/*******************************************************************************
 * Educational Online Test Delivery System Copyright (c) 2014 American
 * Institutes for Research
 * 
 * Distributed under the AIR Open Source License, Version 1.0 See accompanying
 * file AIR-License-1_0.txt or at http://www.smarterapp.org/documents/
 * American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import AIR.Common.Web.Session.HttpContext;
import AIR.Common.Web.Session.Server;

/**
 * @author temp_rreddy
 * 
 */
public class UrlHelper
{
  // / <summary>
  // / Check if the url is HTTP.
  // / </summary>
  public static boolean IsHttpProtocol (String uriString) {
    if (StringUtils.isEmpty (uriString))
      return false;
    return (uriString.startsWith ("http:")) || uriString.startsWith ("https:");
  }

  // / <summary>
  // / Check if the url is FTP.
  // / </summary>
  public static boolean IsFtpProtocol (String uriString) {
    if (StringUtils.isEmpty (uriString))
      return false;
    return (uriString.startsWith ("ftp:"));
  }

  // / <summary>
  // / Check if the url is file path.
  // / </summary>
  public static boolean IsFileProtocol (String uriString) {
    if (StringUtils.isEmpty (uriString))
      return false;
    return (uriString.startsWith ("file:"));
  }

  // TODO Ravi/Shiva required for web layer.
  // <summary>
  // The base URL of the site
  // </summary>
  public static String getBase () {
    String contextPath = Server.getContextPath ();
    if (!contextPath.endsWith ("/"))
      contextPath = contextPath + "/";
    return contextPath;
  }

  public static String resolveUrl (String relativePath) {
    return Server.resolveUrl (relativePath);
  }

  public static String resolveFullUrl (String virtualPath) throws URISyntaxException {
    return Server.resolveUrl (virtualPath);
  }

  public static String buildUrl (String firstPart, String secondPart) {
    String separatorChar = "/";
    if (StringUtils.endsWith (firstPart, "/") || StringUtils.startsWith (secondPart, "/"))
      separatorChar = "";
    return String.format ("%s%s%s", firstPart, separatorChar, secondPart);
  }
}
