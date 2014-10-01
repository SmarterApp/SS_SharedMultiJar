/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Utilities;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public class Dates
{

  public static Date convertXST_EST (Date sourceDateTime, int offset) {
    // int offset = AppConfigSingleton.Instance.TimeZoneOffset;
    // NOTE: Larry wants to ignore the daylight saving
    // if (DateTime.Today.IsDaylightSavingTime())
    // offset += 1;

    return DateUtils.addHours (sourceDateTime, offset);
  }

  public static Date convertEST_XST (Date sourceDateTime, int offset) {
    // int offset = AppConfigSingleton.Instance.TimeZoneOffset;
    // NOTE: Larry wants to ignore the daylight saving
    // if (DateTime.Today.IsDaylightSavingTime())
    // offset += 1;

    return DateUtils.addHours (sourceDateTime, offset * -1);
  }

  /**
   * @param date
   * @return returns the midnight time for this date e.g. if date is
   *         "10/11/2012 12:10 pm" then this will return "10/11/2012 00:00 am"
   */
  public static Date getStartOfDayDate (Date date) {
    Calendar calendarInstance = Calendar.getInstance ();
    calendarInstance.setTime (date);
    calendarInstance.set (Calendar.HOUR_OF_DAY, 0);
    calendarInstance.set (Calendar.MINUTE, 0);
    calendarInstance.set (Calendar.SECOND, 0);
    return calendarInstance.getTime ();
  }

  /**
   * @param date
   * @return returns the midnight time for before date ends e.g. if date is
   *         "10/11/2012 12:10 pm" then this will return "10/11/2012 11:59 pm"
   */
  public static Date getEndOfDayDate (Date date) {
    Calendar calendarInstance = Calendar.getInstance ();
    calendarInstance.setTime (date);
    calendarInstance.set (Calendar.HOUR_OF_DAY, 23);
    calendarInstance.set (Calendar.MINUTE, 59);
    calendarInstance.set (Calendar.SECOND, 0);
    return calendarInstance.getTime ();
  }

  public static Timestamp getTimestamp (Date date) {
    if (date == null)
      return null;
    // TODO Shiva what about timezone?
    Timestamp ts = new Timestamp (date.getTime ());
    return ts;
  }
}
