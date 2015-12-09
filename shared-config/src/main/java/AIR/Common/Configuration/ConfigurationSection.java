/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Configuration;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

public class ConfigurationSection extends HashMap<String, String>
{
  private static final long serialVersionUID = 1L;

  
  
  /**
   * Append properties from a map to this collection
   * @param updates
   */
  public ConfigurationSection updateProperties (Map<String, String> updates) {
    if (updates != null)
      super.putAll (updates);
    return this;
  }
  
  public ConfigurationSection updateProperties( URL url ) throws ConfigurationException {
    XMLPropertiesConfiguration config = new XMLPropertiesConfiguration (url);
    Iterator<String> key_iterator = config.getKeys ();
    while ( key_iterator.hasNext () ) {
      String key = key_iterator.next ();
      this.put ( key, config.getString (key) );
    }
    return this;
  }
  
  @Override
  public String get (Object key) {
    return get (key, null);
  }

  public String get (Object key, String defaultValue) {
    key = (key == null) ? "" : key;
    String rawValue = super.get (key);
    return StringUtils.isEmpty (rawValue) ? defaultValue : rawValue;
  }

  public int getInt32 (String key) {
    return getInt32 (key, null);
  }

  public int getInt32 (String key, Integer defaultValue) {
    String rawValue = get (key);
    defaultValue = (defaultValue == null) ? 0 : defaultValue;
    return StringUtils.isEmpty (rawValue) ? defaultValue : Integer.valueOf (rawValue);
  }

  public long getInt64 (String key) {
    return getInt64 (key, null);
  }

  public long getInt64 (String key, Long defaultValue) {
    String rawValue = get (key);
    defaultValue = (defaultValue == null) ? 0 : defaultValue;
    return StringUtils.isEmpty (rawValue) ? defaultValue : Long.valueOf (rawValue);
  }

  public boolean getBoolean (String key) {
    return getBoolean (key, null);
  }

  public boolean getBoolean (String key, Boolean defaultValue) {
    String rawValue = get (key);
    defaultValue = (defaultValue != null) && defaultValue;
    return StringUtils.isEmpty (rawValue) ? defaultValue : Boolean.valueOf (rawValue);
  }

  public double getDouble (String key) {
    return getDouble (key, null);
  }

  public double getDouble (String key, Double defaultValue) {
    String rawValue = get (key);
    defaultValue = (defaultValue == null) ? 0 : defaultValue;
    return StringUtils.isEmpty (rawValue) ? defaultValue : Double.valueOf (rawValue);
  }

}
