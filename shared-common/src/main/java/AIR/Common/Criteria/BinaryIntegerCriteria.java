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

public class BinaryIntegerCriteria extends Criteria<Integer>
{
  private int _minimum = 0;
  private int _maximum = 0;

  public BinaryIntegerCriteria (String sName, String sDesc, boolean bEnabled, int nMinimum, int nMaximum, int nDefault) {
    setName (sName);
    setDescription (sDesc);
    setEnabled (bEnabled);
    setMinimum (nMinimum);
    setMaximum (nMaximum);
    setDefaultValue (nDefault);
  }

  public int getMinimum () {
    return _minimum;
  }

  public void setMinimum (int minimum) {
    this._minimum = minimum;
  }

  public int getMaximum () {
    return _maximum;
  }

  public void setMaximum (int maximum) {
    this._maximum = maximum;
  }

  @Override
  public String getDefaultValueString () {
    return String.valueOf (getDefaultValue ());
  }

  @Override
  public boolean meets (String strVal, _Ref<String> sMessage) {
    boolean bMet = false;
    sMessage.set ("");
    int bVal = Integer.parseInt (strVal);
    bMet = meets (bVal);
    if (!bMet)
      sMessage.set (String.format ("Value %s given for %s is outside the range: %d-%d", strVal, getName (), getMinimum (), getMaximum ()));
    return bMet;
  }

  private boolean meets (int nValue) {
    return (getMinimum () <= nValue && nValue <= getMaximum ());
  }
}
