/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web.taglib;

public class JsfComponentIncludeException extends RuntimeException
{
  public JsfComponentIncludeException (String message) {
    super (message);
  }

  public JsfComponentIncludeException (String message, Exception exp) {
    super (message, exp);
  }

  public JsfComponentIncludeException (Exception exp) {
    super (exp);
  }

}
