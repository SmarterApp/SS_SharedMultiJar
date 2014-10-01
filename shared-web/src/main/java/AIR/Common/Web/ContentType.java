/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

/**
 *  ContentType enum refers to various ContentType which can be set to HttpServletResponse
 *  @author mpatel
 */
public enum ContentType {
  Text, // text/plain
  Xml, // text/xml
  Html, // text/html
  Json, // application/json
  Javascript // text/javascript
}
