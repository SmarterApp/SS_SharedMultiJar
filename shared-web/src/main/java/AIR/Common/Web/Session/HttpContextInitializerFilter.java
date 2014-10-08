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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class HttpContextInitializerFilter extends OncePerRequestFilter
{
  /*
   * This class sets the Http Request / Response objects in a ThreadLocal object
   * so that wherever else we need access to Request/Response we can just say
   * HttpContext.getRequest() and HttpContext.getResponse().
   * 
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax
   * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
   * javax.servlet.FilterChain)
   */
  @Override
  protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

    /*
     * all we want to do here is set the request/response so that wherever we
     * need access to them we do not have to pass those two as parameters.
     */
    HttpContext currentContext = HttpContext.getCurrentContext ();
    try {
      currentContext.clearCache ();
      currentContext.setRequest (request);
      currentContext.setResponse (response);
      // let the chain go.
      chain.doFilter (request, response);
      // now reset the already set request / response to null just to be on the
      // safe side.
    } finally {
      try {
        currentContext.clearContext ();
      } catch (Exception e) {}
    }
  }

  protected String getAlreadyFilteredAttributeName () {
    return "ContextInitializer" + OncePerRequestFilter.ALREADY_FILTERED_SUFFIX;
  }

}
