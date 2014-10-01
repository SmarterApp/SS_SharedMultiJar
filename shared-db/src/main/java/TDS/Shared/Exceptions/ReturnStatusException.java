/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Exceptions;

import java.sql.ResultSet;
import java.sql.SQLException;

import TDS.Shared.Data.ColumnResultSet;
import TDS.Shared.Data.ReturnStatus;
import AIR.Common.DB.results.SingleDataResultSet;

public class ReturnStatusException extends Exception
{
  /**
   * 
   */
  private static final long  serialVersionUID = 1L;
  private final ReturnStatus _returnStatus;

  public ReturnStatus getReturnStatus () {
    return _returnStatus;
  }

  public ReturnStatusException (String message) {
    super (message);
    this._returnStatus = new ReturnStatus ("failed", message);
  }

  public ReturnStatusException (ReturnStatus returnStatus) {
    super (returnStatus.getReason ());
    this._returnStatus = returnStatus;
  }

  public ReturnStatusException (Exception se) {
    super (se);
    this._returnStatus = new ReturnStatus ("failed", se.getMessage ());
    if (se != null)
      this._returnStatus.setHttpStatusCode (500);
  }

  public static void getInstanceIfAvailable (ResultSet reader, String nullReason) throws ReturnStatusException {
    if (reader == null)
      throw new ReturnStatusException (new ReturnStatus ("failed", nullReason));

    getInstanceIfAvailable (reader);
  }

  public static void getInstanceIfAvailable (ResultSet reader) throws ReturnStatusException {
    // do not throw an exception if result set is null
    if (reader != null) {
      if (reader instanceof ColumnResultSet)
        ((ColumnResultSet) reader).checkReturnStatus ();
      else {
        try {
          ReturnStatus returnStatus = ReturnStatus.parse (reader);
          if (returnStatus != null) {
            throw new ReturnStatusException (returnStatus);
          }
        } catch (SQLException se) {
          throw new ReturnStatusException (se);
        }
      }
    }
  }

  //this method should not throw an exception if null is passed to it
  public static void getInstanceIfAvailable (SingleDataResultSet results) throws ReturnStatusException {
    ReturnStatus returnStatus = ReturnStatus.parse (results);
    if (results != null && results.getReturnStatusCheck ()) {
      throw new ReturnStatusException (returnStatus);
    }
  }

  public static void getInstanceIfAvailable (SingleDataResultSet results, String nullReason) throws ReturnStatusException {
    if (results == null || !results.getRecords ().hasNext ())
      throw new ReturnStatusException (new ReturnStatus ("failed", nullReason));

    getInstanceIfAvailable (results);
  }
}
