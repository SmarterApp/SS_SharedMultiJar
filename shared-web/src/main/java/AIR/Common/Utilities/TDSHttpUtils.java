/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Utilities;

import javax.servlet.http.HttpServletRequest;


/**
 * @author mpatel
 *
 */
public class TDSHttpUtils
{

  public static String getCompleteRequestURL(HttpServletRequest request) {
    StringBuffer requestURL = request.getRequestURL();
    if (request.getQueryString() != null && !request.getQueryString().isEmpty ()) {
        requestURL.append("?").append(request.getQueryString());
    }
    return requestURL.toString();
  }
}

