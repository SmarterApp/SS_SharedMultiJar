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
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A time-unit independent representation of the span between to instants in
 * time.
 * 
 * @author temp_dmenes
 */
public class TimeSpan implements Comparable<TimeSpan>
{
  public static final TimeSpan MIN_VALUE   = new TimeSpan (Long.MIN_VALUE, TimeUnit.NANOSECONDS);

  public static final TimeSpan MAX_VALUE   = new TimeSpan (Long.MAX_VALUE, TimeUnit.NANOSECONDS);

  public static final TimeSpan ZERO        = new TimeSpan (0, TimeUnit.NANOSECONDS);

  private static final String  TIME_FORMAT = "%d days, %d hours, %d minutes and %5.3f seconds";

  private final long           _nanos;

  /**
   * Create a TimeSpan between two instances of java.util.Date.
   * 
   * If either of the dates is an instance of java.sql.Timestamp, then the
   * nanoseconds component of the timestamp will be accounted for.
   * 
   * @param start
   *          Beginning of interval
   * @param end
   *          Length of interval
   */
  public TimeSpan (Date start, Date end) {
    _nanos = getNanos (end) - getNanos (start);
  }

  /**
   * Create a TimeSpan corresponding to an arbitrary count of an arbitrary time
   * unit.
   * 
   * @param duration
   * @param unit
   */
  public TimeSpan (long duration, TimeUnit unit) {
    _nanos = TimeUnit.NANOSECONDS.convert (duration, unit);
  }

  /**
   * Get the length of time represented by the TimeSpan, in a specified time
   * unit
   * 
   * @param unit
   * @return
   */
  public long getDuration (TimeUnit unit) {
    return unit.convert (_nanos, TimeUnit.NANOSECONDS);
  }

  /**
   * Get the number of seconds between 0 and 59 inclusive
   */
  public double getSeconds () {
    double nanos = (double) (_nanos % 60_000_000_000L);
    return nanos * 1e-9;
  }

  public int getMinutes () {
    return (int) (_nanos / 60_000_000_000L) % 60;
  }

  public int getHours () {
    return (int) (_nanos / (60 * 60_000_000_000L)) % 24;
  }

  public int getDays () {
    return (int) (_nanos / (24 * 60 * 60_000_000_000L));
  }

  public String toString () {
    return String.format (TIME_FORMAT, getDays (), getHours (), getMinutes (), getSeconds ());
  }

  /**
   * Convenience function to convert a date to nanoseconds from the epoch
   * 
   * @param d
   * @return
   */
  public static long getNanos (Date d) {
    long nanos = d.getTime () * 1_000_000L;
    if (d instanceof Timestamp) {
      nanos += ((Timestamp) d).getNanos () % 1_000_000;
    }
    return nanos;
  }

  @Override
  public int compareTo (TimeSpan o) {
    return Long.compare (_nanos, o._nanos);
  }

  @Override
  public int hashCode () {
    return new Long (_nanos).hashCode ();
  }

  @Override
  public boolean equals (Object o) {
    if (this == o)
      return true;
    if (!(o instanceof TimeSpan))
      return false;
    return _nanos == ((TimeSpan) o)._nanos;
  }

  public static TimeSpan min (TimeSpan t1, TimeSpan t2) {
    if (t1._nanos < t2._nanos)
      return t1;
    return t2;
  }

  public static TimeSpan max (TimeSpan t1, TimeSpan t2) {
    if (t1._nanos > t2._nanos)
      return t1;
    return t2;
  }

}
