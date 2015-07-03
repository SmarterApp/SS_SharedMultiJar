/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import AIR.Common.Utilities.SpringApplicationContext;
import TDS.Shared.Exceptions.TDSHttpException;

/**
 * @author mpatel
 *
 */
public abstract class FileHttpHandler extends HttpServlet
{
  private static final Logger _logger = LoggerFactory.getLogger (FileHttpHandler.class);

  protected boolean _supportRanges = false;

  protected boolean _loggingEnabled = false;

  public boolean isSupportRanges () {
    return _supportRanges;
  }

  public void setSupportRanges (boolean supportRanges) {
    this._supportRanges = supportRanges;
  }
  
  
  
  
  @Override
  protected void service (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

      if(request.getMethod ().equalsIgnoreCase ("POST")){
        throw new TDSHttpException (405, "Method_not_allowed -- POST -- ");
      }
      try {
          staticFileHandler(request,response);
      } catch (Exception e) {
        _logger.error (e.getMessage (),e); 
        throw e;
      }
  }

 
  /**
   * This is called when the request cannot be handed off to IIS
   * @param context
   * @throws URISyntaxException 
   * @throws TDSHttpException 
   * @throws IOException 
   */
  //This Method moved from FileHttpHandler to hear as there is no way to call child class method from the parent
  public void staticFileHandler(HttpServletRequest request, HttpServletResponse response) throws TDSHttpException, IOException
  {
      String physicalPath = overrideExecuteUrlPath(request);
      FileFtpHandler fileFtpHandler = SpringApplicationContext.getBean ("fileFtpHandler", FileFtpHandler.class);
      if (fileFtpHandler.allowScheme(physicalPath))
      {
        fileFtpHandler.processRequestInternal(request, response, physicalPath);
      }
      else
      {
          // In case the path is a file:// syntax, we need to convert to physical file path
          if (UrlHelper.IsFileProtocol(physicalPath))
          {
              try {
                URI pathUri = new URI(physicalPath);
                physicalPath = pathUri.getPath ();
              } catch (URISyntaxException e) {
                _logger.error (e.getMessage (),e);
              }
          }

          if (_supportRanges) StaticFileHandler3.ProcessRequestInternal(request, response, physicalPath);
          else StaticFileHandler2.ProcessRequestInternal(request, response, physicalPath);
      }
  }
  public abstract String overrideExecuteUrlPath(HttpServletRequest request) throws TDSHttpException;
  
  
}
