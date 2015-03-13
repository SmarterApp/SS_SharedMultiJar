/*******************************************************************************
 * Educational Online Test Delivery System Copyright (c) 2014 American
 * Institutes for Research
 * 
 * Distributed under the AIR Open Source License, Version 1.0 See accompanying
 * file AIR-License-1_0.txt or at http://www.smarterapp.org/documents/
 * American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
/**
* 
 */
package AIR.Common.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.Stack;

import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;

/**
 * @author Shiva BEHERA [sbehera@air.org]
 * 
 */
public class XmlReader
{
  private static final Logger                  _logger        = LoggerFactory.getLogger (XmlReader.class);
  private Document                             _doc           = null;

  private Stack<MutablePair<Content, Integer>> _stack         = new Stack<MutablePair<Content, Integer>> ();
  @SuppressWarnings ("unused")
  private Closeable                            _file          = null;
  @SuppressWarnings ("unused")
  private Namespace                            _rootNameSpace = null;

  public XmlReader (InputStream file) throws IOException, JDOMException {
    this (new InputStreamReader (file));
  }

  public XmlReader (Reader file) throws IOException, JDOMException {
    // Start hack! Shiva: I am not sure why this is happening. but this happened
    // with the earlier .NET code as well.
    // why am I seeing a special character if I directly pass a
    // InputStreamReader to SAXBuilder. I had to do the same hack in
    // ItemScoringHandler.ProcessRequest.
    // I see this message as well
    // "warning: line 6050: incompatible stripping characters and condition"
    // Further information: Elena found two additional links:
    // http://mark.koli.ch/resolving-orgxmlsaxsaxparseexception-content-is-not-allowed-in-prolog
    // and
    // http://stackoverflow.com/questions/5138696/org-xml-sax-saxparseexception-content-is-not-allowed-in-prolog
    BufferedReader bfr = new BufferedReader (file);
    StringBuilder strinBuilder = new StringBuilder ();
    String line = "";
    while ((line = bfr.readLine ()) != null)
      strinBuilder.append (line);
    file.close ();
    String fileContent = strinBuilder.toString ();
    int indexOfBegin = fileContent.indexOf ('<');
    if (indexOfBegin > 0)
      fileContent = fileContent.substring (indexOfBegin);
    file = new StringReader (fileContent);
    // End hack!
    buildDocument (file);
  }

  public static XmlReader create (URI uri) throws XmlReaderException {
    try {
      return new XmlReader (uri.toURL ().openStream ());
    } catch (Exception exp) {
      throw new XmlReaderException (exp);
    }
  }

  public static XmlReader create (String filepath) throws XmlReaderException {
    try {
      return new XmlReader (new FileInputStream (filepath));
    } catch (Exception exp) {
      throw new XmlReaderException (exp);
    }
  }

  public Document getDocument () {
    return _doc;
  }

  public String getAttribute (String attributeName) throws XmlReaderException {
    Element e = getNodeAsElement ();
    if (e != null)
      return e.getAttributeValue (attributeName);
    return null;
  }

  public long getAttributeAsLong (String attributeName) throws XmlReaderException {
    return Long.parseLong (getAttribute (attributeName));
  }

  public boolean getAttributeAsBoolean (String attributeName) {
    return Boolean.parseBoolean (getAttribute (attributeName));
  }

  public Content.CType getNodeType () throws XmlReaderException {
    isEmpty ();
    return _stack.peek ().getLeft ().getCType ();
  }

  public String getName () throws XmlReaderException {
    Element e = getNodeAsElement ();
    if (e != null)
      return e.getName ();
    return null;
  }

  public String readString () throws XmlReaderException {
    isEmpty ();
    return _stack.peek ().getLeft ().getValue ();
  }

  public String readElementContentAsString () throws XmlReaderException {
    return readString ();
  }

  public boolean readToDescendant () throws XmlReaderException {
    return getNextElement (TRAVERSE_TYPE.IMMEDIATE_CHILD_ONLY);
  }

  // TODO simplify and see if we can use another method rather than have all
  // this logic in here
  private boolean readToDescendant (String item) throws XmlReaderException, IOException {
    MutablePair<Content, Integer> alwaysTop = null;
    if (_stack.size () > 0) {
      alwaysTop = _stack.peek ();
    }
    while (_stack.size () != 0) {
      MutablePair<Content, Integer> topElement = _stack.peek ();

      if (topElement.getLeft ().getCType () == CType.Element) {
        Element topElementNode = (Element) topElement.getLeft ();
        if (StringUtils.equals (item, topElementNode.getName ()))
          return true;

        int nextChild = topElement.getRight () + 1;
        if (topElementNode.getChildren ().size () > nextChild) {
          topElement.setRight (nextChild);
          Element nextTraversalNode = topElementNode.getChildren ().get (nextChild);
          _stack.push (new MutablePair<Content, Integer> (nextTraversalNode, -1));
        } else {
          // we do not want to pop the original top node (alwaysTop) as we are
          // only doing descendant.
          if (!alwaysTop.equals (topElement))
            _stack.pop ();
          else
            break;
        }
      }
    }

    return false;
  }

  public boolean readToNextSibling () throws XmlReaderException {
    return getNextElement (TRAVERSE_TYPE.NEXT_SIBLING);
  }

  public boolean readToNextSibling (String item) throws XmlReaderException, IOException {
    while (this.traverseNextSibling ()) {
      if (StringUtils.equals (item, getName ()))
        return true;
    }
    return false;
  }

