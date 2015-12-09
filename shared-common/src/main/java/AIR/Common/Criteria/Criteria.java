/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Criteria;

public abstract class Criteria<T> implements INamedCriteria
{
  private String  _name         = new String ();
  private String  _description  = new String ();
  private boolean _enabled      = false;
  private T       _defaultValue = null;

  @Override
  public String getName () {
    return _name;
  }

  public void setName (String name) {
    this._name = name;
  }

  @Override
  public String getDescription () {
    return _description;
  }

  public void setDescription (String description) {
    this._description = description;
  }

  @Override
  public boolean isEnabled () {
    return _enabled;
  }

  public void setEnabled (boolean enabled) {
    this._enabled = enabled;
  }

  public T getDefaultValue () {
    return _defaultValue;
  }

  public void setDefaultValue (T defaultValue) {
    this._defaultValue = defaultValue;
  }
}
