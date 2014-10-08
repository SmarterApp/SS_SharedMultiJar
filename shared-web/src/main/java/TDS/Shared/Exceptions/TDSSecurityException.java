/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Exceptions;

import javax.servlet.http.HttpServletResponse;

public class TDSSecurityException extends TDSHttpException
{
  /**
   * 
   */
  private static final long   serialVersionUID = 1L;
  private final static String ErrorMessage     = "Your browser session has timed out. Please sign in again.";

  public TDSSecurityException () {
    super (HttpServletResponse.SC_FORBIDDEN, ErrorMessage);
  }

  public TDSSecurityException (String message) {
    super (HttpServletResponse.SC_FORBIDDEN, message);
  }

}
