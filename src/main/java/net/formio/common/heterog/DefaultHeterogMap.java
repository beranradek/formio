/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.common.heterog;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of typesafe heterogeneous container (heterogeneous mapping).
 * <p>
 * Immutable: No, items can be added/removed to/from internal state.
 * 
 * @author Radek Beran
 */
class DefaultHeterogMap<K> implements Serializable, HeterogMap<K>, Map<TypedKey<K, ?>, Object> {
	private static final long serialVersionUID = 1584641968968664353L;
	
	// Unbounded wildcard "?" is related to key of map, not a map itself
	// Java's type system is NOT enough powerful to express relationship
	// between type in TypedKey and type of value, but that relationship
	// is properly handled in putTyped and getTyped methods.
	private final Map<TypedKey<K, ?>, Object> container;

	DefaultHeterogMap() {
		this(new HashMap<TypedKey<K, ?>, Object>());
	}
	
	DefaultHeterogMap(int initialCapacity) {
		this(new HashMap<TypedKey<K, ?>, Object>(initialCapacity));
	}
	
	DefaultHeterogMap(int initialCapacity, float loadFactor) {
		this(new HashMap<TypedKey<K, ?>, Object>(initialCapacity, loadFactor));
	}
	
	/** 
	 * Constructs new heterogeneous container using given underlying map. If given map
	 * is another heterogeneous container, it constructs shallow copy of given container.
	 * @param bakingMap
	 */
	DefaultHeterogMap(Map<TypedKey<K, ?>, Object> bakingMap) {
		Map<TypedKey<K, ?>, Object> bakingMapping = bakingMap;
		if (bakingMapping == null) throw new IllegalArgumentException("bakingMap cannot be null");
		if (bakingMapping instanceof DefaultHeterogMap) {
			DefaultHeterogMap<K> srcContainer = (DefaultHeterogMap<K>)bakingMapping;
			bakingMapping = copyUnderlyingMap(srcContainer.container);
		}
		this.container = bakingMapping;
	}
	
	// ---- HeterogMap:

	@Override
	public <T> T putTyped(TypedKey<K, T> key, T value) {
		if (key == null)
			throw new NullPointerException("key cannot be null");
		T prev = getTyped(key);
		putTypedInternal(key, value);
		return prev;
	}
	
	@SuppressWarnings("unchecked") // we know heterogeneous container can contain only correctly paired keys and values
	@Override
	public void putAllFromSource(HeterogMap<K> c) {
		if (c != null) {
			for (TypedKey<K, ?> id : c.keySet()) {
				putTyped((TypedKey<K, Object>)id, c.getTyped(id));
			}
		}
	}
	
	@Override
	public <T> T getTyped(TypedKey<K, T> key) {
		if (key == null)
			throw new NullPointerException("key cannot be null");
		// Dynamic check that value is of type represented by valueClass in key
		// It returns correct type. If type is incorrect, ClassCastException is thrown.
		return key.getValueClass().cast(container.get(key));
	}
	
	@Override
	public <T> T removeTyped(TypedKey<K, T> key) {
		T value = getTyped(key);
		// value can be null for the key, but still present
		this.container.remove(key);
		return value;
	}
	
	@Override
	public <T> boolean containsKey(TypedKey<K, T> key) {
		return this.container.containsKey(key);
	}
	// ----
	
	@Override
	public int size() {
		return container.size();
	}

	@Override
	public boolean isEmpty() {
		return this.container.isEmpty();
	}
	
    @SuppressWarnings("unchecked") // we know only TypedKey key can be put into the map
	@Override
	public Object remove(Object key) {
    	if (!(key instanceof TypedKey))
    		throw new IllegalArgumentException("key must be of type TypedKey");
    	return removeTyped((TypedKey<K, Object>)key);
    }

	@Override
	public void clear() {
		this.container.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.container.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.container.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return this.container.get(key);
	}

	@SuppressWarnings("unchecked") // type of value is checked inside the internally used putTyped method
	@Override
	public Object put(TypedKey<K, ?> key, Object value) {
		return putTyped((TypedKey<K, Object>)key, value);
	}

	@Override
	public void putAll(Map<? extends TypedKey<K, ?>, ? extends Object> m) {
		for (Map.Entry<? extends TypedKey<K, ?>, Object> e : this.container.entrySet())
            put(e.getKey(), e.getValue());
	}

	@Override
	public Set<TypedKey<K, ?>> keySet() {
		return this.container.keySet();
	}

	@Override
	public Collection<Object> values() {
		return this.container.values();
	}

	@Override
	public Set<Map.Entry<TypedKey<K, ?>, Object>> entrySet() {
		return this.container.entrySet();
	}
	
