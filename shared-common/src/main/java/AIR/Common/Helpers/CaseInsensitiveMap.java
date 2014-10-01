/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Helpers;

/**
 * @author efurman
 * 
 */
// TODO Elena/Shiva look into this.
/*
 * Apache Commons Collections has a CaseInsensitiveMap
 * (http://commons.apache.org
 * /proper/commons-collections/apidocs/org/apache/commons
 * /collections/map/CaseInsensitiveMap.html) but for some reason we could not
 * find the generic version of that in our commons-collection jar.
 */
import java.util.HashMap;

public class CaseInsensitiveMap<T> extends HashMap<String, T>
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public T put (String key, T value) {
    return super.put (key.toLowerCase (), value);
  }

  @Override
  public T get (Object key) {
    return super.get (key.toString ().toLowerCase ());
  }

  @Override
  public boolean containsKey (Object key) {
    return super.containsKey (key.toString ().toLowerCase ());
  }

  public boolean tryGetValue (String key, _Ref<T> ref) 
  {
    key = key.toLowerCase ();
    if (this.containsKey (key)) {
      ref.set (get (key));
      return true;
    }
    return false;
  }
}
