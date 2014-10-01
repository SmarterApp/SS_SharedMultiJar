/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Configuration;


public class ConfigurationManager
{

  private final ConfigurationSection _appSettings      = new ConfigurationSection ();
  private final ConfigurationSection _rendererSettings = new ConfigurationSection ();

  public ConfigurationSection getRendererSettings () {
    return _rendererSettings;
  }

  public ConfigurationSection getAppSettings () {
    return _appSettings;
  }

  //TODO mpatel - implement to get ConfigurationManager.ConnectionStrings["SESSION_DB"] or 
  public static String getConnectionStrings(String id) {
    return "";
  }
}
