/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web.Session;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class HttpRequestLoggerInitializerFilter implements Filter
{
  private static long         sequenceNumber   = 0;
  private String              prefix           = null;
  @SuppressWarnings ("unused")
  private boolean             logRequestBodies = false;
  private static final Logger logger           = LoggerFactory.getLogger (HttpRequestLoggerInitializerFilter.class);

  @Override
  public void init (FilterConfig filterConfig) throws ServletException {
    prefix = filterConfig.getInitParameter ("prefix");
    String strLogRequestBodies = filterConfig.getInitParameter ("LogRequestBodies");
    logRequestBodies = (strLogRequestBodies != null) && (!"false".equalsIgnoreCase (strLogRequestBodies));
  }

  @Override
  public void doFilter (final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    final String requestPath = StringUtils.defaultString (httpRequest.getRequestURI ());
    MDC.put ("requestSequence", getNextSequence ());
    MDC.put ("requestTime", new SimpleDateFormat ("YYYY-MM-dd hh:mm:ss.SSS").format (new Date ()));
    MDC.put ("requestPath", requestPath);
    MDC.put ("safeRequestPath", requestPath.replace ('/', '~').replace ('\\', '~'));
    MDC.put ("prefix", StringUtils.defaultString (prefix, ""));
    HttpServletRequest wrappedRequest = httpRequest;
    if ( logger.isDebugEnabled ()) {
      addRequestDetailsToLoggers (httpRequest);
      // TODO: To log request bodies, more work is needed on the ContentLoggingHttpServletRequest
//      if (logRequestBodies && !(
//          httpRequest.getMethod ().equals ("GET") ||
//          httpRequest.getMethod ().equals ("DELETE"))) {
//        wrappedRequest = new ContentLoggingHttpServletRequest (httpRequest);
//        ((ContentLoggingHttpServletRequest) wrappedRequest).logContent (logger);
//      }
//      else {
//        logger.debug ("<<<<< Request content logging disabled. Set filter parameter LogRequestBodies to enable >>>>>");
//      }
    }

    chain.doFilter (wrappedRequest, response);

    // This tells the appender to close the file with the next logging request
    MDC.put ("close", "true");
    logger.debug ("========================== Request Logging End ===================================");
    MDC.clear ();
  }

  @Override
  public void destroy () {

  }

  @SuppressWarnings ("unchecked")
  private void addRequestDetailsToLoggers (HttpServletRequest request) {
    final String method = request.getMethod ();
    logger.debug ("========================== Request Logging Start ===================================");
    logger.debug ("Method:       " + method);
    logger.debug ("Request URI:  " + request.getRequestURI ());
    logger.debug ("Received at:  " + MDC.get ("requestTime"));
    logger.debug ("Query String: " + request.getQueryString ());
    logger.debug ("<<<<<<<<< Headers start >>>>>>>>>>");

    Enumeration<String> headerNames = request.getHeaderNames ();
    while (headerNames.hasMoreElements ()) {
      String headerName = headerNames.nextElement ();
      String headerValue = request.getHeader (headerName);
      logger.debug (headerName + " : " + headerValue);
    }
    logger.debug ("<<<<<<<<< Headers end >>>>>>>>>>");
  }

  private synchronized String getNextSequence () {
    return String.format ("%012d", ++sequenceNumber);
  }

}
