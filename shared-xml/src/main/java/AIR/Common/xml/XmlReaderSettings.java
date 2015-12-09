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
public class XmlReaderSettings
{
  private boolean _ignoreWhitespace = false;
  private Object  _xmlResolver      = null;
  private boolean _prohibitDtd      = false;

  public boolean getIgnoreWhitespace () {
    return _ignoreWhitespace;
  }

  public void setIgnoreWhitespace (boolean value) {
    _ignoreWhitespace = value;
  }

  // TODO Shiva
  public Object getXmlResolver () {
    return _xmlResolver;
  }

  public void setXmlResolver (Object value) {
    _xmlResolver = value;
  }

  public boolean getProhibitDtd () {
    return _prohibitDtd;
  }

  public void setProhibitDtd (boolean value) {
    _prohibitDtd = value;
  }
}
