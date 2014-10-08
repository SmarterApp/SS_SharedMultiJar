/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
/**
 * 
 */
package AIR.Common.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Transformer;

/**
 * @author Shiva BEHERA [sbehera@air.org]
 * 
 */
public class IGrouping<K, V> extends ArrayList<V>
{
  private static final long serialVersionUID = 1L;
  private K                 _key             = null;

  public IGrouping (K key) {
    setKey (key);
  }

  public K getKey () {
    return _key;
  }

  public void setKey (K value) {
    _key = value;
  }

  public static <K, V> List<IGrouping<K, V>> createGroups (Collection<V> list,
      Transformer transformer) {
    Map<K, IGrouping<K, V>> map = new HashMap<K, IGrouping<K, V>> ();
    for (V element : list) {
      @SuppressWarnings ("unchecked")
      K groupValue = (K) transformer.transform (element);
      IGrouping<K, V> group = null;
      if (map.containsKey (groupValue)) {
        group = map.get (groupValue);
      } else {
        group = new IGrouping<K, V> (groupValue);
        map.put (groupValue, group);
      }
      group.add (element);
    }
    return new ArrayList<IGrouping<K, V>> (map.values ());
  }
}
