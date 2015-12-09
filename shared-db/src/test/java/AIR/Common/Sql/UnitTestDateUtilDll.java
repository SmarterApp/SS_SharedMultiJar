/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import AIR.Common.Configuration.ConfigurationSection;
import TDS.Shared.Exceptions.ReturnStatusException;

/**
 * @author efurman
 * 
 */
public class UnitTestDateUtilDll extends AbstractDateUtilDll
{
  static Logger                _logger       = LoggerFactory.getLogger (UnitTestDateUtilDll.class);
  // TODO Elena/Shiva address the issue of time zome here.
  // TODO Elena/shiva check if we can reuse AIR.Common.Utilities.Dates

  @Autowired
  @Qualifier ("appSettings")
  private ConfigurationSection appSettings   = null;
  private Calendar             _baseLineDate = Calendar.getInstance ();

  public UnitTestDateUtilDll () {
    String baseLineDateTime = appSettings.get ("unitTests.baseLineDateTime");
    try {
      _baseLineDate.setTime (new SimpleDateFormat (DB_DATETIME_FORMAT).parse (baseLineDateTime));
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace ();
      _logger.error ("Could not convert date time to a proper time: " + (StringUtils.isEmpty (baseLineDateTime) ? "Value passed is null. Set baseline datetime in configuration." : baseLineDateTime));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see AIR.Common.Sql.IDateUtilDll#getDate(java.sql.Connection)
   * 
   * @param connection is not used internall here.
   */
  public Date getDate (Connection connection) throws SQLException {
    return getDateStatic ();
  }

  public Date getDateWRetStatus (Connection connection) throws ReturnStatusException {
    return getDateStatic ();
  }

  private synchronized Date getDateStatic ()
  {
    Date current = _baseLineDate.getTime ();
    // increment the time by a couple of seconds to simulate subsequent calls
    // with increasing time.
    _baseLineDate.add (Calendar.MILLISECOND, 5);
    return current;
  }

  // public static void main(String argv[])
  // {
  //
  // try{
  //
  // /*
  // * load up configuration. we expect a settings.xml file on the path.
  // */
  // ConfigurationManager config = ConfigurationManager.getInstance ();
  // ClassLoader thisClassLoader = UnitTestDateUtilDll.class.getClassLoader ();
  // config.getAppSettings ().setURL (thisClassLoader.getResource
  // ("settings-unitTests.xml"));
  // config.getDatabaseSettings ().setURL (thisClassLoader.getResource
  // ("database.xml"));
  //
  // String dateTime = "2012-11-10 15:10:10";
  // String format = "yyyy-MM-dd HH:mm:ss";
  // Date parsedDate = new SimpleDateFormat (format).parse( dateTime );
  // /*
  // Calendar cal = Calendar.getInstance ();
  // cal.set (Calendar.YEAR, 2012);
  // cal.set (Calendar.DAY_OF_MONTH, 10);
  // cal.set (Calendar.MONTH, 10);
  // cal.set(Calendar.HOUR_OF_DAY, 15);
  // cal.set (Calendar.MINUTE, 10);
  // cal.set(Calendar.SECOND, 10);
  // */
  // // String regen = new SimpleDateFormat (format).format (parsedDate, new
  // StringBuffer (), new FieldPosition (0)).toString ();
  // //String calTime = DB_DATETIME_FORMAT.format (cal.getTime (), new
  // StringBuffer (), new FieldPosition (0)).toString ();
  // System.err.println("");
  // }
  // catch (Exception exp)
  // {
  // System.err.println("");
  //
  // }
  //
  // }
}
