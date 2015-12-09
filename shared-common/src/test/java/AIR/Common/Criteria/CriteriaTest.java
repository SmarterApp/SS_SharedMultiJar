/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Criteria;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import AIR.Common.Helpers._Ref;

public class CriteriaTest
{

  @Before
  public void setUp () throws Exception {
  }

  @Test
  public void testUnaryBoolCriteria () {
    String sName = "BoolTest";
    String sDesc = "Test Boolean Criteria";
    boolean bEnabled = true;
    boolean bExpected = true;
    boolean bDefault = false;
    UnaryBoolCriteria criteria = new UnaryBoolCriteria (sName, sDesc, bEnabled, bExpected, bDefault);

    _Ref<String> sMesg = new _Ref<String> ();
    boolean bCurrentValue = true;
    boolean bRet = criteria.meets (Boolean.toString (bCurrentValue), sMesg);
    assertEquals (sMesg.get (), bExpected, bRet);

    String sExpectedDefaultValue = Boolean.toString (bDefault);
    String sCurrentDefaultValue = criteria.getDefaultValueString ();
    assertTrue (String.format ("Display value does not match: Expected:%s, Actual: %s", sExpectedDefaultValue, sCurrentDefaultValue), sExpectedDefaultValue.equals (sCurrentDefaultValue));
  }

  @Test
  public void testBinaryIntegerCriteria () {
    String sName = "IntegerTest";
    String sDesc = "Test Integer Criteria";
    boolean bEnabled = true;
    int nMinimum = 100;
    int nMaximum = 200;
    int nDefault = 150;
    BinaryIntegerCriteria criteria = new BinaryIntegerCriteria (sName, sDesc, bEnabled, nMinimum, nMaximum, nDefault);

    _Ref<String> sMesg = new _Ref<String> ();
    int nCurrentValue = 175;
    boolean bExpected = true;
    boolean bRet = criteria.meets (Integer.toString (nCurrentValue), sMesg);
    assertEquals (sMesg.get (), bExpected, bRet);

    String sExpectedDefaultValue = Integer.toString (nDefault);
    String sCurrentDefaultValue = criteria.getDefaultValueString ();
    assertTrue (String.format ("Display value does not match: Expected:%s, Actual: %s", sExpectedDefaultValue, sCurrentDefaultValue), sExpectedDefaultValue.equals (sCurrentDefaultValue));
  }

  @Test
  public void testBinaryFloatCriteria () {
    String sName = "FloatTest";
    String sDesc = "Test Float Criteria";
    boolean bEnabled = true;
    float fMinimum = 100;
    float fMaximum = 200;
    float fDefault = 150;
    BinaryFloatCriteria criteria = new BinaryFloatCriteria (sName, sDesc, bEnabled, fMinimum, fMaximum, fDefault);

    _Ref<String> sMesg = new _Ref<String> ();
    float fCurrentValue = 175;
    boolean bExpected = true;
    boolean bRet = criteria.meets (Float.toString (fCurrentValue), sMesg);
    assertEquals (sMesg.get (), bExpected, bRet);

    Float fCurrentDefaultValue = Float.parseFloat (criteria.getDefaultValueString ());
    assertTrue (String.format ("Display value does not match: Expected:%f, Actual: %f", fDefault, fCurrentDefaultValue), fDefault == fCurrentDefaultValue);
  }

  @Test
  public void testBinaryDoubleCriteria () {
    String sName = "DoubleTest";
    String sDesc = "Test Double Criteria";
    boolean bEnabled = true;
    double fMinimum = 100;
    double fMaximum = 200;
    double fDefault = 150;
    BinaryDoubleCriteria criteria = new BinaryDoubleCriteria (sName, sDesc, bEnabled, fMinimum, fMaximum, fDefault);

    _Ref<String> sMesg = new _Ref<String> ();
    double fCurrentValue = 175;
    boolean bExpected = true;
    boolean bRet = criteria.meets (Double.toString (fCurrentValue), sMesg);
    assertEquals (sMesg.get (), bExpected, bRet);

    double fCurrentDefaultValue = Double.parseDouble (criteria.getDefaultValueString ());
    assertTrue (String.format ("Display value does not match: Expected:%f, Actual: %f", fDefault, fCurrentDefaultValue), fDefault == fCurrentDefaultValue);
  }

}
