/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import AIR.Common.DB.results.DbResultRecord;
import AIR.Common.DB.results.SingleDataResultSet;

// TODO Oksana / Shiva fix this code. The Exists method is wrong.
public class ReturnStatus
{

  // / <summary>
  // / A generic delegate for people to use when creating custom gettext
  // functions.
  // / </summary>
  // public delegate String GetMessageText(String key, String context, bool
  // database);

  // / <summary>
  // / This class is used to parse return status from TDS session stored
  // procedures.
  // / </summary>

  // / <summary>
  // / A custom function for getting the i18n translation for the reason.
  // / </summary>
  // / <remarks>
  // / If you want to make use of this you should assign a function here when
  // your application initializes.
  // / This is not thread safe to assign a function. But it should be thread
  // safe to call this function.
  // / </remarks>

  // TODO
  // public static GetMessageText GetCustomMessageText;

  private String _status;
  private String _reason;
  private String _context        = "Default";
  private String _appKey;
  private int    _httpStatusCode = 200;

  @JsonIgnore
  public String getAppKey () {
    return _appKey;
  }

  public void setAppKey (String appKey) {
    this._appKey = appKey;
  }

  @JsonProperty ("status")
  public String getStatus () {
    return _status;
  }

  public void setStatus (String status) {
    this._status = status;
  }

  @JsonIgnore
  public int getHttpStatusCode () {
    return _httpStatusCode;
  }

  public void setHttpStatusCode (int code) {
    _httpStatusCode = code;
  }

  @JsonProperty ("reason")
  public String getReason () {
    return _reason;
  }

  public void setReason (String reason) {
    this._reason = reason;
  }

  @JsonIgnore
  public String getContext () {
    return _context;
  }

  public void setContext (String context) {
    this._context = context;
  }

  public ReturnStatus (String status) {
    this._status = status;
  }

  public ReturnStatus (String status, String reason) {
    this._status = status;
    this._reason = reason;
  }

  public ReturnStatus (String status, String reason, int httpStatusCode) {
    this._status = status;
    this._reason = reason;
    this._httpStatusCode = httpStatusCode;
  }
  
  public ReturnStatus (String status, String reason, String context) {
    this._status = status;
    this._reason = reason;
    this._context = context;
  }

  // TODO Oksana/Shiva Exists method is all wrong. Look at the line where we
  // check for both "reason" and "status". The logic is messed up and besides
  // that we should be using HasColumn.
  // / <summary>
  // / Does this reader have a return status
  // / </summary>
  public static boolean exists (ResultSet reader) throws SQLException {
    boolean result = false;
    ColumnResultSet columnResultSet = ColumnResultSet.getColumnResultSet (reader);
    if (columnResultSet.hasColumn ("status") && columnResultSet.hasColumn ("reason")) {
      result = true;
    }
    return result;
  }

  public static boolean exists (SingleDataResultSet reader) {
    boolean result = false;
    if (reader.hasColumn ("status") && reader.hasColumn ("reason")) {
      result = true;
    }
    return result;
  }

  // / <summary>
  // / Parse any current return status data.
  // / </summary>
  public static ReturnStatus parse (ResultSet reader) throws SQLException {
    return parseInternal (reader);
  }

  public static ReturnStatus parse (SingleDataResultSet result) {
    if (result == null || !exists (result))
      return null;

    DbResultRecord record = result.getRecords ().next ();
    return parse (record);
  }

  public static ReturnStatus parse (DbResultRecord result) {
    // TODO Ravi/Shiva should we do an "exists()" check here?
    if (result != null) {
      ReturnStatus returnStatus = new ReturnStatus (result.<String> get ("status"));
      try {
        returnStatus._reason = result.<String> get ("reason");
        // TODO mpatel remove it once code point to Elena's new DLL Code
        // Following Catch block is to handle null values - In case reason is
        // null metadata returns Integer value for reason and throwing classcast
        // exception
      } catch (ClassCastException e) {
        if (result.get ("reason") != null && result.get ("reason").equals ("0")) {
          returnStatus._reason = "";
        }
      }
      if (result.hasColumn ("context")) {
        try {
          returnStatus._context = result.<String> get ("context");
        } catch (ClassCastException e) {
          System.out.println (result.get ("context"));
          if (result.get ("context") != null && result.get ("context").equals ("0")) {
            returnStatus._context = "";
          }
        }
      }
      if (result.hasColumn ("appkey"))
        returnStatus._appKey = result.<String> get ("appkey");
      return returnStatus;
    }
    return null;
  }

  // / <summary>
  // / Advance the reader if there is return status data and parse.
  // / </summary>
  // TODO not using anywhere
  public static ReturnStatus readAndParse (ResultSet reader) throws SQLException {
    // check if the reader has return status info
    if (!exists (reader))
      return null;

    // read first record
    reader.first ();

    return parseInternal (reader);
  }

  public static ReturnStatus readAndParse (SingleDataResultSet reader) {
    return parse (reader);
  }

  // TODO Oksana/Shiva This one will not get any data as we have not done
  // reader.next();
  // / <summary>
  // / Call this function on a data reader to get that rows return status
  // info.
  // / </summary>
  private static ReturnStatus parseInternal (ResultSet reader) throws SQLException {
    // check if the reader has return status info
    if (!exists (reader))
      return null;
    // Shiva: I am going to put this reader.next() back in here as all of
    // Elena's code is now reliant on it.
    // I am going to redo the fixes in the SP repositories.
    reader.next ();
    ReturnStatus returnStatus = new ReturnStatus (reader.getString ("status"));
    // get the reason if it exists
    ColumnResultSet columnResultSet = ColumnResultSet.getColumnResultSet (reader);
    if (columnResultSet.hasColumn ("reason")) {
      returnStatus._reason = reader.getString ("reason");
    }
    if (columnResultSet.hasColumn ("context")) {
      returnStatus._context = reader.getString ("context");
    }
    if (columnResultSet.hasColumn ("appkey")) {
      returnStatus._appKey = reader.getString ("appkey");
    }
    return returnStatus;
  }

}
