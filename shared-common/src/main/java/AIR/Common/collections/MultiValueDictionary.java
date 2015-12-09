/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author temp_rreddy
 * 
 */
public class MultiValueDictionary<K, V> extends HashMap<K, HashSet<V>>
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public MultiValueDictionary () {
    super ();
  }

  // / <summary>
  // / Adds the specified value under the specified key
  // / </summary>
  // / <param name="key">The key.</param>
  // / <param name="value">The value.</param>
  public void add (K key, V value) {
    // ArgumentVerifier.CantBeNull(key, "key");
    HashSet<V> container = null;
    container = this.get (key);
    if (container == null) {
      container = new HashSet<V> ();
      put (key, container);
    }
    container.add (value);
  }

  // / <summary>
  // / Merges the specified multivaluedictionary into this instance.
  // / </summary>
  // / <param name="toMergeWith">To merge with.</param>
  @SuppressWarnings ("unchecked")
  public void merge (MultiValueDictionary<K, V> toMergeWith) {
    if (toMergeWith == null) {
      return;
    }

    for (Map.Entry<K, HashSet<V>> pair : toMergeWith.entrySet ()) {
      for (V value : (V[]) (pair.getValue ().toArray ())) {
        this.add (pair.getKey (), value);
      }
    }
  }

  public HashSet<V> getValues (K key, boolean returnEmptySet) {
    // TODO
    HashSet<V> toReturn = null;
    toReturn = this.get (key);
    if (toReturn != null && returnEmptySet) {
      toReturn = new HashSet<V> ();
    }
    return toReturn;
  }

}
