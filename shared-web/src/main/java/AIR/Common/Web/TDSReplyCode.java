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
 * @author mpatel
 *
 */
public enum TDSReplyCode {
      OK(0),
      Error(1),
      Denied(2),
      ReturnStatus(3);
      
      TDSReplyCode(long code) {
        this.code = code;
      }
      
      private long code;

      public long getCode () {
        return code;
      }

}
