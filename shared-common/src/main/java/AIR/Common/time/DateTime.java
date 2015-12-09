/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;


/**
 * @author jmambo
 *
 */
public class DateTime
{
  
  
  public static Date getNow() {
    return new Date();
  }
  
  public static String getTodaysDate(String format) {
    return new SimpleDateFormat(format).format(new Date());
  }
  
  public static String getFormattedDate(Date date, String format) {
    return new SimpleDateFormat(format).format(date);
  }
  
  public static String getFormattedDate (Calendar calendar, String format) {
    return new SimpleDateFormat(format).format(calendar.getTime());
  }

  public static String getFormattedDate (XMLGregorianCalendar calendar, String format) {
    return new SimpleDateFormat(format).format(calendar.toGregorianCalendar().getTime());
  }

  
  /**
   * Gets a Date of January 1, 0001
   * 
   * @return Date
   */
  public static Date getMinValue() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(1, 0, 1, 0, 0, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }


  

}
