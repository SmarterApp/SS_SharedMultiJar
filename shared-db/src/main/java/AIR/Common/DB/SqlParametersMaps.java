/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.DB;

import java.util.Map;
import java.util.Set;

import AIR.Common.Helpers.CaseInsensitiveMap;

public class SqlParametersMaps
{
  private CaseInsensitiveMap<Object> _parametersMap = new CaseInsensitiveMap<Object> ();

  public SqlParametersMaps ()
  {
  }

  public SqlParametersMaps put (String key, Object value)
  {
    _parametersMap.put (key, value);
    return this;
  }

  public Object get (String key)
  {
    return _parametersMap.get (key);
  }

  public boolean containsKey (String key)
  {
    return _parametersMap.containsKey (key);
  }

  public int size () {
    return _parametersMap.size ();
  }

  public boolean isEmpty () {
    return _parametersMap.isEmpty ();
  }

  public Object remove (String key) {
    return _parametersMap.remove (key);
  }

  public Set<String> keySet () {
    return _parametersMap.keySet ();
  }

  public Set<java.util.Map.Entry<String, Object>> entrySet () {
    return _parametersMap.entrySet ();
  }

  protected Map<String, Object> getMap ()
  {
    return _parametersMap;
  }

}
