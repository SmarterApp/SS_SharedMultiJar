/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web.Session;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;

public class ContentLoggingHttpServletRequest extends HttpServletRequestWrapper
{
  private byte[] content;

  public ContentLoggingHttpServletRequest (HttpServletRequest request) throws IOException {
    super (request);
    InputStream is = super.getInputStream();
    content = IOUtils.toByteArray(is);
    // TODO: Need to implement parsing of body parameters
  }
  
  public ContentLoggingHttpServletRequest (HttpServletRequest request, byte[] content) {
    super (request);
    this.content = content;
  }

  public void logContent( Logger logger ) throws IOException {
    logger.debug ("<<<<<<<<< Body start >>>>>>>>>>");
    logger.debug (IOUtils.toString (getReader()));
    logger.debug ("<<<<<<<<< Body end >>>>>>>>>>");
  }
  
  
  
  @Override
  public ServletRequest getRequest () {
        return new ContentLoggingHttpServletRequest( (HttpServletRequest) super.getRequest (), content );
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
      return this.new ServletInputStreamImpl();
  }

  @Override
  public BufferedReader getReader() throws IOException {
      String enc = getCharacterEncoding();
      if(enc == null) enc = "UTF-8";
      return new BufferedReader(new InputStreamReader(getInputStream(), enc));
  }
  
  

  @Override
  public String getParameter (String name) {
    // TODO Need to implement parameters
    throw new NotImplementedException("For proper function, parameter parsing must be implemented on ContentLoggingHttpServletRequest");
  }

  @Override
  public Map<String,?> getParameterMap () {
    // TODO Need to implement parameters
    throw new NotImplementedException("For proper function, parameter parsing must be implemented on ContentLoggingHttpServletRequest");
  }

  @Override
  public Enumeration<String> getParameterNames () {
    // TODO Need to implement parameters
    throw new NotImplementedException("For proper function, parameter parsing must be implemented on ContentLoggingHttpServletRequest");
  }

  @Override
  public String[] getParameterValues (String name) {
    // TODO Need to implement parameters
    throw new NotImplementedException("For proper function, parameter parsing must be implemented on ContentLoggingHttpServletRequest");
  }



  private class ServletInputStreamImpl extends ServletInputStream {

    private final ByteArrayInputStream is;
    
    public ServletInputStreamImpl( ) {
      is = new ByteArrayInputStream(content);
    }
    
    @Override
    public int read () throws IOException {
      return is.read ();
    }

    @Override
    public int read (byte[] b) throws IOException {
      return is.read (b);
    }

    @Override
    public int read (byte[] b, int off, int len) throws IOException {
      return is.read (b, off, len);
    }

    @Override
    public long skip (long n) throws IOException {
      return is.skip (n);
    }

    @Override
    public int available () throws IOException {
      return is.available ();
    }

    @Override
    public void close () throws IOException {
      is.close ();
    }

    @Override
    public synchronized void mark (int readlimit) {
      is.mark (readlimit);
    }

    @Override
    public synchronized void reset () throws IOException {
      is.reset ();
    }

    @Override
    public boolean markSupported () {
      return is.markSupported ();
    }
  }
}
