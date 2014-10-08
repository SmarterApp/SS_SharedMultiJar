/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Configuration;

import org.apache.commons.lang.StringUtils;

import AIR.Common.Helpers.InvalidCastException;
import AIR.Common.Helpers._Ref;

// / <summary>
// / This class is used to get app settings out of web.config or a database.
// / </summary>
public class AppSettings
{
  private static IAppSettingsHandler _handler;

  // / <summary>
  // / Set the handler used to lookup settings. In this handler you
  // / should figure out what the current client is and other context
  // / related issues.
  // / </summary>
  public static void setHandler (IAppSettingsHandler handler) {
    _handler = handler;
  }

  // / <summary>
  // / Lookup an app settings.
  // / </summary>
  // / <typeparam name="T">The data type of the setting. Right now there is only
  // string, bool and int.</typeparam>
  // / <param name="name">The name of the app setting key.</param>
  // / <param name="defaultValue">A default value in case the app setting is not
  // found.</param>
  // / <returns></returns>
  public static <T> AppSetting<T> get (String name, T defaultValue, AppSettingsHelperMethodWrapper<T> appSettingsHelper) {
    // first check web.config
    if (AppSettingsHelper.exists (name)) {
      T value = appSettingsHelper.getValue (name, defaultValue);
      return new AppSetting<T> (name, value);
    }
    // TODO Shiva. I need to revising the following if loop.
    // if a getter function was assigned then load from that
    if (_handler != null) {
      _Ref<T> value = new _Ref<T> (defaultValue);
      if (_handler.get (name, value)) {
        try {
          return new AppSetting<T> (name, (T) (value.get ()));
        } catch (Exception ice) {
          throw new InvalidCastException (String.format ("%1$s (%2$s)", ice.getMessage (), name), ice);
        }
      }
    }

    // return default
    return new AppSetting<T> (name, defaultValue);
  }

  public static AppSetting<Double> getDouble (String name) {
    return AppSettings.<Double> get (name, new Double (0), new AppSettingsHelperMethodWrapper<Double> ()
    {
      @Override
      public Double getValue (String key, Double defaultValue) {
        return AppSettingsHelper.getDouble (key, defaultValue);
      }
    });
  }

  public static AppSetting<Integer> getInteger (String name) {
    return getInteger (name, 0);
  }

  public static AppSetting<Integer> getInteger (String name, final int defaultValue) {
    return AppSettings.<Integer> get (name, defaultValue, new AppSettingsHelperMethodWrapper<Integer> ()
    {
      @Override
      public Integer getValue (String key, Integer defaultValue) {
        return AppSettingsHelper.getInt32 (key, defaultValue);
      }
    });
  }

  public static AppSetting<Long> getLong (String name) {
    return AppSettings.<Long> get (name, new Long (0), new AppSettingsHelperMethodWrapper<Long> ()
    {
      @Override
      public Long getValue (String key, Long defaultValue) {
        return AppSettingsHelper.getInt64 (key, defaultValue);
      }
    });
  }

  public static AppSetting<Boolean> getBoolean (String name) {
    return AppSettings.<Boolean> get (name, false, new AppSettingsHelperMethodWrapper<Boolean> ()
    {
      @Override
      public Boolean getValue (String key, Boolean defaultValue) {
        return AppSettingsHelper.getBoolean (key, defaultValue);
      }
    });
  }

  public static AppSetting<String> getString (String name) {
    return AppSettings.<String> get (name, null, new AppSettingsHelperMethodWrapper<String> ()
    {
      @Override
      public String getValue (String key, String defaultValue) {
        return AppSettingsHelper.get (key, defaultValue);
      }
    });
  }

  public static Object parseString (String type, String value) {
    switch (type.toLowerCase ()) {
    case "boolean":
      return new Boolean (value);
    case "integer":
      return new Integer (value);
    default:
      return value; // default also covers "string"
    }
  }

  /*
   * function to check if a AppSetting is empty or null. It not only checks the
   * AppSetting object but also check the internal value to be null.
   * 
   * TODO Sajib/Shiva : See if there is anyway to use TypeReference to change
   * the arguement list to AppSetting<T> instead.
   */
  public static boolean isNullOrEmptyString (AppSetting<String> setting)
  {
    if (setting == null)
      return true;

    return StringUtils.isEmpty (setting.getValue ());
  }

  private interface AppSettingsHelperMethodWrapper<T>
  {
    T getValue (String key, T defaultValue);
  }

}
