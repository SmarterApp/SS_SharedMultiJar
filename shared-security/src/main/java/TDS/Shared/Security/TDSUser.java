/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import TDS.Shared.Data.ReturnStatus;

public class TDSUser
{
  private String       _id;
  private List<String> _roles  = new ArrayList<String> ();
  private long         _key;                              // user key in
                                                           // tblUser
  private long         _entitykey;                        // RTS entity key
  private String       _password;
  private String       _fullname;

  private String       _clientname;
  // UserCookie userInfoCookie;
  public String        _rtsPassword;
  private boolean      _isAuth = false;                   // is authenticated
  private boolean      _isNew  = false;                   // is the user just

  // login?

  @JsonProperty ("ClientName")
  public String getClientName () {
    return _clientname;
  }

  public void setClientName (String clientName) {
    this._clientname = clientName;
  }

  @JsonProperty ("Key")
  public long getKey () {
    return _key;
  }

  public void setKey (long key) {
    this._key = key;
  }

  @JsonProperty ("EntityKey")
  public long getEntityKey () {
    return _entitykey;
  }

  public void setEntityKey (long entitykey) {
    this._entitykey = entitykey;
  }

  @JsonProperty ("ID")
  public String getId () {
    return _id;
  }

  public void setId (String id) {
    this._id = id;
  }

  @JsonProperty ("Password")
  public String getPassword () {
    return _password;
  }

  public void setPassword (String password) {
    this._password = password;
  }

  @JsonProperty ("Roles")
  public List<String> getRoles () {
    return _roles;
  }

  public void setRoles (List<String> roles) {
    this._roles = roles;
  }

  public void addRole (String role) {
    this._roles.add (role);
  }

  public String getRoles (String delimiter) {
    return StringUtils.join (_roles.iterator (), delimiter);
  }

  @JsonProperty ("FullName")
  public String getFullname () {
    return _fullname;
  }

  public void setFullname (String fullname) {
    this._fullname = fullname;
  }

  @JsonProperty ("IsAuth")
  public boolean isAuth () {
    return _isAuth;
  }

  public void setAuth (boolean isAuth) {
    this._isAuth = isAuth;
  }

  @JsonProperty ("IsNew")
  public boolean isNew () {
    return _isNew;
  }

  public void setNew (boolean isNew) {
    this._isNew = isNew;
  }

  public String getRTSPassword () {
    return _rtsPassword;
  }

  public void setRTSPassword (String rTSPassword) {
    _rtsPassword = rTSPassword;
  }

  public boolean HasRole (String role) {
    if (this._roles == null || this._roles.size () < 1)
      return false;
    for (int i = 0; i < this._roles.size (); i++) {
      // sequential search since very few
      if (role.equals (this._roles.get (i)))
        return true;
    }
    return false;
  }

  public ReturnStatus ReturnedStatus;

  public ReturnStatus getReturnedStatus () {
    return ReturnedStatus;
  }

  public void setReturnedStatus (ReturnStatus returnedStatus) {
    this.ReturnedStatus = returnedStatus;
  }

  public String printUser () {
    // TO DO
    String test = "";
    // return
    // String.Format("<div>Key: {0}</div><div>UserID: {1}</div><div>UserRoles: {2}</div>",
    // Key, ID, Roles);
    return test;
  }
}
