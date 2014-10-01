/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Browser;

import AIR.Common.Web.BrowserOS;

public class BrowserRule
{

  private int           _priority;
  private BrowserOS     _osName;
  private double        _osMinVersion;
  private double        _osMaxVersion;
  private String        _architecture;
  private String        _name;
  private double        _minVersion;
  private double        _maxVersion;
  private BrowserAction _action;
  private String        _messageKey;

  public BrowserRule ()
  {
	  _priority = 0;
	  _osName = BrowserOS.Unknown;
	  _osMinVersion = 0;
	  _osMaxVersion = 0;
	  _architecture = "*";
	  _name = "*";
	  _minVersion = 0;
	  _maxVersion = 0;
	  _action = BrowserAction.Allow;
  }

  public BrowserRule (int priority, BrowserOS osName, double osMinVersion, double osMaxVersion, String architecture, String name, double minVersion, double maxVersion, BrowserAction action)
  {
    _priority = priority;
    _osName = osName;
    _osMinVersion = osMinVersion;
    _osMaxVersion = osMaxVersion;
    _architecture = architecture;
    _name = name;
    _minVersion = minVersion;
    _maxVersion = maxVersion;
    _action = action;
  }

  public int getPriority () {
    return _priority;
  }

  public void setPriority (int _priority) {
    this._priority = _priority;
  }

  public BrowserOS getOsName () {
    return _osName;
  }

  public void setOsName (BrowserOS _osName) {
    this._osName = _osName;
  }

  public double getOsMinVersion () {
    return _osMinVersion;
  }

  public void setOsMinVersion (double _osMinVersion) {
    this._osMinVersion = _osMinVersion;
  }

  public double getOsMaxVersion () {
    return _osMaxVersion;
  }

  public void setOsMaxVersion (double _osMaxVersion) {
    this._osMaxVersion = _osMaxVersion;
  }

  public String getArchitecture () {
    return _architecture;
  }

  public void setArchitecture (String _architecture) {
    this._architecture = _architecture;
  }

  public String getName () {
    return _name;
  }

  public void setName (String _name) {
    this._name = _name;
  }

  public double getMinVersion () {
    return _minVersion;
  }

  public void setMinVersion (double _minVersion) {
    this._minVersion = _minVersion;
  }

  public double getMaxVersion () {
    return _maxVersion;
  }

  public void setMaxVersion (double _maxVersion) {
    this._maxVersion = _maxVersion;
  }

  public BrowserAction getAction () {
    return _action;
  }

  public void setAction (BrowserAction _action) {
    this._action = _action;
  }

  public String getMessageKey () {
    return _messageKey;
  }

  public void setMessageKey (String _messageKey) {
    this._messageKey = _messageKey;
  }

}
