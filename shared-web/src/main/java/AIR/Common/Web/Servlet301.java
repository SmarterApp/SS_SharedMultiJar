/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple servlet to return a 301 redirect to the server.
 * 
 * Usually used to redirect requests that have no page to a default index page.
 * The target of the redirect should be specified as an init-param to the
 * servlet in the web.xml file, with the name
 * "AIR.Common.Web.Servlet301.REDIRECT_URL"
 * 
 * @author temp_dmenes
 * 
 */
public class Servlet301 extends HttpServlet
{

  public static final String  REDIRECT_URL_INIT_PARAM = "AIR.Common.Web.Servlet301.REDIRECT_URL";

  private static final long   serialVersionUID        = 1L;
  @SuppressWarnings ("unused")
  private static final Logger _logger                 = LoggerFactory.getLogger (Servlet301.class);
  private volatile String     _redirectUrl            = null;
  private volatile boolean    _isAbsolute             = false;

  @Override
  protected void doHead (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doGet (req, resp);
  }

  @Override
  protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setStatus (301);
    resp.addHeader ("Cache-Control", "no-store, no-cache, must-revalidate");
    resp.addHeader ("Expires", "Thu, 01 Jan 1970 00:00:00 GMT");
    if (_isAbsolute) {
      resp.addHeader ("Location", _redirectUrl);
    }
    else {

      StringBuffer path = req.getRequestURL ();
      int n = path.length ();
      if (req.getPathInfo () != null) {
        n -= req.getPathInfo ().length ();
      }
      n -= req.getServletPath ().length ();
      path.delete (n, path.length ());
      path.append (_redirectUrl);
      resp.addHeader ("Location", path.toString ());
    }
  }

  @Override
  public void init (ServletConfig config) throws ServletException {
    _redirectUrl = config.getInitParameter (REDIRECT_URL_INIT_PARAM);
    _isAbsolute = _redirectUrl.startsWith ("http://");
  }

}
