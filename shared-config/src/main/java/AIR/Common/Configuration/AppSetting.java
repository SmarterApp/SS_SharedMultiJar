/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Configuration;

public class AppSetting<T>
{

  // TODO Shiva any way to make this readonly like in C# ?
  private String _name;

  private T      _value;

  public AppSetting (String name, T value) {
    _name = name;
    _value = value;
  }

  public String getName () {
    return _name;
  }

  public T getValue () {
    return _value;
  }

  public Object getObject () {
    return _value;
  }

  @Override
  public String toString () {
    if (_value == null)
      return "";
    return _value.toString ();
  }
}