	/**
     * Compares the specified object with this map for equality.  Returns
     * <tt>true</tt> if the given object is also a map and the two maps
     * represent the same mappings.  More formally, two maps <tt>m1</tt> and
     * <tt>m2</tt> represent the same mappings if
     * <tt>m1.entrySet().equals(m2.entrySet())</tt>.  This ensures that the
     * <tt>equals</tt> method works properly across different implementations
     * of the <tt>Map</tt> interface.
     *
     * <p>This implementation first checks if the specified object is this map;
     * if so it returns <tt>true</tt>.  Then, it checks if the specified
     * object is a map whose size is identical to the size of this map; if
     * not, it returns <tt>false</tt>.  If so, it iterates over this map's
     * <tt>entrySet</tt> collection, and checks that the specified map
     * contains each mapping that this map contains.  If the specified map
     * fails to contain such a mapping, <tt>false</tt> is returned.  If the
     * iteration completes, <tt>true</tt> is returned.
     *
     * @param o object to be compared for equality with this map
     * @return <tt>true</tt> if the specified object is equal to this map
     */
    @Override
	public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof HeterogMap))
            return false;
        HeterogMap<K> m = (HeterogMap<K>)o;
        if (m.size() != size())
            return false;
        try {
            Iterator<Entry<TypedKey<K, ?>, Object>> i = entrySet().iterator();
            while (i.hasNext()) {
                Entry<TypedKey<K, ?>, Object> e = i.next();
                TypedKey<K, ?> key = e.getKey();
                Object value = e.getValue();
                if (value == null) {
                    if (m.getTyped(key) != null || !m.containsKey(key))
                        return false;
                } else {
                    if (!value.equals(m.getTyped(key)))
                        return false;
                }
            }
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }

        return true;
    }

    /**
     * Returns the hash code value for this map.  The hash code of a map is
     * defined to be the sum of the hash codes of each entry in the map's
     * <tt>entrySet()</tt> view.  This ensures that <tt>m1.equals(m2)</tt>
     * implies that <tt>m1.hashCode()==m2.hashCode()</tt> for any two maps
     * <tt>m1</tt> and <tt>m2</tt>, as required by the general contract of
     * {@link Object#hashCode}.
     *
     * <p>This implementation iterates over <tt>entrySet()</tt>, calling
     * {@link Map.Entry#hashCode hashCode()} on each element (entry) in the
     * set, and adding up the results.
     *
     * @return the hash code value for this map
     * @see Map.Entry#hashCode()
     * @see Object#equals(Object)
     * @see Set#equals(Object)
     */
    @Override
    public int hashCode() {
        int h = 0;
        Iterator<Entry<TypedKey<K, ?>, Object>> i = entrySet().iterator();
        while (i.hasNext())
            h += i.next().hashCode();
        return h;
    }
    
    @Override
    public Map<K, Object> asMap() {
    	Map<K, Object> map = new HashMap<K, Object>();
    	for (Map.Entry<TypedKey<K, ?>, Object> e : entrySet()) {
    		map.put(e.getKey().getKey(), e.getValue());
    	}
    	return map;
    }
    
    /**
     * Returns a string representation of this map.  The string representation
     * consists of a list of key-value mappings in the order returned by the
     * map's <tt>entrySet</tt> view's iterator, enclosed in braces
     * (<tt>"{}"</tt>).  Adjacent mappings are separated by the characters
     * <tt>", "</tt> (comma and space).  Each key-value mapping is rendered as
     * the key followed by an equals sign (<tt>"="</tt>) followed by the
     * associated value.  Keys and values are converted to strings as by
     * {@link String#valueOf(Object)}.
     *
     * @return a string representation of this map
     */
    @Override
    public String toString() {
        Iterator<Entry<TypedKey<K, ?>, Object>> i = entrySet().iterator();
        if (! i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<TypedKey<K, ?>, Object> e = i.next();
            TypedKey<K, ?> key = e.getKey();
            Object value = e.getValue();
            sb.append(key.toString());
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (! i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }
	
	private <T> void putTypedInternal(TypedKey<K, T> key, T value) {
		// Cast prevents from invalid type when raw method without generics is used
		this.container.put(key, key.getValueClass().cast(value));
	}
	
	private static <K> Map<TypedKey<K, ?>, Object> copyUnderlyingMap(Map<TypedKey<K, ?>, Object> source) {
		if (source == null) throw new IllegalArgumentException("source cannot be null");
		if (source instanceof LinkedHashMap) {
			return new LinkedHashMap<TypedKey<K, ?>, Object>(source);
		}
		return new HashMap<TypedKey<K, ?>, Object>(source);
	}
}
