package com.technion.studybuddy.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.util.SparseArray;

public class SparseArrayMap<E> implements Map<Integer, E> {

	private final SparseArray<E> inner;

	public SparseArrayMap() {
		inner = new SparseArray<E>();
	}

	public SparseArrayMap(int initialCapacity) {
		inner = new SparseArray<E>(initialCapacity);
	}

	@Override
	public void clear() {
		inner.clear();

	}

	@Override
	public boolean containsKey(Object key) {
		return (inner.indexOfKey((Integer) key) >= 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsValue(Object value) {
		return (inner.indexOfValue((E) value) >= 0);
	}

	@Override
	public Set<java.util.Map.Entry<Integer, E>> entrySet() {
		Set<java.util.Map.Entry<Integer, E>> entrySet = new HashSet<java.util.Map.Entry<Integer, E>>(
				inner.size());
		for (int i = 0; i < inner.size(); i++) {
			Map.Entry<Integer, E> e = new AbstractMap.SimpleEntry<Integer, E>(
					inner.keyAt(i), inner.valueAt(i));
			entrySet.add(e);
		}

		return entrySet;

	}

	@Override
	public E get(Object key) {
		return inner.get((Integer) key);
	}

	@Override
	public boolean isEmpty() {
		return inner.size() == 0;
	}

	@Override
	public Set<Integer> keySet() {
		Set<Integer> keySet = new HashSet<Integer>();

		for (int i = 0; i < inner.size(); i++) {
			keySet.add(inner.keyAt(i));
		}

		return keySet;
	}

	@Override
	public E put(Integer key, E value) {
		E prev = inner.get(key);
		inner.put(key, value);
		return prev;
	}

	@Override
	public void putAll(Map<? extends Integer, ? extends E> arg0) {
		for (Map.Entry<? extends Integer, ? extends E> e : arg0.entrySet()) {
			inner.put(e.getKey(), e.getValue());
		}
	}

	@Override
	public E remove(Object key) {
		E value = inner.get((Integer) key);
		inner.delete((Integer) key);
		return value;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return inner.size();
	}

	@Override
	public Collection<E> values() {
		Collection<E> values = new ArrayList<E>(inner.size());
		for (int i = 0; i < inner.size(); i++) {
			values.add(inner.valueAt(i));
		}
		return values;

	}

	/***
	 * 
	 * The hash code of a map is defined to be the sum of the hash codes of each
	 * entry in the map's entrySet() view
	 * 
	 * @see <a
	 *      href="http://docs.oracle.com/javase/7/docs/api/java/util/Map.html#hashCode()"
	 *      >http://docs.oracle.com/javase/7/docs/api/java/util/Map.html#hashCode()</a>
	 */
	@Override
	public int hashCode() {
		int hashcode = 0;
		for (Map.Entry<Integer, E> e : entrySet()) {
			hashcode += e.hashCode();
		}
		return hashcode;
	}

	/***
	 * 
	 * Returns true if the given object is also a map and the two maps represent
	 * the same mappings. More formally, two maps m1 and m2 represent the same
	 * mappings if m1.entrySet().equals(m2.entrySet())
	 * 
	 * @see <a
	 *      href="http://docs.oracle.com/javase/7/docs/api/java/util/Map.html#equals(java.lang.Object)"
	 *      >http://docs.oracle.com/javase/7/docs/api/java/util/Map.html#equals(java.lang.Object)</a>
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!Map.class.isInstance(obj))
			return false;
		@SuppressWarnings("unchecked")
		Map<Integer, E> other = (Map<Integer, E>) obj;
		return entrySet().equals(other.entrySet());
	}

}
