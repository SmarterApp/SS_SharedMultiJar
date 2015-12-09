/*******************************************************************************
 * Educational Online Test Delivery System Copyright (c) 2014 American
 * Institutes for Research
 * 
 * Distributed under the AIR Open Source License, Version 1.0 See accompanying
 * file AIR-License-1_0.txt or at http://www.smarterapp.org/documents/
 * American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import AIR.Common.Helpers._Ref;
import AIR.Common.Utilities.TDSStringUtils;
import AIR.Common.Utilities.UrlEncoderDecoderUtils;
import AIR.Common.xml.IXmlSerializable;
import AIR.Common.xml.TdsXmlOutputFactory;

public class HttpWebHelper
{
  private static final Logger       _logger                 = LoggerFactory.getLogger (HttpWebHelper.class);

  private int                       _requestTimeOutInMillis = 10000;
  private final CloseableHttpClient _client;

  private ThreadLocal<HttpContext>  _contextPool            = new ThreadLocal<HttpContext> ()
                                                            {
                                                              @Override
                                                              protected HttpContext initialValue () {
                                                                return new BasicHttpContext ();
                                                              }
                                                            };

  public int getTimeoutInMillis () {
    return _requestTimeOutInMillis;
  }

  public void setTimeoutInMillis (int timeoutInMillis) {
    _requestTimeOutInMillis = timeoutInMillis;
  }

  public HttpWebHelper () {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager (100000000, TimeUnit.SECONDS);
    connectionManager.setMaxTotal (50);
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

  /*
   * http://stackoverflow.com/questions/4767553/safe-use-of-httpurlconnection
   */
  // TODO Shiva: Verify that the usage of this in qti scoring engine is kosher!
  public String submitForm1 (String url, Map<String, Object> formParameters, int maxTries, _Ref<Integer> httpStatusCode) throws IOException {
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

    byte[] encodeDataInBytes = null;

    StringBuilder strn = new StringBuilder ();
    StringBuilder payloadBuilder = new StringBuilder ();
    for (Map.Entry<String, Object> entry : formParameters.entrySet ()) {
      payloadBuilder.append (UrlEncoderDecoderUtils.encode (entry.getKey ()) + "=" + UrlEncoderDecoderUtils.encode (entry.getValue ().toString ()) + "&");
      strn.append (entry.getKey () + "=" + entry.getValue ().toString () + "\n");
    }
    String encodedData = payloadBuilder.toString ();
    // _logger.info ("Python request URL: " + url + " ; request data: " +
    // encodedData);
    encodeDataInBytes = encodedData.getBytes ();
    
    for (int i = 1; i <= maxTries; i++) {
      HttpURLConnection connection = null;
      
      OutputStream os = null;
      BufferedReader rd = null;
      try {
        connection = (HttpURLConnection) new URL (url).openConnection ();
        connection.setConnectTimeout (getTimeoutInMillis ());
        connection.setReadTimeout (getTimeoutInMillis ());
        connection.setDoOutput (true);
        connection.setRequestMethod ("POST");
        connection.setRequestProperty ("Content-Length", "" + encodeDataInBytes.length);
        // connection.setRequestProperty ("Connection", "Close");
        //connection.setRequestProperty ("expect", "100-continue");
        connection.setUseCaches (false);

        os = connection.getOutputStream ();
        os.write (encodeDataInBytes);

        httpStatusCode.set (connection.getResponseCode ());

        // try reading the result from the server. if there is an error we will
        // automatically go to the exception handler
        rd = new BufferedReader (new InputStreamReader (connection.getInputStream ()));

        StringBuilder sb = new StringBuilder ();
        String line = null;
        while ((line = rd.readLine ()) != null)
          sb.append (line + "\n");

        return sb.toString ();
      } catch (Exception e) {

        _logger.error ("Error Url : " + url + " ; post data: " + encodedData);
        _logger.error ("================Start Raw==============\n");
        _logger.error (strn.toString ());
        _logger.error ("================End Raw==============\n");
        _logger.error ("Could not retrieve response: ", e.getMessage ());
        _logger.error ("Stacktrace : " + TDSStringUtils.exceptionToString (e));
        if (i == maxTries)
          throw new IOException (e);
      } finally {
        // close the output stream
        try {
          if (os != null)
            os.close ();
        } catch (Exception e) {
          _logger.error ("Error closing socket output stream: ", e.getMessage ());
          _logger.error ("Stacktrace : " + TDSStringUtils.exceptionToString (e));
          e.printStackTrace ();
        }

        // close the input stream
        try {
          if (rd != null)
            rd.close ();
        } catch (Exception e) {
          _logger.error ("Error closing socket input stream: ", e.getMessage ());
          _logger.error ("Stacktrace : " + TDSStringUtils.exceptionToString (e));
          e.printStackTrace ();
        }

        try {
          if (connection != null) {
            // read the errorStream if any and close it.
            InputStream es = ((HttpURLConnection) connection).getErrorStream ();
            if (es != null) {
              try {
                BufferedReader esBfr = new BufferedReader (new InputStreamReader (es));
                String line = null;
                // read the response body
                while ((line = esBfr.readLine ()) != null) {
                }
              } catch (Exception exp) {
                _logger.error ("Stacktrace : " + TDSStringUtils.exceptionToString (exp));
              }
              es.close ();
            }
          }
        } catch (Exception e) {
          _logger.error ("Printing error stream: ", e.getMessage ());
          _logger.error ("Stacktrace : " + TDSStringUtils.exceptionToString (e));
          e.printStackTrace ();
        }
        /*
         * try { if (connection != null) { connection.disconnect (); } } catch
         * (Exception e) { _logger.error ("Disconnecting socket: ", e.getMessage
         * ()); _logger.error ("Stacktrace : " +
         * TDSStringUtils.exceptionToString (e)); e.printStackTrace (); }
         */
      }
    }

    // for some reason we ended here. just throw an exception.
    throw new IOException ("Could not retrive result.");
  }

  public String submitForm (String url, Map<String, Object> formParameters, int maxTries, _Ref<Integer> httpStatusCode) throws IOException {

    HttpPost post = new HttpPost (url);

    post.setProtocolVersion (new ProtocolVersion ("HTTP", 1, 1));
    post.setConfig (RequestConfig.custom ().setConnectionRequestTimeout (getTimeoutInMillis ()).setConnectTimeout (getTimeoutInMillis ()).setSocketTimeout (getTimeoutInMillis ()).build ());
 
    List<NameValuePair> urlParameters = new ArrayList<NameValuePair> ();

    for (Map.Entry<String, Object> entry : formParameters.entrySet ())
      urlParameters.add (new BasicNameValuePair (entry.getKey (), entry.getValue ().toString ()));

    post.setEntity (new UrlEncodedFormEntity (urlParameters));

    CloseableHttpResponse response = _client.execute (post, _contextPool.get ());
    // get response code.
    httpStatusCode.set (response.getStatusLine ().getStatusCode ());

    BufferedReader rd = new BufferedReader (new InputStreamReader (response.getEntity ().getContent ()));
    StringBuffer result = new StringBuffer ();
    String line = "";
    while ((line = rd.readLine ()) != null) {
      result.append (line);
    }

    rd.close ();
    response.close ();
    post.releaseConnection ();

    return result.toString ();

  }

  /*
   * http://stackoverflow.com/questions/4767553/safe-use-of-httpurlconnection
   * http://hc.apache.org/httpcomponents-client-ga/tutorial/html/fluent.html
   */
  // TODO Shiva: Verify that the usage of this in qti scoring engine is kosher!
  public String submitForm3 (String url, Map<String, Object> formParameters, int maxTries, _Ref<Integer> httpStatusCode) throws IOException {
    Form f = Form.form ();
    for (Map.Entry<String, Object> entry : formParameters.entrySet ())
      f.add (entry.getKey (), entry.getValue ().toString ());

    for (int i = 1; i <= maxTries; i++) {
      try {
        return Request.Post (url).staleConnectionCheck (true).connectTimeout (getTimeoutInMillis ()).socketTimeout (getTimeoutInMillis ()).version (HttpVersion.HTTP_1_1).bodyForm (f.build ()).execute ().returnContent ().asString ();
      } catch (Exception e) {
        httpStatusCode.set (HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        if (i == maxTries)
          throw new IOException (e);
      }
    }

    // for some reason we ended here. just throw an exception.
    throw new IOException ("Could not retrive result.");
  }

  public String getResponse (String urlString, String postBody) throws Exception {
    return getResponse (urlString, postBody, "json");
  }

  /*
   * Shiva: The following getResponse method is just a simple POST
   * implementation for testing purposes. This needs to be refined further
   * before use in production systems.
   */
  public String getResponse (String urlString, String postRequestParams, String contentMimeType) throws Exception {
    try {
      URL url = new URL (urlString);
      HttpURLConnection urlConn = (HttpURLConnection) url.openConnection ();
      urlConn.setRequestProperty ("Content-Type", String.format ("application/%s; charset=UTF-8", contentMimeType));
      urlConn.setUseCaches (false);

      // the request will return a response
      urlConn.setDoInput (true);

      // set request method to POST
      urlConn.setDoOutput (true);

      OutputStreamWriter writer = new OutputStreamWriter (urlConn.getOutputStream ());
      writer.write (postRequestParams);
      writer.flush ();

      // reads response, store line by line in an array of Strings
      BufferedReader reader = new BufferedReader (new InputStreamReader (urlConn.getInputStream ()));

      StringBuffer bfr = new StringBuffer ();

      String line = "";
      while ((line = reader.readLine ()) != null) {
        bfr.append (line + "\n");
      }

      reader.close ();
      writer.close ();
      urlConn.disconnect ();
      return bfr.toString ();

    } catch (Exception exp) {
      StringWriter strn = new StringWriter ();
      exp.printStackTrace (new PrintWriter (strn));
      _logger.error (strn.toString ());
      exp.printStackTrace ();
      throw new Exception (exp);
    }
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
