/*******************************************************************************
 * Educational Online Test Delivery System Copyright (c) 2014 American
 * Institutes for Research
 * 
 * Distributed under the AIR Open Source License, Version 1.0 See accompanying
 * file AIR-License-1_0.txt or at http://www.smarterapp.org/documents/
 * American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.xml;

import javax.xml.stream.XMLInputFactory;

import com.ctc.wstx.stax.WstxInputFactory;

/**
 * @author jmambo
 *
 */
public class TdsXmlInputFactory
{

  public static XMLInputFactory newInstance () {
    XMLInputFactory xmlInputFactory = new WstxInputFactory ();
    xmlInputFactory.setProperty (XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    // TODO Shiva: Talk to John about this. Without the following line the math
    // content has a problem with DTD while loading
    // up itempreview configuration.
    xmlInputFactory.setProperty (XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
    return xmlInputFactory;
  }

}
