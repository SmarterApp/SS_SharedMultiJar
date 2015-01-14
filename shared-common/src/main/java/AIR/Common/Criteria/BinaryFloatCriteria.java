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

public class BinaryFloatCriteria extends Criteria<Float>
{
  private static final float _cfEpsilon = 0.00000001F;
  private float              _minimum   = 0;
  private float              _maximum   = 0;

  public BinaryFloatCriteria (String sName, String sDesc, boolean bEnabled, float fMinimum, float fMaximum, float fDefault) {
    setName (sName);
    setDescription (sDesc);
    setEnabled (bEnabled);
    setMinimum (fMinimum);
    setMaximum (fMaximum);
    setDefaultValue (fDefault);
  }

  public float getMinimum () {
    return _minimum;
  }

  public void setMinimum (float minimum) {
    this._minimum = minimum;
  }

  public float getMaximum () {
    return _maximum;
  }

  public void setMaximum (float maximum) {
    this._maximum = maximum;
  }

  @Override
  public String getDefaultValueString () {
    int i = (int) getDefaultValue ().floatValue ();
    return getDefaultValue () == i ? String.valueOf (i) : String.valueOf (getDefaultValue ());
  }

  @Override
  public boolean meets (String strVal, _Ref<String> sMessage) {
    boolean bMet = false;
    sMessage.set ("");
    float fVal = Float.parseFloat (strVal);
    bMet = meets (fVal);
    if (!bMet)
      sMessage.set (String.format ("Value %s given for %s is outside the range: %f-%f", strVal, getName (), getMinimum (), getMaximum ()));
    return bMet;
  }

  private boolean meets (float fValue) {
    float deltaMin = fValue - getMinimum ();
    float deltaMax = getMaximum () - fValue;
    if (deltaMin >= 0F && deltaMax >= 0F)
      return true;
    if (Math.abs (deltaMin) <= _cfEpsilon || Math.abs (deltaMax) <= _cfEpsilon)
      return true;
    return false;
  }
}
