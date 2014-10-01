/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TDSStringUtils
{

  // TODO replace with
  // http://docs.oracle.com/javase/1.5.0/docs/api/java/util/Formatter.html#detail
  // Alternates exist here http://www.stringtemplate.org/download.html
  // Also see
  // http://stackoverflow.com/questions/2286648/named-placeholders-in-string-formatting
  // https://code.google.com/p/guava-libraries/
  // TODO Shiva we have a bug here with \
  /*
   * This is a C# style String.Format method.
   */
  /**
   * @deprecated use String.format instead. Read up more at
   *             http://docs.oracle.com
   *             /javase/1.5.0/docs/api/java/util/Formatter.html#detail. For
   *             example instead of writing in "{0} {1} {0}" format write it as
   *             "%1$s %2$s %1$s". Use this method only for porting C# strings
   *             that already exist in the .NET Code.
   */
  @Deprecated
  public static String format (String formatPattern, Object... values) {
    if (formatPattern == null)
      return null;
    if (values == null || values.length == 0)
      return formatPattern;

    for (int counter1 = 0; counter1 < values.length; ++counter1) {
      Object value = values[counter1];
      if (value != null) {
        Pattern p = Pattern.compile ("\\{" + counter1 + "\\}");
        Matcher m = p.matcher (formatPattern);
        formatPattern = m.replaceAll (value.toString ());
      }
    }
    return formatPattern;
  }
}
