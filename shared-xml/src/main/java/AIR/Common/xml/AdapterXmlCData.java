/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang3.StringUtils;

/*
 * copied from
 * http://lauraliparulo.altervista.org/jaxb-part-4-handling-cdata-elements/
 */
public class AdapterXmlCData extends XmlAdapter<String, String>
{

  @Override
  public String marshal (String value) throws Exception {
    return "<![CDATA[" + value + "]]>";
  }

  @Override
  public String unmarshal (String value) throws Exception {
    return value;
  }

}
