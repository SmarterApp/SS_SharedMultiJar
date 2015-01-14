/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Criteria;

import AIR.Common.Helpers._Ref;

public class UnaryBoolCriteria extends Criteria<Boolean>
{
  private boolean _expectedValue = false;

  public UnaryBoolCriteria (String sName, String sDesc, boolean bEnabled, boolean bExpected, boolean bDefault) {
    setName (sName);
    setDescription (sDesc);
    setEnabled (bEnabled);
    setExpectedValue (bExpected);
    setDefaultValue (bDefault);
  }

  public boolean getExpectedValue () {
    return _expectedValue;
  }

  public void setExpectedValue (boolean expectedValue) {
    this._expectedValue = expectedValue;
  }

  @Override
  public String getDefaultValueString () {
    return String.valueOf (getDefaultValue ());
  }

  @Override
  public boolean meets (String strVal, _Ref<String> sMessage) {
    boolean bMet = false;
    sMessage.set ("");
    boolean bVal = Boolean.parseBoolean (strVal);
    bMet = meets (bVal);
    if (!bMet)
      sMessage.set (String.format ("Value %s given for %s is not the expected: %b", strVal, getName (), getExpectedValue ()));
    return bMet;
  }

  private boolean meets (boolean bValue) {
    return (bValue == getExpectedValue ());
  }
}
