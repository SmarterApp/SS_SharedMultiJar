/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
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

    if (HttpContext.getCurrentContext () == null)
      return null;
    HttpServletRequest request = HttpContext.getCurrentContext ().getRequest ();
    return request.getContextPath () + "/";
  }

  public static String resolveUrl (String relativePath) {
    return Server.resolveUrl (relativePath);
  }
  
  public static String resolveFullUrl(String virtualPath) throws URISyntaxException
  {
      URI baseUri = new URI(getBase ());
      String resolvedUrl = resolveUrl(virtualPath);
      //URI resolveUri = new URI(baseUri, resolvedUrl);
      // TODO mpatel/Shiva - Check if the following line of code works the same as Dotnet code new URI(URL baseURI, String relativeURI)
      URI resolveUri = baseUri.resolve (resolvedUrl);
      return resolveUri.toString();
  } 

  // TODO Ravi/Shiva need for Web layer.
  /*
   * // / <summary> // / The base URL of the site // / </summary> public static
   * String getBaseWithPort () {
   * 
   * if (HttpContext.getCurrentContext () == null) return null;
   * HttpServletRequest request = HttpContext.getCurrentContext ().getRequest
   * ();
   * 
   * return getProtocol () + request.getServerName () + getPort () +
   * request.getRequestURI () + "/";
   * 
   * }
   */
  /*
   * // / <summary> // / The URL protocol (HTTP or HTTPS) // / </summary> public
   * static String getProtocol () {
   * 
   * if (HttpContext.getCurrentContext () == null) return null;
   * HttpServletRequest request = HttpContext.getCurrentContext ().getRequest
   * (); // TODO // string protocol =
   * request.ServerVariables["SERVER_PORT_SECURE"]; String protocol = (String)
   * request.getAttribute ("SERVER_PORT_SECURE"); return (protocol == null ||
   * protocol == "0") ? "http://" : "https://";
   * 
   * //TODO Ravi read this article
   * //http://www.velocityreviews.com/forums/t147520
   * -am-i-running-under-http-or-https.html return null; }
   */
  // / <summary>
  // / The URL port
  // / </summary>
  // TODO Ravi/Shiva this will be required for UI layer.
  /*
   * public static String getPort () {
   * 
   * if (HttpContext.getCurrentContext () == null) return null;
   * HttpServletRequest request = HttpContext.getCurrentContext ().getRequest
   * ();
   * 
   * String port = request.ServerVariables["SERVER_PORT"];
   * 
   * if (port == null || port == "80" || port == "443") { return ""; }
   * 
   * return ":" + port;
   * 
   * }
   */

  // / <summary>
  // / Returns a site relative HTTP path from a partial path starting out with a
  // ~.
  // / Same syntax that ASP.Net internally supports but this method can be used
  // / outside of the Page framework.
  // /
  // / Works like Control.ResolveUrl including support for ~ syntax
  // / but returns an absolute URL.
  // / </summary>
  // / <param name="virtualPath">Any Url including those starting with ~</param>
  // / <returns>relative url</returns>
  // / <remarks>
  // / Source:
  // http://clientdependency.codeplex.com/SourceControl/changeset/view/73172#1481746
  // / Additional: http://west-wind.com/weblog/posts/154812.aspx
  // / </remarks>
  // TODO Shiva/Ravi We will have to revisit this when we implement the web UI
  // layer.
  /*
   * public static String ResolveUrl (String virtualPath) { if
   * (StringUtils.isEmpty (virtualPath)) return virtualPath;
   * 
   * // *** Absolute path - just return if (IsAbsolutePath (virtualPath)) return
   * virtualPath;
   * 
   * // *** We don't start with the '~' -> we don't process the Url if
   * (!virtualPath.startsWith ("~")) return virtualPath;
   * 
   * // *** Fix up path for ~ root app dir directory // VirtualPathUtility blows
   * up if there is a // query string, so we have to account for this. int
   * queryStringStartIndex = virtualPath.indexOf ('?');
   * 
   * if (queryStringStartIndex != -1) { String queryString =
   * virtualPath.substring (queryStringStartIndex); String baseUrl =
   * virtualPath.substring (0, queryStringStartIndex);
   * 
   * return String.concat (VirtualPathUtility.ToAbsolute (baseUrl,
   * HttpRuntime.AppDomainAppVirtualPath), queryString); }
   * 
   * return VirtualPathUtility.ToAbsolute (virtualPath,
   * HttpRuntime.AppDomainAppVirtualPath); }
   */

  // / <summary>
  // / Checks for an absolute http path
  // / </summary>
  // / <remarks>
  // / Takes into account this type of url:
  // / ~/pathtoresolve/page.aspx?returnurl=http://servertoredirect/resource.aspx
  // / which is not an absolute path but contains the characters to describe it
  // as one.
  // / </remarks>
  // / <param name="virtualPath"></param>
  // / <returns></returns>
  // private static boolean IsAbsolutePath (String virtualPath) {
  // // *** Absolute path - just return
  // int IndexOfSlashes = virtualPath.indexOf ("://");
  // int IndexOfQuestionMarks = virtualPath.indexOf ("?");
  //
  // if (IndexOfSlashes > -1 && (IndexOfQuestionMarks < 0 ||
  // (IndexOfQuestionMarks > -1 && IndexOfQuestionMarks > IndexOfSlashes)))
  // return true;
  //
  // return false;
  // }
  // TODO shiva/ravi implement in web layer
  /*
   * public static String ResolveFullUrl (String virtualPath) { URI baseUri =
   * new URI (getBase ()); String resolvedUrl = ResolveUrl (virtualPath); URI
   * resolveUri = new URI (baseUri, resolvedUrl); return resolveUri.toString ();
   * }
   */

  // / <summary>
  // / Returns the physical file path that corresponds to the specified virtual
  // file path on the Web Server.
  // / </summary>
  // / <remarks>
  // / This wraps up the built in ASP.NET ways of doing this.
  // / </remarks>
  // TODO shiva
  /*
   * public static String MapPath (String virtualPath) {
   * 
   * 
   * return (HttpContext.Current != null) ?
   * HttpContext.Current.Server.MapPath(virtualPath) :
   * HostingEnvironment.MapPath(virtualPath);
   * 
   * }
   */
  public static String buildUrl (String firstPart, String secondPart)
  {
    String separatorChar = "/";
    if (StringUtils.endsWith (firstPart, "/") || StringUtils.startsWith (secondPart, "/"))
      separatorChar = "";
    return String.format ("%s%s%s", firstPart, separatorChar, secondPart);
  }
}
