/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.time;

import java.sql.Timestamp;

public final class TimeUtils
{
  public static Timestamp nowTimestamp () {
    Long now = System.nanoTime ();
    Timestamp value = new Timestamp (now / 1_000_000L);
    value.setNanos (Long.valueOf (now % 1_000_000_000L).intValue ());
    return value;
  }
}
