/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.xml;


import javax.xml.stream.XMLOutputFactory;

import com.ctc.wstx.stax.WstxOutputFactory;

/**
 * @author jmambo
 *
 */
public class TdsXmlOutputFactory
{

  public static XMLOutputFactory newInstance() {
    return new WstxOutputFactory();
  }
  
}
