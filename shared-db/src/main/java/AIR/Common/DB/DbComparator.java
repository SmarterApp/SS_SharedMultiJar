/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.DB;

import java.util.Date;
import java.util.UUID;

public class DbComparator
{
  /********************** greaterThan ****************************/

  public static boolean greaterThan (Integer one, Integer two) {
    if (two == null || one == null)
      return false;

    if (one > two)
      return true;

    return false;
  }

  public static boolean greaterThan (Float one, Float two) {
    if (two == null || one == null)
      return false;

    if (one > two)
      return true;

    return false;
  }

  public static boolean greaterThan (Long one, Long two) {
    if (two == null || one == null)
      return false;

    if (one > two)
      return true;

    return false;
  }

  public static <T extends Number, S extends Number> boolean greaterThan (T one, S two) {
    if (two == null || one == null)
      return false;

    if (one.doubleValue () > two.doubleValue ())
      return true;

    return false;
  }

  public static boolean greaterThan (UUID one, UUID two) {
    if (two == null || one == null)
      return false;

    if (one.compareTo (two) > 0)
      return true;

    return false;
  }

  public static boolean greaterThan (Date one, Date two) {
    if (two == null || one == null)
      return false;

    if (one.compareTo (two) > 0)
      return true;

    return false;
  }

  /**************************** lessThan ********************************************/
  public static boolean lessThan (Integer one, Integer two) {
    if (two == null || one == null)
      return false;

    if (one < two)
      return true;

    return false;
  }

  public static boolean lessThan (Float one, Float two) {
    if (two == null || one == null)
      return false;

    if (one < two)
      return true;

    return false;
  }

  public static boolean lessThan (Long one, Long two) {
    if (two == null || one == null)
      return false;

    if (one < two)
      return true;

    return false;
  }

  public static <T extends Number, S extends Number> boolean lessThan (T one, S two) {
    if (two == null || one == null)
      return false;

    if (one.doubleValue () < two.doubleValue ())
      return true;

    return false;
  }

  public static boolean lessThan (UUID one, UUID two) {
    if (two == null || one == null)
      return false;

    if (one.compareTo (two) < 0)
      return true;

    return false;
  }

  public static boolean lessThan (Date one, Date two) {
    if (two == null || one == null)
      return false;

    if (one.compareTo (two) < 0)
      return true;

    return false;
  }

  /************************* greaterOrEqual **************************************/
  public static boolean greaterOrEqual (Integer one, Integer two) {
    if (two == null || one == null)
      return false;

    if (one >= two)
      return true;

    return false;
  }

  public static boolean greaterOrEqual (Float one, Float two) {
    if (two == null || one == null)
      return false;

    if (one >= two)
      return true;

    return false;
  }

  public static boolean greaterOrEqual (Long one, Long two) {
    if (two == null || one == null)
      return false;

    if (one >= two)
      return true;

    return false;
  }

  public static <T extends Number, S extends Number> boolean greaterOrEqual (T one, S two) {
    if (two == null || one == null)
      return false;

    if (one.doubleValue () >= two.doubleValue ())
      return true;

    return false;
  }

  public static boolean greaterOrEqual (UUID one, UUID two) {
    if (two == null || one == null)
      return false;

    if (one.compareTo (two) >= 0)
      return true;

    return false;
  }

  public static boolean greaterOrEqual (Date one, Date two) {
    if (two == null || one == null)
      return false;

    if (one.compareTo (two) >= 0)
      return true;

    return false;
  }

  /******************************* lessOrEqual *************************************/
  public static boolean lessOrEqual (Integer one, Integer two) {
    if (two == null || one == null)
      return false;

    if (one <= two)
      return true;

    return false;
  }

  public static boolean lessOrEqual (Float one, Float two) {
    if (two == null || one == null)
      return false;

    if (one <= two)
      return true;

    return false;
  }

  public static boolean lessOrEqual (Long one, Long two) {
    if (two == null || one == null)
      return false;

    if (one <= two)
      return true;

    return false;
  }

  public static <T extends Number, S extends Number> boolean lessOrEqual (T one, S two) {
    if (two == null || one == null)
      return false;

    if (one.doubleValue () <= two.doubleValue ())
      return true;

    return false;
  }

  public static boolean lessOrEqual (UUID one, UUID two) {
    if (two == null || one == null)
      return false;

    if (one.compareTo (two) <= 0)
      return true;

    return false;
  }

  public static boolean lessOrEqual (Date one, Date two) {
    if (two == null || one == null)
      return false;

    if (one.compareTo (two) <= 0)
      return true;

    return false;
  }

  /****************************** isEqual ****************************************/
  public static boolean isEqual (String one, String two) {
    if (two == null || one == null)
      return false;
    
    return one.equalsIgnoreCase (two);
  }

  public static boolean isEqual (Object one, Object two) {
    if (two == null || one == null)
      return false;

    return one.equals (two);
  }

