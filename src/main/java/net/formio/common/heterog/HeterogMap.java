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

import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * Type-safe container which can contain values of different
 * types. Values are accessed via keys that hold String name of value
 * and information about value's class.
 * @author Radek Beran
 */
public interface HeterogMap<K> {

	/**
	 * Puts new key-value to the container.
	 * @param key key for the value
	 * @param value
	 * @return the previous value associated with <tt>typedId</tt>, or
     * <tt>null</tt> if there was no mapping for <tt>typedId</tt>.
     * (A <tt>null</tt> return can also indicate that the container
     * previously associated <tt>null</tt> with <tt>typedId</tt>,
     * if the implementation supports <tt>null</tt> values.)
	 */
	<T> T putTyped(TypedKey<K, T> key, T value);
	
	/**
	 * Copies all of the keys and values from the specified container to this container.
	 * @param source
	 */
	void putAllFromSource(HeterogMap<K> source);

	/**
	 * Returns value associated with given key, {@code null} if no such value is present.
	 * @param key
	 * @return
	 */
	<T> T getTyped(TypedKey<K, T> key);
	
	/**
	 * Removes value associated with given key.
	 * Returns the value to which this container previously associated the key,
     * or <tt>null</tt> if the container contained no mapping for the key.
	 * @param key
	 * @return
	 */
	<T> T removeTyped(TypedKey<K, T> key);
	
	/**
     * Returns a {@link Set} view of the keys contained in this container.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa. If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation), the results of
     * the iteration are undefined. The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     *
     * @return a set view of the keys contained in this container
     */
	// TypedKey is in "consumer super" position, but ? inside TypedKey in "producer extends" position,
	// so ? that is equivalent to "? extends Object" is used
	Set<TypedKey<K, ?>> keySet();
	
	/**
     * Returns a {@link Collection} view of the values contained in this container.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa. If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined. The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations. It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a collection view of the values contained in this container
     */
	// Object is in "consumer super" position and ? is equivalent to "? extends Object" ("producer extends" position),
	// so Object is used instead of ?
	Collection<Object> values();
    
    /**
     * Returns a {@link Set} view of the mappings contained in this container.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the
     * iterator) the results of the iteration are undefined. The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations. It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this container
     */
	// TypedKey is in "consumer super" position, but ? inside TypedKey in "producer extends" position,
	// so ? that is equivalent to "? extends Object" is used inside TypedKey.
	// Object is in "consumer super" position.
    Set<Map.Entry<TypedKey<K, ?>, Object>> entrySet();
	
	int size();

	boolean isEmpty();
	
	void clear();
	
	<T> boolean containsKey(TypedKey<K, T> key);
	
	/**
     * Compares the specified object with this map for equality.  Returns
     * <tt>true</tt> if the given object is also a map and the two maps
     * represent the same mappings.  More formally, two maps <tt>m1</tt> and
     * <tt>m2</tt> represent the same mappings if
     * <tt>m1.entrySet().equals(m2.entrySet())</tt>.  This ensures that the
     * <tt>equals</tt> method works properly across different implementations
     * of the <tt>Map</tt> interface.
     *
     * @param o object to be compared for equality with this map
     * @return <tt>true</tt> if the specified object is equal to this map
     */
    @Override
	boolean equals(Object o);

    /**
     * Returns the hash code value for this map.  The hash code of a map is
     * defined to be the sum of the hash codes of each entry in the map's
     * <tt>entrySet()</tt> view.  This ensures that <tt>m1.equals(m2)</tt>
     * implies that <tt>m1.hashCode()==m2.hashCode()</tt> for any two maps
     * <tt>m1</tt> and <tt>m2</tt>, as required by the general contract of
     * {@link Object#hashCode}.
     *
     * @return the hash code value for this map
     * @see Map.Entry#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    @Override
	int hashCode();
    
    /**
     * Returns the heterogeneous map in a form of common {@link Map}.
     * @return
     */
    Map<K, Object> asMap();
}
