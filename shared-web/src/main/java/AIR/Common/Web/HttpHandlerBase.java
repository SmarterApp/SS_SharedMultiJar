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
package AIR.Common.Web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import AIR.Common.Web.Session.HttpContext;
import AIR.Common.data.ResponseData;
import TDS.Shared.Data.ReturnStatus;
import TDS.Shared.Exceptions.ReturnStatusException;
import TDS.Shared.Exceptions.TDSSecurityException;

/**
 * @author Shiva BEHERA [sbehera@air.org]
 * 
 */
public abstract class HttpHandlerBase implements BeanFactoryAware
{
  private Logger      _logger         = LoggerFactory.getLogger (HttpHandlerBase.class);
  private BeanFactory contextBeans    = null;
  private HttpContext _currentContext = HttpContext.getCurrentContext ();

  public void setBeanFactory (BeanFactory beanFactory) throws BeansException {
    this.contextBeans = beanFactory;
    onBeanFactoryInitialized ();
    try {
      // this is just so that we can prepopulate the objects.
      // TODO Shiva should we be doing this at a different location?
      init ();
    } catch (TDSSecurityException exp) {
      _logger.error (exp.getMessage ());
    }
  }

  @ExceptionHandler ({ ReturnStatusException.class })
  @ResponseBody
  public ReturnStatus handleReturnStatusException (ReturnStatusException rse, HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
    //response.setStatus (HttpServletResponse.SC_OK);
    //return exp.getReturnStatus ();
	  
  
   response.setStatus(HttpServletResponse.SC_FORBIDDEN);
   response.setContentType("application/json");
   ObjectMapper mapper = new ObjectMapper();
   ResponseData<ReturnStatus> out = new ResponseData<ReturnStatus> (TDSReplyCode.ReturnStatus.getCode(), rse.getReturnStatus().getReason(), null);
   mapper.writeValue(response.getOutputStream(), out);
   return rse.getReturnStatus();    
  }

  public void setMIMEType(ContentType contentType)
  {
      WebHelper.setContentType(contentType);
  }
  
  public static void writeString(String value) throws IOException
  {
      WebHelper.writeString(value);
  }

  public static void WriteString(String value,Object[] values) throws IOException
  {
      WebHelper.writeString(value, values);
  }
  
  //@ExceptionHandler ({ IOException.class })
  //@ResponseBody
  //public void handleReturnStatusException (IOException exp, HttpServletResponse response) throws ReturnStatusException {
  //  response.setStatus (HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  //  throw new ReturnStatusException (exp);
  //}

  protected HttpContext getCurrentContext () {
    return _currentContext;
  }

  protected <T> T getBean (String name, Class<T> c) {
    return this.contextBeans.getBean (name, c);
  }

  protected abstract void onBeanFactoryInitialized ();

  protected HttpHandlerBase () {

  }
  
  protected void SetMIMEType(ContentType contentType) {
    WebHelper.setContentType (contentType);
  }

  private void init () throws TDSSecurityException {

  }
}
