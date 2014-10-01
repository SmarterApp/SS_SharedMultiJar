/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class XmlElement
{
  private static final long serialVersionUID = -9066651765605712764L;
  Element                   _element;

  public XmlElement (Element ele) {
    this._element = ele;
  }

  public List<Element> selectNodes (String xpath) {
    XPathFactory factory = XPathFactory.instance ();
    XPathExpression<Element> expr = factory.compile (xpath, Filters.element ());
    return expr.evaluate (_element);
  }

  public List<Element> selectNodes (String xpath, XmlNamespaceManager nsmgr) {
    XPathFactory factory = XPathFactory.instance ();
    XPathExpression<Element> expr = factory.compile (xpath, Filters.element (), null, nsmgr.getNamespaceList ());
    return expr.evaluate (_element);
  }

  public Element selectSingleNode (String xpath, XmlNamespaceManager nsmgr) {
    XPathFactory factory = XPathFactory.instance ();
    XPathExpression<Element> expr = factory.compile (xpath, Filters.element (), null, nsmgr.getNamespaceList ());
    return expr.evaluateFirst (_element);
  }

  public List<Element> getElementsByTagName (String name) {
    Iterator<Element> it = _element.getDescendants (Filters.element ());
    List<Element> list = new ArrayList<Element> ();
    while (it.hasNext ()) {
      Element e = it.next ();
      if (e.getName ().equals (name)) {
        list.add (e);
      }
    }
    return list;
  }

  public String getOuterXml () {
    XMLOutputter outp = new XMLOutputter ();
    outp.setFormat (Format.getCompactFormat ());
    String returnString = "";
    try (StringWriter sw = new StringWriter ()) {
      outp.output (_element, sw);
      returnString = sw.getBuffer ().toString ();
    } catch (IOException e1) {
      e1.printStackTrace ();
    }
    return returnString;
  }

  public String getInnerXml () {
    XMLOutputter outp = new XMLOutputter ();

    outp.setFormat (Format.getCompactFormat ());
    try (StringWriter sw = new StringWriter ()) {
      outp.outputElementContent (_element, sw);
      return sw.toString ();
    } catch (IOException e1) {
      e1.printStackTrace ();
      throw new XmlReaderException (e1);
    }
  }
}