  public static <T extends Number, S extends Number> boolean isEqual (T one, S two) {
    if (two == null || one == null)
      return false;

    if (one.doubleValue () == two.doubleValue ())
      return true;

    return false;
  }

  /****************************** notEqual ****************************************/
  public static boolean notEqual (String one, String two) {
    if (two == null || one == null)
      return false;

    return (!one.equalsIgnoreCase (two));
  }

  public static boolean notEqual (Object one, Object two) {
    if (two == null || one == null)
      return false;

    return (!one.equals (two));
  }
  
  public static <T extends Number, S extends Number> boolean notEqual (T one, S two) {
    if (two == null || one == null)
      return false;

    if (one.doubleValue () == two.doubleValue ())
      return false;

    return true;
  }
  /****************************** containsIgnoreCase ******************************/
  /**
   * 
   * @param one
   * @param two
   * @return
   * 
   * return true if String one contains String two ignore cases 
   */
  public static boolean containsIgnoreCase (String one, String two) {
	    if (two == null || one == null)
	      return false;
	    
	    String oneIC = one.toLowerCase();
	    String twoIC = two.toLowerCase();
	    return oneIC.contains(twoIC);
	  }

  //==============================================================================

  public static void main (String[] agrv) {
    Integer oneInt = 6;
    Integer twoInt = 7;
    Date oneDate = new Date ();
    long tm = oneDate.getTime () - 86400000;
    Date twoDate = new Date ();
    twoDate.setTime (tm); // one day behind oneDate
    UUID oneUUID = UUID.randomUUID ();
    UUID twoUUID = UUID.randomUUID ();
    Long oneLong = 5L;
    Long twoLong = 6L;
    String oneStr = "abcd";
    String twoStr = "AbCd";
    boolean comp;

    System.err.println (String.format ("oneInt: %d, twoLong: %d", oneInt, twoLong));
    comp = isEqual (oneInt, twoLong);
    System.err.println (String.format ("isEqual result:%s", comp));
    comp = notEqual (oneInt, twoLong);
    System.err.println (String.format ("notEqual result:%s", comp));
    
    System.err.println (String.format ("oneInt: %d, oneLong: %d", oneInt, oneLong));
    comp = isEqual (oneInt, oneLong);
    System.err.println (String.format ("isEqual result:%s", comp));
    comp = notEqual (oneInt, oneLong);
    System.err.println (String.format ("notEqual result:%s", comp));
    
    System.err.println (String.format ("oneInt: %d, twoInt: %d", oneInt, twoInt));
    comp = notEqual (oneInt, twoInt);
    System.err.println (String.format ("notEqual result:%s", comp));
    comp = isEqual (oneInt, twoInt);
    System.err.println (String.format ("isEqual result:%s", comp));
    comp = lessThan (oneInt, twoInt);
    System.err.println (String.format ("lessThan result:%s", comp));
    comp = greaterThan (oneInt, twoInt);
    System.err.println (String.format ("greaterThan result:%s", comp));

    System.err.println (String.format ("oneDate: %s, twoDate: %s", oneDate.toString (), twoDate.toString ()));
    comp = notEqual (oneDate, twoDate);
    System.err.println (String.format ("notEqual result:%s", comp));
    comp = isEqual (oneDate, twoDate);
    System.err.println (String.format ("isEqual result:%s", comp));
    comp = lessThan (oneDate, twoDate);
    System.err.println (String.format ("lessThan result:%s", comp));
    comp = greaterThan (oneDate, twoDate);
    System.err.println (String.format ("greaterThan result:%s", comp));

    System.err.println (String.format ("oneUUID: %s, twoUUID: %s", oneUUID.toString (), twoUUID.toString ()));
    comp = notEqual (oneUUID, twoUUID);
    System.err.println (String.format ("notEqual result:%s", comp));
    comp = isEqual (oneUUID, twoUUID);
    System.err.println (String.format ("isEqual result:%s", comp));
    comp = lessThan (oneUUID, twoUUID);
    System.err.println (String.format ("lessThan result:%s", comp));
    comp = greaterThan (oneUUID, twoUUID);
    System.err.println (String.format ("greaterThan result:%s", comp));

    System.err.println (String.format ("oneLong: %d, twoInt: %d", oneLong, twoInt));
    comp = notEqual (oneLong, twoInt);
    System.err.println (String.format ("notEqual result:%s", comp));
    comp = isEqual (oneLong, twoInt);
    System.err.println (String.format ("isEqual result:%s", comp));
    comp = lessThan (oneLong, twoInt);
    System.err.println (String.format ("lessThan result:%s", comp));
    comp = greaterThan (oneLong, twoInt);
    System.err.println (String.format ("greaterThan result:%s", comp));

    System.err.println (String.format ("oneStr: %s, twoStr: %s", oneStr, twoStr));
    comp = notEqual (oneStr, twoStr);
    System.err.println (String.format ("notEqual result:%s", comp));
    comp = isEqual (oneStr, twoStr);
    System.err.println (String.format ("isEqual result:%s", comp));

  }
}
