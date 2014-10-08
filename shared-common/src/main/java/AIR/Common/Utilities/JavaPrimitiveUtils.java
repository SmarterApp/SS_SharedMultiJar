/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Utilities;

import AIR.Common.Helpers._Ref;

public class JavaPrimitiveUtils
{
  public static boolean longTryParse (String value, _Ref<Long> ref) {
    try {
      ref.set (Long.parseLong (value));
      return true;
    } catch (NumberFormatException exp) {
      return false;
    }
  }

  public static boolean intTryParse (String value, _Ref<Integer> ref) {
    try {
      ref.set (Integer.parseInt (value));
      return true;
    } catch (NumberFormatException exp) {
      return false;
    }
  }
  
  public static boolean floatTryParse (String value, _Ref<Float> ref) {
    try {
      ref.set (Float.parseFloat (value));
      return true;
    } catch (NumberFormatException exp) {
      return false;
    }
  }
  
  public static boolean doubleTryParse (String value, _Ref<Double> ref) {
    try {
      ref.set (Double.parseDouble (value));
      return true;
    } catch (NumberFormatException exp) {
      return false;
    }
  }

  public static boolean boolTryParse (String value, _Ref<Boolean> ref) {
    try {
      ref.set (Boolean.parseBoolean (value));
      return true;
    } catch (NumberFormatException exp) {
      return false;
    }
  }

  /*
   * C#: EnumType enumVal; if (Enum.TryParse(valStr, true, out enumVal)) ... }
   * 
   * Java: EnumType enumVal; if (Cs2Java.Enum.tryParse(EnumType.class, valStr,
   * true)) { enumVal = Cs2Java.Enum.parse(EnumType.class, valStr, true); ... }
   */
  public static <E extends java.lang.Enum<E>> boolean enumTryParse (
      Class<E> enumType, String value, boolean ignoreCase, _Ref<E> out) {
    E[] enumsConstants = enumType.getEnumConstants ();
    for (E e : enumsConstants) {
      String n = e.name ();
      if (ignoreCase ? n.equalsIgnoreCase (value) : n.equals (value)) {
        out.set (e);
        return true;
      }
    }
    return false;
  }
}