  public boolean read () throws XmlReaderException {
    // if either the InputStream is empty or our stack is empty, we do not have
    // any more elements.
    return getNextElement (TRAVERSE_TYPE.ANY);
  }

  public boolean readToFollowing (String item) throws XmlReaderException, IOException {
    if (StringUtils.equals (item, getName ()))
      return true;
    while (read ()) {
      if (StringUtils.equals (item, getName ()))
        return true;
    }
    return false;
  }

  public void moveToElement () {
    // Shiva: No need to do anything here. we are already in the node unlike in
    // .NET when
    // the pointer shifts to an attribute.
  }

  public void close () {
    try {
      if (_file != null) {
        _file.close ();
        _file = null;
      }
    } catch (IOException exp) {
      _logger.error (exp.getMessage ());
    }
  }

  @Override
  public void finalize () {
    close ();
  }

  public boolean isEmptyElement () {
    Content thisNode = _stack.peek ().getLeft ();
    switch (thisNode.getCType ()) {
    case CDATA:
    case Text:
    case Comment:
      return true;
    case Element:
      Element thisElement = getNodeAsElement ();
      return thisElement.getChildren ().size () > 0;
    }
    return true;
  }

  private boolean getNextElement (TRAVERSE_TYPE type) {
    try {
      switch (type) {
      case ANY:
        return traverseAny ();
      case IMMEDIATE_CHILD_ONLY:
        return traverseImmediateChild (true);
      case NEXT_SIBLING:
        return traverseNextSibling ();
      }
    } catch (IOException exp) {
      throw new XmlReaderException (exp);
    }
    return true;
  }

  private boolean traverseNextSibling () throws IOException {
    /*
     * if (_file.available () == 0) { return false; }
     */
    if (_stack.size () == 0)
      return false;

    MutablePair<Content, Integer> currentTop = _stack.pop ();
    if (_stack.size () != 0) {

      MutablePair<Content, Integer> topElement = _stack.peek ();
      // We already went into topElement's children and so there is no
      // need to check if it of type element.
      Element topElementNode = (Element) topElement.getLeft ();

      int nextChild = topElement.getRight () + 1;
      if (topElementNode.getChildren ().size () > nextChild) {
        topElement.setRight (nextChild);
        Element nextTraversalNode = topElementNode.getChildren ().get (nextChild);
        _stack.push (new MutablePair<Content, Integer> (nextTraversalNode, -1));
        return true;
      }
    }
    // else put the previous top back on the top.
    _stack.push (currentTop);
    return false;
  }

  private boolean traverseAny () throws IOException {
    /*
     * if (_file.available () == 0) { return false; }
     */
    if (_stack.size () == 0)
      return false;

    // if we have a next child then we will go to that.
    // else we will back up to the parent and find its next child.
    if (traverseImmediateChild (false))
      return true;
    else {
      // so, the current top of stack has no/more children. so we will pop it
      // and back up the stack until we find one
      while (_stack.size () > 0) {
        _stack.pop ();
        if (traverseImmediateChild (false))
          return true;
      }
    }
    // no more nodes;
    return false;
  }

  private boolean traverseImmediateChild (boolean firstOnly) throws IOException {
    /*
     * if (_file.available () == 0) { return false; }
     */
    if (_stack.size () == 0)
      return false;

    MutablePair<Content, Integer> topElement = _stack.peek ();
    Content node = topElement.getLeft ();
    if (node.getCType () == CType.Element) {
      Element topElementNode = (Element) node;
      int nextChild = 0;
      if (!firstOnly)
        nextChild = topElement.getRight () + 1;
      // if we have a next child then we will go to that.
      if (topElementNode.getChildren ().size () > nextChild) {
        topElement.setRight (nextChild);
        Element nextTraversalNode = topElementNode.getChildren ().get (nextChild);
        _stack.push (new MutablePair<Content, Integer> (nextTraversalNode, -1));
        return true;
      }
    }
    return false;
  }

  private boolean isEmpty () {
    if (_stack.size () == 0)
      throw new XmlReaderException ("No more content");
    return true;
  }

  private enum TRAVERSE_TYPE {
    ANY, IMMEDIATE_CHILD_ONLY, NEXT_SIBLING
  };

  private Element getNodeAsElement () {
    isEmpty ();
    Content node = _stack.peek ().getLeft ();
    if (node.getCType () == CType.Element)
      return (Element) node;
    return null;
  }

  public static void main (String[] args) {
    try {

      InputStream input = new FileInputStream ("C:/WorkSpace/TDSCore/AppsCurrent/Student/XmlReaderApplication/books3.xml");
      XmlReader reader = new XmlReader (input);
      reader.read ();
      reader.read ();
      reader.readToDescendant ();
      System.err.println (reader.getName ());
      System.err.println (reader.readString ());
      reader.readToDescendant ("y");
      System.err.println (reader.getName ());
      System.err.println (reader.readString ());
    } catch (Exception exp) {
      exp.printStackTrace ();
    }

    System.err.println ("");
  }

  private void buildDocument (Closeable file) throws JDOMException, IOException {
    SAXBuilder builder = new SAXBuilder ();
    if (file instanceof InputStream)
      _doc = builder.build ((InputStream) file);
    else if (file instanceof Reader)
      _doc = builder.build ((Reader) file);
    _stack.push (new MutablePair<Content, Integer> (_doc.getRootElement (), -1));
    _rootNameSpace = _doc.getRootElement ().getNamespace ();
    _file = file;
  }
}
