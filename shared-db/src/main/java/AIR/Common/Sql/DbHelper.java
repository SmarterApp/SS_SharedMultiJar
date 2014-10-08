/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Sql;

import java.util.List;

import org.apache.commons.lang.StringUtils;

public class DbHelper
{

  public static Object isNullifyString (Object value) {
    if (value == null || value.toString ().trim ().equalsIgnoreCase ("") || value.toString ().length () < 1)
      return null;
    else
      return value;
  }

  // TODO Elena/Shiva is there a better way to do this.
  public static String getDbCsvFromString (String csvString, Character delimiter)
  {
    String[] contexts = StringUtils.split (csvString, delimiter == null ? ',' : delimiter);
    StringBuilder strnBuilder = new StringBuilder ();
    for (int counter1 = 0; counter1 < contexts.length; ++counter1)
    {
      if (counter1 != 0)
        strnBuilder.append (",");
      strnBuilder.append (String.format ("'%1$s'", contexts[counter1].trim ()));
    }

    return strnBuilder.toString ();
  }

  // TODO Elena/Shiva is there a better way to do this.
  public static <T> String getDbCsvFromMathTypes (List<T> list)
  {
    // TODO Elena/Shiva
    return StringUtils.join (list, ',');
  }

  public static void main (String[] argv)
  {
    List<Long> x = new java.util.ArrayList<Long> ();
    x.add (new Long (10));
    x.add (new Long (11));

    String z = getDbCsvFromMathTypes (x);
    System.err.println (z);
  }
}
