/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.TDSLogger;

import javax.servlet.http.HttpServletRequest;

/**
 * @author efurman
 *
 */
public interface ITDSLogger
{
  // If msg parm is null and exception parm is not null,
  // msg will be extracted from exception.
  
  // If method using APIs below is not part of
  // Controller layer, i.e. it does not have access to HttpServletRequest,
  //  you can pass null as httpservletrequest parameter.
  
  // All calls from Controller level must pass valid httpservletrequest parameter.
  // If contoller method does not have HttpServletRequest in its mapping methods,
  // it can be added.
  // Example:
  //@RequestMapping (value = "MasterShell.axd/getTests")
  //@ResponseBody
  //public ResponseData<List<TestSelection>> getTests 
  // (@RequestParam (value = "grade", required = false) String grade)
  
  // may be changed into:
  //@RequestMapping (value = "MasterShell.axd/getTests")
  //@ResponseBody
  //public ResponseData<List<TestSelection>> getTests 
  // (@RequestParam (value = "grade", required = false) String grade,
  // HttpServletRequest request) 
  
  // All calls to _logger.xxx preceeding calls to tdsLogger APIs
  // mist be removed.They were moved inside tdsLogger APIs calls.
  
  
  public void applicationError (String msg, String methodName, HttpServletRequest request, Exception ex); 
  
  public void applicationWarn (String msg, String methodName, HttpServletRequest request, Exception ex) ;
  
  public void applicationInfo (String msg, String methodName, HttpServletRequest request) ;
    
  public void applicationFatal (String msg, String methodName, Exception ex);
  
  public void configFatal (String msg, String methodName, Exception ex);
  public void configError (String msg, String methodName, Exception ex);

  public void sqlWarn (String msg, String methodName) ;

  //public void sqlError (String msg,  String methodName) ;

  public void javascriptError (String msg, String details, String methodName, HttpServletRequest request);

  public void javascriptCritical (String msg,  String details, String methodName, HttpServletRequest request) ;

  public void javascriptInfo (String msg,  String details, String methodName, HttpServletRequest request) ;
  
  public void rendererWarn (String msg,  String methodName);
}
