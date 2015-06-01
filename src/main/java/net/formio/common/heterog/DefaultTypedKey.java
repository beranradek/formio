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

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Key that also holds type of associated value.
 * <p>
 * Thread-safe: Immutable, if also key class (K) is immutable!
 * 
 * @author Radek Beran
 */
public final class DefaultTypedKey<K, V> extends AbstractTypedKey<K, V> {
	private static final long serialVersionUID = -1112883168944104127L;
	
	private static final Object mutex = new Object();
	
	// WeakHashMap must have TypedKey as type of keys
	private static final Map<TypedKey<?, ?>, TypedKey<?, ?>> cache = new WeakHashMap<TypedKey<?, ?>, TypedKey<?, ?>>();
	
	private DefaultTypedKey(K key, Class<V> cls) {
		super(key, cls);
	}
	
	/**
	 * Returns instance of identifier.
	 * @param key
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked") // we know that inserted element contains Class<T>, so this cast is type-safe.
	public static <K, V> TypedKey<K, V> valueOf(K key, Class<V> cls) {
		if (cls == null) {
			throw new IllegalArgumentException("valueClass cannot be null");
		}
		if (key == null) {
			throw new IllegalArgumentException("key cannot be null");
		}
		// try to get immutable instance from cache
		TypedKey<K, V> referenceInstance = new DefaultTypedKey<K, V>(key, cls);
		// Cache is internal and is modified only in this method.
		TypedKey<K, V> typedId = null;
		synchronized (mutex) {
			typedId = (TypedKey<K, V>) cache.get(referenceInstance);
		}
		if (typedId == null) {
			typedId = referenceInstance;
			synchronized (mutex) {
				cache.put(typedId, typedId);
			}
		}
		return typedId;
	}
	
}
