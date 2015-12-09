/*******************************************************************************
 * Educational Online Test Delivery System Copyright (c) 2014 American
 * Institutes for Research
 * 
 * Distributed under the AIR Open Source License, Version 1.0 See accompanying
 * file AIR-License-1_0.txt or at http://www.smarterapp.org/documents/
 * American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Utilities;

import org.apache.commons.lang3.StringUtils;

public class MathUtils
{

  /*
   * This works like the Math.truncate in .NEt.
   */
  // TODO Shiva: I looked but could not find any easy implementation.
  public static double truncate (double d) {
    if (d < 0)
      return -1 * Math.floor (d * -1);
    else
      return Math.floor (d);
  }

  /*
   * returns the truncated integer value for doubleValue string. this does not
   * do any error checking.
   */
  public static double parseAndTruncate (String doubleValue) {
    doubleValue = StringUtils.trim (doubleValue);
    return truncate (Double.parseDouble (doubleValue));
  }
}
