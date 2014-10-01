/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Utilities;

import java.util.List;
import java.util.Map;

public class MapUtils
{
  @SuppressWarnings ("unchecked")
  public static Object getNested (Map<String, ?> map, final Object... keys) {
    Object o = map;
    for (Object key_i : keys) {
      if (key_i instanceof String) {
        o = ((Map<String, ?>) o).get ((String) key_i);
      }
      else if (key_i instanceof Integer) {
        o = ((List<?>) o).get ((Integer) key_i);
      }
      else {
        throw new IllegalArgumentException (String.format ("Cannot dereference nested object using key of type %s", key_i.getClass ().getName ()));
      }
    }
    return o;
  }
}
