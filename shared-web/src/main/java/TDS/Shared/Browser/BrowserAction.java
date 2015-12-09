/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Browser;

import org.apache.commons.lang3.StringUtils;

/**
 * @author temp_rreddy
 * 
 */
public enum BrowserAction {
  Allow, Deny, Warn;

  public static BrowserAction getBrowserActionFromStringCaseInsensitive (String value) {
    for (BrowserAction type : BrowserAction.values ()) {
      if (StringUtils.equalsIgnoreCase (type.name (), value))
        return type;
    }
    return Warn;
  }
}
