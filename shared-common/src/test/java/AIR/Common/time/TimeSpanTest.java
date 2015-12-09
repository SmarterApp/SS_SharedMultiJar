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
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TimeSpanTest
{

  @Test
  public void testTimeSpanDateDate () {

    Calendar cal = Calendar.getInstance ();
    cal.set (1968, 11, 16, 0, 27, 17);
    Date d1 = cal.getTime ();
    cal.set (1968, 11, 16, 0, 29, 19);
    Date d2 = cal.getTime ();
    TimeSpan OUT = new TimeSpan (d1, d2);
    assertEquals (122_000_000_000L, OUT.getDuration (TimeUnit.NANOSECONDS));

  }

  @SuppressWarnings ("deprecation")
  @Test
  public void testTimeSpanTimestampTimestamp () {

    Timestamp t1 = new Timestamp (2013, 11, 16, 1, 2, 3, 456);
    Timestamp t2 = new Timestamp (2013, 11, 16, 1, 2, 4, 56789);
    TimeSpan OUT = new TimeSpan (t1, t2);
    assertEquals (4_000_056_789L - 3_000_000_456L, OUT.getDuration (TimeUnit.NANOSECONDS));

  }

  @Test
  public void testTimeSpanLongTimeUnit () {
    TimeSpan time_span;
    time_span = new TimeSpan (1, TimeUnit.NANOSECONDS);
    assertEquals (1, time_span.getDuration (TimeUnit.NANOSECONDS));
    time_span = new TimeSpan (1, TimeUnit.MICROSECONDS);
    assertEquals (1_000, time_span.getDuration (TimeUnit.NANOSECONDS));
    time_span = new TimeSpan (1, TimeUnit.MILLISECONDS);
    assertEquals (1_000_000, time_span.getDuration (TimeUnit.NANOSECONDS));
    time_span = new TimeSpan (1, TimeUnit.SECONDS);
    assertEquals (1_000_000_000, time_span.getDuration (TimeUnit.NANOSECONDS));
    time_span = new TimeSpan (1, TimeUnit.MINUTES);
    assertEquals (60_000_000_000L, time_span.getDuration (TimeUnit.NANOSECONDS));
    time_span = new TimeSpan (1, TimeUnit.HOURS);
    assertEquals (3_600_000_000_000L, time_span.getDuration (TimeUnit.NANOSECONDS));
    time_span = new TimeSpan (1, TimeUnit.DAYS);
    assertEquals (24L * 3_600_000_000_000L, time_span.getDuration (TimeUnit.NANOSECONDS));
  }

  @Test
  public void testGetDuration () {
    TimeSpan time_span;
    time_span = new TimeSpan (1, TimeUnit.DAYS);
    assertEquals (24L * 3_600_000_000_000L, time_span.getDuration (TimeUnit.NANOSECONDS));
    assertEquals (24L * 3_600_000_000L, time_span.getDuration (TimeUnit.MICROSECONDS));
    assertEquals (24L * 3_600_000L, time_span.getDuration (TimeUnit.MILLISECONDS));
    assertEquals (24L * 3_600L, time_span.getDuration (TimeUnit.SECONDS));
    assertEquals (24L * 60L, time_span.getDuration (TimeUnit.MINUTES));
    assertEquals (24L, time_span.getDuration (TimeUnit.HOURS));
    assertEquals (1L, time_span.getDuration (TimeUnit.DAYS));
  }

  @Test
  public void testGetNanos () {
    Date d1 = new Date (123_456L);
    assertEquals (123_456_000_000L, TimeSpan.getNanos (d1));
    Timestamp t1 = new Timestamp (0L);
    t1.setNanos (123_456_789);
    assertEquals (123_456_789, TimeSpan.getNanos (t1));
  }

  @SuppressWarnings ("deprecation")
  @Test
  public void testFormat () {
    Date d1 = new Date (2013, 1, 2, 3, 4, 5);
    d1.setTime (d1.getTime () + 678);
    Date d2 = new Date (2013, 1, 1, 1, 1, 1);
    TimeSpan t1 = new TimeSpan (d2, d1);
    assertEquals ("1 days, 2 hours, 3 minutes and 4.678 seconds", t1.toString ());
  }

}
