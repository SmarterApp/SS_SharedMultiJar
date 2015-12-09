/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import AIR.Common.xml.IXmlSerializable;
import AIR.Common.xml.TdsXmlOutputFactory;
import AIR.Common.xml.XmlReader;

public class HttpWebHelperTest
{

  public static Logger _logger = LoggerFactory.getLogger (HttpWebHelperTest.class);
  
  public HttpWebHelper _httpWebHelper;
  @Before
  public void setUp() {
    _httpWebHelper = new HttpWebHelper();
  }
  
  @After
  public void tearDown() {
    _httpWebHelper.cleanUp ();
    _httpWebHelper = null;
  }

  @Test
  public void SendXmlTest () throws IOException, InterruptedException, XMLStreamException {

    MockData data = new MockData ("This is a test");

    IdiotServer server = new IdiotServer (8888);
    server.start ();

    _httpWebHelper.sendXml ("http://localhost:8888/", data);
    server.join (1000);
    if (server.isAlive ()) {
      server.interrupt ();
      throw new AssertionError ("Server did not receive request");
    }
    assertTrue (server.isSucceeded ());
    assertNull (server.getError ());
    String response = server.getResponse ();
    _logger.info (response);
    ByteArrayOutputStream databytes = new ByteArrayOutputStream ();
    XMLOutputFactory factory = TdsXmlOutputFactory.newInstance ();
    XMLStreamWriter writer = factory.createXMLStreamWriter (databytes);
    data.writeXML (writer);
    assertEquals (response, databytes.toString ());

    assertTrue (server.getHeaders ().get (0).startsWith ("POST"));
    boolean foundContentType = false;
    for (String header : server.getHeaders ()) {
      _logger.info (header);
      if (header.equals ("Content-Type: text/xml")) {
        foundContentType = true;
      }
    }
    assertTrue (foundContentType);
  }

  public static class IdiotServer extends Thread
  {
    private final ServerSocket _socket;
    private boolean            _succeeded = false;
    private String             _response  = null;
    private Throwable          _error     = null;
    private List<String>       _headers   = new ArrayList<String> ();

    public IdiotServer (int port) throws IOException {
      super ();
      _socket = new ServerSocket (port);
    }

    public boolean isSucceeded () {
      return _succeeded;
    }

    public List<String> getHeaders () {
      return _headers;
    }

    public String getResponse () {
      return _response;
    }

    public Throwable getError () {
      return _error;
    }

    @Override
    public void run () {
      try (
          Socket clientSocket = _socket.accept ();
          InputStream input = clientSocket.getInputStream ();
          Reader reader = new InputStreamReader (input);
          BufferedReader lineReader = new BufferedReader (reader);) {

        int contentLength = 0;

        String line = lineReader.readLine ().trim ();
        while (!StringUtils.isEmpty (line)) {
          _headers.add (line);
          if (line.startsWith ("Content-Length:")) {
            contentLength = Integer.valueOf (line.substring (15).trim ());
          }
          line = lineReader.readLine ().trim ();
        }

        if (contentLength > 0) {
          char[] chars = new char[contentLength];
          lineReader.read (chars);
          _response = new String (chars);
        }

        clientSocket.getOutputStream ().write ("HTTP/1.1 200 OK".getBytes ());
        _succeeded = true;
      } catch (Throwable t) {
        _error = t;
      }

    }
  }

  public static class MockData implements IXmlSerializable
  {

    private final String _data;

    public MockData (String data) {
      _data = data;
    }

    @Override
    public void readXML (XmlReader src) {
      // Do nothing
    }

    @Override
    public void writeXML (XMLStreamWriter out) throws XMLStreamException {
      out.writeStartDocument ();
      out.writeStartElement ("data");
      out.writeCharacters (_data);
      out.writeEndElement ();
      out.writeEndDocument ();
      out.close ();
    }
  }

}
