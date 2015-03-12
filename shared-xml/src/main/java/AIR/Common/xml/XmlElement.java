/*******************************************************************************
 * Educational Online Test Delivery System Copyright (c) 2014 American
 * Institutes for Research
 * 
 * Distributed under the AIR Open Source License, Version 1.0 See accompanying
 * file AIR-License-1_0.txt or at http://www.smarterapp.org/documents/
 * American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Parent;
import org.jdom2.Text;
import org.jdom2.filter.Filters;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class XmlElement
{
  private static final long serialVersionUID = -9066651765605712764L;
  Content                   _element;

  public XmlElement (Content ele) {
    this._element = ele;
  }

  public Parent getParent () {
	return _element.getParent ();
  }

  public XmlElement getParentElement () {
	return new XmlElement(_element.getParentElement ());
  }

  /*
   * XmlElement just wraps a JDOM2 Content object. This call provides access to
   * that Content object directly. TODO : If you need access to the Content
   * object as Element - lets talk about it.
   */
  public Content getContentNode () {
    return _element;
  }

  public String getLocalName () {
    /*
     * We follow this .NET API
     * http://msdn.microsoft.com/en-us/library/system.xml
     * .xmlnode.name(v=vs.110).aspx
     */
    if (_element instanceof Element)
      return ((Element) _element).getName ();
    else if (_element instanceof Comment)
      return "#comment";
    else if (_element instanceof Text)
      return "#text";
    return null;
  }

  // searches descendants for "name" and returns the first one.
  public Element getElement (final String name) {
    if (_element instanceof Parent)
      return XmlHelper.getElement ((Parent) _element, name);
    return null;
  }

  // searchers descendants for "name" and returns all that it finds.
  public List<Element> getElements (final String name) {
    if (_element instanceof Parent)
      return XmlHelper.getElements ((Parent) _element, name);
    return null;
  }

  // searches descendants for "name" in a namespace aware fashion and returns
  // the first one.
  public Element getElement (final String name, Namespace ns) {
    if (_element instanceof Parent)
      return XmlHelper.getElement ((Parent) _element, name, ns);
    return null;
  }

  // searches descendants for "name" in a namespace aware fashion and returns
  // all that it finds.
  public List<Element> getElements (final String name, Namespace ns) {
    if (_element instanceof Parent)
      return XmlHelper.getElements ((Parent) _element, name, ns);
    return null;
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

  public Element selectSingleNode (String xpath) {
    XPathFactory factory = XPathFactory.instance ();
    XPathExpression<Element> expr = factory.compile (xpath, Filters.element ());
    return expr.evaluateFirst (_element);
  }

  public List<Element> getElementsByTagName (String name) {
    List<Element> list = new ArrayList<Element> ();
    if (!(_element instanceof Element))
      return list;

    Iterator<Element> it = ((Element) _element).getDescendants (Filters.element ());

    while (it.hasNext ()) {
      Element e = it.next ();
      if (e.getName ().equals (name)) {
        list.add (e);
      }
    }
    return list;
  }

  public String getInnerTextNormalize () {
	return getInnerText().replace(" ", "");
  }

  public String getInnerText () {
    return _element.getValue ();
  }

  public String getOuterXml () {
    XMLOutputter outp = new XMLOutputter ();
    outp.setFormat (Format.getCompactFormat ());
    String returnString = "";
    try (StringWriter sw = new StringWriter ()) {
      if (_element instanceof Element)
        outp.output ((Element) _element, sw);
      else {
        // TODO: What to do for other types of Content?
        // Note: Document is not derived from Content.
      }
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
      if (_element instanceof Element)
        outp.outputElementContent ((Element) _element, sw);
      else {
        // TODO: What to do for other types of Content?
      }

      return sw.toString ();
    } catch (IOException e1) {
      e1.printStackTrace ();
      throw new XmlReaderException (e1);
    }
  }

  public XmlElement getPreviousSibling () {
    if (_element.getParent () == null)
      return null;

    Parent parent = _element.getParent ();
    int indexForThis = parent.indexOf (_element);
    if (indexForThis > 0)
      return new XmlElement (parent.getContent (--indexForThis));
    return null;
  }

  public XmlElement insertBefore (Content node, Content refNode) {
    if (_element instanceof Element) {
      Element castedElement = (Element) _element;
      int existingPosition = castedElement.indexOf (refNode);
      if (existingPosition  < 0)
    	  existingPosition = 0;
      return new XmlElement (castedElement.addContent (existingPosition, node));
    }
    return null;
  }

  public List<Element> getChildNodes () {
    if (_element instanceof Element) {
      return ((Element) _element).getChildren ();
    }
    return new ArrayList<Element> ();
  }

  public XmlElement getLastChild () {
    if (_element instanceof Element) {
      List<Element> childNodes = getChildNodes ();
      if (childNodes.size () > 0)
        return new XmlElement (childNodes.get (childNodes.size () - 1));
    }
    return null;
  }

  public XmlElement getFirstChild () {
    if (_element instanceof Element) {
      List<Element> childNodes = getChildNodes ();
      if (childNodes.size () > 0)
        return new XmlElement (childNodes.get (0));
    }
    return null;
  }

  // This is tested
  public boolean hasChildNodes () {
    if (_element instanceof Element) {
      List<Element> childNodes = getChildNodes ();
      if (childNodes.size () > 0)
        return true;
    }
    return false;
  }

  public XmlElement removeChild (Content node) {
    if (_element instanceof Parent) {
      Parent castedElement = (Parent) _element;
      // TODO: Minor point. Should we just use .removeContent(Content) instead?
      // That way we can avoid two look ups through the descendants list.
      int existingPosition = castedElement.indexOf (node);
      return new XmlElement (castedElement.removeContent (existingPosition));
    }
    return null;
  }

  /*
   * This is a helper API to getParent above. In getParent we are forced to
   * return an object of type Parent. However, the rest of our API relies on
   * Content. Hence this API will take a Parent object and return an XmlElement
   * object if the Parent object is of type Content. The only other case we need
   * to think about is when Parent object inherits from Docuemnt rather than
   * Content. In that case we will return the root element of Parent object.
   */
  public static XmlElement getXmlElementForParent (Parent parent) {
    if (parent instanceof Content)
      return new XmlElement ((Content) parent);
    else if (parent instanceof Document) {
      return new XmlElement (((Document) parent).getRootElement ());
    }
    // TODO : how to print a more valid log ? I cannot find anything in the
    // Parent class.
    throw new NotProperJDomNodeException (String.format ("getXmlElementForParent() failed as the argument is neither of type Content nor of type Document. Parent: %s.", parent.toString ()));
  }
  
  @Override public boolean equals( Object aThat ) {
	  if ( this == aThat ) return true;
	  if ( !(aThat instanceof XmlElement) ) return false;
	  XmlElement that = (XmlElement)aThat;
	  return (this.getLocalName().equals(that.getLocalName()) &&
			  this.getChildNodes().size() == that.getChildNodes().size() &&
			  this.getInnerText().equals(that.getInnerText()));
	  }

	  @Override public int hashCode() {
	    return (this.getLocalName().hashCode() + this.getChildNodes().size() + this.getInnerTextNormalize().hashCode());
	  }

}
