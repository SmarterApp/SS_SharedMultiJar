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

public class BinaryDoubleCriteria extends Criteria<Double>
{
  private static final double _cfEpsilon = 0.00000001F;
  private double              _minimum   = 0;
  private double              _maximum   = 0;

  public BinaryDoubleCriteria (String sName, String sDesc, boolean bEnabled, double fMinimum, double fMaximum, double fDefault) {
    setName (sName);
    setDescription (sDesc);
    setEnabled (bEnabled);
    setMinimum (fMinimum);
    setMaximum (fMaximum);
    setDefaultValue (fDefault);
  }

  public double getMinimum () {
    return _minimum;
  }

  public void setMinimum (double minimum) {
    this._minimum = minimum;
  }

  public double getMaximum () {
    return _maximum;
  }

  public void setMaximum (double maximum) {
    this._maximum = maximum;
  }

  @Override
  public String getDefaultValueString () {
    int i = (int) getDefaultValue ().doubleValue ();
    return getDefaultValue () == i ? String.valueOf (i) : String.valueOf (getDefaultValue ());
  }

  @Override
  public boolean meets (String strVal, _Ref<String> sMessage) {

    boolean bMet = false;
    sMessage.set ("");
    double fVal = Double.parseDouble (strVal);
    bMet = meets (fVal);
    if (!bMet)
      sMessage.set (String.format ("Value %s given for %s is outside the range: %f-%f", strVal, getName (), getMinimum (), getMaximum ()));
    return bMet;
  }

  private boolean meets (double fValue) {
    double deltaMin = fValue - getMinimum ();
    double deltaMax = getMaximum () - fValue;
    if (deltaMin >= 0F && deltaMax >= 0F)
      return true;
    if (Math.abs (deltaMin) <= _cfEpsilon || Math.abs (deltaMax) <= _cfEpsilon)
      return true;
    return false;
  }
}
