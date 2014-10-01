/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Helpers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//TODO Shiva: now that we have switched to a version of commons-collection that supports generics
//we should try to use that instead.
public class CaseInsensitiveBiMap<V> implements Map<String, V> {

	private Map<String, V> _forwardMap = new CaseInsensitiveMap<V>();
	private Map<V, String> _reverseMap = new HashMap<V, String>();

	@Override
	public int size() {
		return _forwardMap.size();
	}

	@Override
	public boolean isEmpty() {
		return _forwardMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return _forwardMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return _forwardMap.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return _forwardMap.get(key);
	}

	@Override
	public V remove(Object key) {
		V value = _forwardMap.remove(key);
		inverse().remove(value);
		return value;
	}

	@Override
	public void clear() {
		_forwardMap.clear();
		inverse().clear();
	}

	@Override
	public Set<String> keySet() {
		return _forwardMap.keySet();
	}

	@Override
	public Set<java.util.Map.Entry<String, V>> entrySet() {
		return _forwardMap.entrySet();
	}

	public String inverseGet(V key) {
		return inverse().get(key);
	}

	@Override
	public V put(String key, V value) {
		if (containsKey(key))
			throw new KeyAlreadyExistsException(key);
		inverse().put(value, key);
		return _forwardMap.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends V> map) {
		_forwardMap.putAll(map);
		for (Map.Entry<? extends String, ? extends V> entry : map.entrySet()) {
			inverse().put(entry.getValue(), entry.getKey());
		}
	}

	@Override
	public Collection<V> values() {
		return _forwardMap.values();
	}

	private Map<V, String> inverse() {
		return _reverseMap;
	}
}
