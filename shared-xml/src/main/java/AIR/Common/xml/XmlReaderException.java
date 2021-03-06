/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
/**
 * 
 */
package AIR.Common.xml;

/**
 * @author Shiva BEHERA [sbehera@air.org]
 * 
 */
public class XmlReaderException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public XmlReaderException (Exception exp) {
    super (exp);
  }

  /**
   * @param string
   */
  public XmlReaderException (String string) {
    super (string);
  }
}
