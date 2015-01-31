/*******************************************************************************
 * Educational Online Test Delivery System Copyright (c) 2014 American
 * Institutes for Research
 * 
 * Distributed under the AIR Open Source License, Version 1.0 See accompanying
 * file AIR-License-1_0.txt or at http://www.smarterapp.org/documents/
 * American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import AIR.Common.Helpers._Ref;
import AIR.Common.xml.IXmlSerializable;
import AIR.Common.xml.TdsXmlOutputFactory;

public class HttpWebHelper
{
  private static final Logger       _logger      = LoggerFactory.getLogger (HttpWebHelper.class);

  private final CloseableHttpClient _client;

  private ThreadLocal<HttpContext>  _contextPool = new ThreadLocal<HttpContext> ()
                                                 {
                                                   @Override
                                                   protected HttpContext initialValue () {
                                                     return new BasicHttpContext ();
                                                   }
                                                 };

  public HttpWebHelper () {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager ();
    _client = HttpClientBuilder.create ().setConnectionManager (connectionManager).build ();
  }

  @PreDestroy
  public void cleanUp () {
    // We hope that, by making the variable inaccessible, we are doing enough to
    // let the GC figure out that it should go away.
    _contextPool = null;

    // Close the client and release its resources
    try {
      _client.close ();
    } catch (IOException e) {
      _logger.error ("Error closing HTTP client connection pool", e);
    }
  }

  public void sendXml (String url, IXmlSerializable inputData) {
    sendXml (url, inputData, new NullOutputStream ());
  }

  public void sendXml (String url, IXmlSerializable inputData, OutputStream outputData) {
    ByteArrayOutputStream out = new ByteArrayOutputStream ();
    HttpPost post = new HttpPost (url);
    post.setHeader ("Content-Type", "text/xml");
    HttpResponse response = null;
    try {
      XMLOutputFactory output = TdsXmlOutputFactory.newInstance ();
      XMLStreamWriter writer = output.createXMLStreamWriter (out);
      inputData.writeXML (writer);
      ByteArrayEntity entity = new ByteArrayEntity (out.toByteArray ());
      post.setEntity (entity);
      response = _client.execute (post, _contextPool.get ());
    } catch (IOException | XMLStreamException e) {
      try {
        outputData.write (String.format ("HTTP client through exception: %s", e.getMessage ()).getBytes ());
      } catch (IOException e1) {
        // Our IOExceptioin through an IOException--bad news!!!
        _logger.error ("Output stream encountered an error while attempting to process an error message", e1);
        _logger.error (String.format ("Original drror message was \"\"", e.getMessage ()));
      }
    }
    if (response != null) {
      HttpEntity responseEntity = response.getEntity ();
      if (responseEntity != null) {
        String responseString = "";
        try {
          responseString = EntityUtils.toString (responseEntity);
          outputData.write (responseString.getBytes ());
        } catch (IOException e) {
          // Our IOExceptioin through an IOException--bad news!!!
          _logger.error ("Output stream encountered an error while attempting to process a reply", e);
          _logger.error (String.format ("Original reply content was \"\"", responseString));
        }
        EntityUtils.consumeQuietly (responseEntity);
      }
    }
  }

  // TODO Shiva: Verify that the usage of this in qti scoring engine is kosher!
  public String submitForm (String url, Map<String, Object> formParameters, int maxTries, _Ref<Integer> httpStatusCode) throws IOException {
    // 1 Create the Apache Commons PostMethod object.
    // 2 Add everything from formParameters to the PostMethod object as
    // parameters. Remember to run .toString on each object.
    // 3 Make POST calls as show in here:
    // http://hc.apache.org/httpclient-3.x/tutorial.html
    // 4 One divergence from example code in step 3 is that the whole block
    // needs to go into a for loop that will loop over maxTries time until
    // successful or maxTries reached.
    // 5 If any exception happens then wrap that exception in an IOException.
    // 6 Set httpStatusCode to statusCode from the example.

    for (int i = 1; i <= maxTries; i++) {
      try {

        HttpPost postMethod = new HttpPost (url);

        BasicHttpParams httpParams = new BasicHttpParams ();
        for (Map.Entry<String, Object> entry : formParameters.entrySet ())
          httpParams.setParameter (entry.getKey (), entry.getValue ().toString ());
        postMethod.setParams (httpParams);

        HttpResponse response = _client.execute (postMethod, _contextPool.get ());

        if (response != null) {

          httpStatusCode.set (response.getStatusLine ().getStatusCode ());

          if (httpStatusCode.get () != HttpServletResponse.SC_OK)
            throw new IOException (String.format ("Http Status code is %s", "" + httpStatusCode.get ()));

          HttpEntity responseEntity = response.getEntity ();
          if (responseEntity != null) {
            return EntityUtils.toString (responseEntity);
          } else {
            throw new IOException ("Could not retrieve response; responseEntity is null.");
          }
        } else {
          throw new IOException ("Could not retrieve response; response is null.");
        }
      } catch (IOException e) {
        _logger.error ("Could not retrieve response: ", e.getMessage ());
        System.err.println ("Error encountered:" + e.getMessage ());
        e.printStackTrace ();
        if (i == maxTries)
          throw e;
      }
    }
    throw new IOException ("Could not retrive result.");
  }

  public byte[] getBytes (URL url, String accept) throws IOException {
    HttpGet get;
    try {
      get = new HttpGet (url.toURI ());
    } catch (URISyntaxException e) {
      throw new IOException ("Cannot get a valid URI from the supplied URL", e);
    }
    if (accept != null) {
      get.setHeader ("Accept", accept);
    }
    try (CloseableHttpResponse resp = _client.execute (get)) {
      if (resp.getStatusLine ().getStatusCode () != 200) {
        throw new IOException (String.format ("Received status %d %s from %s, expected 200 OK", resp.getStatusLine ().getStatusCode (), resp.getStatusLine ().getReasonPhrase (), url.toString ()));
      }
      ByteArrayOutputStream stream = new ByteArrayOutputStream ();
      resp.getEntity ().writeTo (stream);
      return stream.toByteArray ();
    }
  }
}
