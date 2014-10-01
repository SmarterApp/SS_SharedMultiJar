/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Exceptions;

import javax.servlet.ServletException;

public class TDSHttpException extends ServletException
{

  /**
   * 
   */
  private static final long   serialVersionUID    = 1L;
  // TODO Shiva should this be initialized to something else instead?
  private int                 _httpStatusCode     = 0;
  private String              _httpStatusMessage  = null;

  private final static String HTTP_MESSAGE_FORMAT = "Status Code: %s ; Status Message: %s";

  public TDSHttpException (int statusCode, String message) {
    super (String.format (HTTP_MESSAGE_FORMAT, "" + statusCode, message));
    this._httpStatusCode = statusCode;
    this._httpStatusMessage = message;
  }

  public int getHttpStatusCode () {
    return _httpStatusCode;
  }

  public String getHttpStatusMessage () {
    return _httpStatusMessage;
  }
}
