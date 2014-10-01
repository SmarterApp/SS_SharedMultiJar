/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Sql;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.sql.Connection;
import java.text.SimpleDateFormat;

import TDS.Shared.Exceptions.ReturnStatusException;
import AIR.Common.DB.SQLConnection;

/**
 * @author efurman
 * 
 */
public abstract class AbstractDateUtilDll
{
  public static final String DB_DATETIME_FORMAT              = "yyyy-MM-dd HH:mm:ss";
  public static final String DB_DATETIME_FORMAT_MS_PRECISION = "yyyy-MM-dd HH:mm:ss.SSS";
  protected Date             _now                            = null;
  protected Date             _midnightAM                     = null;
  protected Date             _midnightPM                     = null;

  public abstract Date getDate (Connection connection) throws SQLException;

  public abstract Date getDateWRetStatus (Connection connection) throws ReturnStatusException;

  public void calculateMidnights (SQLConnection connection, int timezoneOffset) throws SQLException {
    if (_now == null)
      _now = getDate (connection);
    Calendar calendar = Calendar.getInstance ();
    calendar.setTime (_now);

    calendar.add (Calendar.HOUR_OF_DAY, (-1 * timezoneOffset));
    calendar.set (Calendar.HOUR_OF_DAY, 0);
    calendar.set (Calendar.MINUTE, 0);
    calendar.set (Calendar.SECOND, 0);
    calendar.set (Calendar.MILLISECOND, 0);

    calendar.add (Calendar.HOUR_OF_DAY, timezoneOffset);
    _midnightAM = calendar.getTime ();

    calendar.add (Calendar.HOUR_OF_DAY, 24);
    _midnightPM = calendar.getTime ();
  }

  public Date getMidnightAM () throws SQLException {
    if (_midnightAM == null)
      throw new SQLException ("Must calculateMidnighs before getting it");
    return _midnightAM;
  }

  public Date getMidnightPM () throws SQLException {
    if (_midnightPM == null)
      throw new SQLException ("Must calculateMidnighs before getting it");
    return _midnightPM;
  }

  public static String getDateAsFormattedString (Date date)
  {
    return new SimpleDateFormat (DB_DATETIME_FORMAT).format (date);
  }

  public static String getDateAsFormattedMillisecondsString (Date date)
  {
    return new SimpleDateFormat (DB_DATETIME_FORMAT_MS_PRECISION).format (date);
  }
}
