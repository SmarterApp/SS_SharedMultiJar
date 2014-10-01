/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Messages;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// / <summary>
// / This is used to load translations from an XML file. It will return a flat
// MessageDTO object.
// / </summary>
// / <example>
// /
// / <messageSystem>
// / <contextType name="ServerSide">
// / <context name="Default.aspx">
// / <message id="10926" key="Login.Button.SignIn">
// / <translation lang="ENU"><![CDATA[Sign In]]></translation>
// / <translation lang="ESN"><![CDATA[Iniciar sesi?n]]></translation>
// / </message>
// / </context>
// / </contextType>
// / </messageSystem>
// /
// / </example>
public class MessageXml
{
  Logger                         _logger         = LoggerFactory.getLogger (MessageXml.class);
  private final List<MessageDTO> _messageDTOList = new ArrayList<MessageDTO> ();

  public List<MessageDTO> getMessageDTOs () {
    return _messageDTOList;
  }

  // / <summary>
  // / Loads an XML file into DTO's. If an existing translation exists in DTO it
  // will be replaced.
  // / This allows you to build a hierarchy for projects.
  // / </summary>

  public Iterable<MessageDTO> load (String xmlFile) throws Exception {

    Iterable<MessageDTO> messageDTOs = null;

    try {
      messageDTOs = parse (xmlFile);

    } catch (Exception ex) {
      _logger.error ("Failed to load xml messages", ex);
      throw ex;
    }

    if (messageDTOs != null) {
      _messageDTOList.addAll ((Collection<? extends MessageDTO>) messageDTOs); // todo
    }
    return _messageDTOList;

  }

  private Iterable<MessageDTO> parse (String xmlFile) throws Exception {

    File f = new File (xmlFile);
    if (!f.exists ())
      return null;

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ();
    // then we have to create document-loader:
    DocumentBuilder loader = factory.newDocumentBuilder ();
    Document doc = loader.parse (xmlFile);
    doc.getDocumentElement ().normalize ();

    doc.getDocumentElement ().getNodeName ();

    MessageDTO messageObj = new MessageDTO ();
    if (doc.hasChildNodes ()) {

      messageObj = checkChildNodes (doc.getChildNodes (), messageObj);
    }
    return _messageDTOList;

  }

  private MessageDTO checkChildNodes (NodeList nodeList, MessageDTO messageObj) throws Exception {

    for (int count = 0; count < nodeList.getLength (); count++) {
      messageObj = new MessageDTO ();
      Node tempNode = nodeList.item (count);

      if (tempNode.getNodeName () == "contextType") {
        NamedNodeMap nodeMap = tempNode.getAttributes ();

        ContextType contextType;
        String contextTypeString = nodeMap.item (0).getNodeValue ();
        try {
          contextType = ContextType.getContextTypeFromStringCaseInsensitive (contextTypeString);
        } catch (IllegalArgumentException iar) {
          _logger.error ("ContextType " + contextTypeString + "is not a legal value");
          throw new Exception ("ContextType " + contextTypeString + "is not a legal value");
        }
        messageObj.setContextType (contextType);
      }
      if (tempNode.getNodeName () == "context") {
        NamedNodeMap nodeMap = tempNode.getAttributes ();
        messageObj.setContext (nodeMap.item (0).getNodeValue ());
      }
      if (tempNode.getNodeName () == "message") {
        NamedNodeMap nodeMap = tempNode.getAttributes ();
        messageObj.setMessageId (nodeMap.item (0).getNodeValue ().length ());
        messageObj.setAppKey (nodeMap.item (1).getNodeValue ());
      }

      if (tempNode.getNodeName () == "translation") {
        if (tempNode.getNodeType () == Node.ELEMENT_NODE) {
          messageObj.setMessage (tempNode.getTextContent ());
          if (tempNode.hasAttributes ()) {
            // get attributes names and values
            NamedNodeMap nodeMap = tempNode.getAttributes ();
            for (int i = 0; i < nodeMap.getLength (); i++) {
              Node node = nodeMap.item (i);
              messageObj.setLanguage (node.getNodeValue ());

            }
          }
        }
      }

      if (tempNode.hasChildNodes ()) {
        // loop again if has child nodes
        tempNode.getChildNodes ();

      }
    }
    return messageObj;

  }

}
