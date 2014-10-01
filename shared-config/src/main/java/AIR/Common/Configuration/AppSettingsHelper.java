/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Configuration;

import org.apache.commons.lang3.StringUtils;

import AIR.Common.Utilities.SpringApplicationContext;

public class AppSettingsHelper
{
  
  private static ConfigurationSection _appSettings;
  
  static {
    _appSettings =  SpringApplicationContext.getBean ("configurationManager", ConfigurationManager.class).getAppSettings ();
  }
  
  // / <summary>
  // / Check if app setting exists.
  // / </summary>
  public static boolean exists (String key) {
    // return (ConfigurationManager.getInstance ().getAppSettings ().get (key)
    // != null);
    return false;
  }

  public static String get (String key) {
    return get (key, null);
  }

  public static String get (String key, String defaultValue) {
     String value = _appSettings.get(key);
     if (value == null && defaultValue != null) {
       return defaultValue;
     }
    return value;
  }

  public static int getInt32 (String key) {
    return getInt32 (key, null);
  }

  public static int getInt32 (String key, int defaultValue) {
    return getInt32 (key, (Integer) defaultValue);
  }

  private static int getInt32 (String key, Integer defaultValue) {
    String rawValue = get (key);

    if (!StringUtils.isEmpty (rawValue)) {
      int value = Integer.parseInt (rawValue);
      return value;
    }

    return defaultValue != null ? defaultValue.intValue () : 0;
  }

  public static long getInt64 (String key) {
    return getInt64 (key, null);
  }

  public static long getInt64 (String key, long defaultValue) {
    return getInt64 (key, (int) defaultValue);
  }

  private static long getInt64 (String key, Long defaultValue) {
    String rawValue = get (key);

    if (!StringUtils.isEmpty (rawValue)) {
      long value = Long.parseLong (rawValue);
      return value;

    }

    return defaultValue != null ? defaultValue.longValue () : 0;
  }

  public static boolean getBoolean (String key) {
    return getBoolean (key, null);
  }

  public static boolean getBoolean (String key, boolean defaultValue) {
    return getBoolean (key, (Boolean) defaultValue);
  }

  private static boolean getBoolean (String key, Boolean defaultValue) {
    String rawValue = get (key);
    if (!StringUtils.isEmpty (rawValue))
      ;
    {
      boolean value = Boolean.parseBoolean (rawValue);
      return value;
    }
    // TODO Check this again.
    /*
     * return defaultValue != null ? defaultValue.booleanValue() : false;
     */
    // hack! it was giving compilation issues above. need to fix that instead.
  }

  public static double getDouble (String key) {
    return getDouble (key, null);
  }

  public static double getDouble (String key, double defaultValue) {
    return getDouble (key, (double) defaultValue);
  }

  private static double getDouble (String key, Double defaultValue) {
    String rawValue = get (key);

    if (!StringUtils.isEmpty (rawValue)) {
      double value = Double.parseDouble (rawValue);
      return value;

    }

    return defaultValue != null ? defaultValue.doubleValue () : 0;
  }

}
