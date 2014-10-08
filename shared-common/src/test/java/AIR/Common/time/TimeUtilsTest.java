/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.time;

import static org.junit.Assert.*;

import java.sql.Timestamp;

import org.junit.Test;

public class TimeUtilsTest
{

  @Test
  public synchronized void nowTimestampTest () throws InterruptedException {
    // We had a problem where nowTimestamp was recording the fractional second
    // modulo 1 ms.
    // This test is intended to catch that problem

    Timestamp t1 = TimeUtils.nowTimestamp ();
    wait (25);
    Timestamp t2 = TimeUtils.nowTimestamp ();
    Long diff = t2.getTime () - t1.getTime ();
    assertNotEquals (t1.getTime (), t2.getTime ());
    assertTrue (diff > 15 && diff < 45);
  }

}
