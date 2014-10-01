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

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Parent;
import org.jdom2.filter.Filters;
import org.jdom2.util.IteratorIterable;

/**
 * @author Shiva BEHERA [sbehera@air.org]
 * 
 */
public class XmlHelper
{
  public static Element getElement (Parent doc, final String name) {
    List<Element> matches = getElements (doc, name);
    if (matches.size () > 0)
      return matches.get (0);
    return null;
  }

  public static List<Element> getElements (Parent doc, final String name) {
    IteratorIterable<Element> matches = doc.<Element> getDescendants (Filters.element (name));
    List<Element> returnList = new ArrayList<Element> ();
    while (matches.hasNext ())
      returnList.add (matches.next ());
    return returnList;
  }

  public static Element getElement (Parent doc, final String name, Namespace ns) {
    List<Element> matches = getElements (doc, name, ns);
    if (matches.size () > 0)
      return matches.get (0);
    return null;
  }

  public static List<Element> getElements (Parent doc, final String name, Namespace ns) {
    IteratorIterable<Element> matches = doc.<Element> getDescendants (Filters.element (name, ns));
    List<Element> returnList = new ArrayList<Element> ();
    while (matches.hasNext ())
      returnList.add (matches.next ());
    return returnList;
  }
}
