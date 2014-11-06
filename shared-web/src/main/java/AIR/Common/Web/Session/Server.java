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
package AIR.Common.Web.Session;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

import AIR.Common.Utilities.Path;

/**
 * @author Shiva BEHERA [sbehera@air.org]
 * 
 */
public class Server
{
  private static String         _contextPath = null;
  private static String         _docBasePath = null;
  private static ServletContext _context     = null;

  public static void setServletContext (ServletContext value) {
    _context = value;
  }

  public static void setContextPath (String value) {
    _contextPath = value;
  }

  public static String getContextPath () {
    return _contextPath;
  }

  public static void setDocBasePath (String value) {
    _docBasePath = value;
  }

  public static String getDocBasePath () {
    return _docBasePath;
  }

  // TODO Shiva we have a problem here. the problem is virtual paths like "."
  // and "~/" or "./xyz" or "~/xyz" will not be mapped properly
  // to real path.
  public static String mapPath (String virtualPath) {
    if (Path.isAbsolute (virtualPath))
      return virtualPath;
    // if the path begins with "~/" then remove the "~/" as it will be mapped to
    // the
    // realpath.
    // TODO shiva/sajib are there any other such cases e.g. "./"?
    if (virtualPath.startsWith ("~/"))
      virtualPath = virtualPath.substring (2);

    if (virtualPath.indexOf (getContextPath ()) == 0)
      virtualPath = virtualPath.substring (getContextPath ().length ());

    return _context.getRealPath (virtualPath);
  }

  /*
   * relative path may have a "~" in it. the o
   */
  // TODO Shiva/Sajib. We may need to revisit this.
  // TODO this is very hacky!!!
  public static String resolveUrl (String relativePath) {
    if (StringUtils.isEmpty (relativePath))
      relativePath = "";
    // Shiva/Sajib: Do not do .toLowerCase()
    if (StringUtils.startsWith (relativePath, "http"))
      return relativePath;
    if (StringUtils.startsWith (relativePath, "~/")) {
      return getContextPath () + relativePath.substring (1);
    } else if (StringUtils.startsWith (relativePath, "/"))
      return getContextPath () + relativePath;
    return getContextPath () + "/" + relativePath;
  }

}
