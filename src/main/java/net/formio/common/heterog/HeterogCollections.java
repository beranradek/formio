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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Operations with heterogeneous collections.
 * @author Radek Beran
 */
public final class HeterogCollections {

	public static <K> HeterogMap<K> unmodifiableMap(HeterogMap<K> c) {
		return new UnmodifiableHeterogeneousMap<K>(c);
	}
	
	public static <K> HeterogMap<K> synchronizedMap(HeterogMap<K> c) {
		return new SynchronizedHeterogeneousMap<K>(c);
	}
	
	public static <K> HeterogMap<K> newMap() {
		return new DefaultHeterogMap<K>(new HashMap<TypedKey<K, ?>, Object>());
	}
	
	public static <K> HeterogMap<K> newConcurrentMap() {
		return new DefaultHeterogMap<K>(new ConcurrentHashMap<TypedKey<K, ?>, Object>());
	}
	
	public static <K> HeterogMap<K> newLinkedMap() {
		return new DefaultHeterogMap<K>(new LinkedHashMap<TypedKey<K, ?>, Object>());
	}
	
	public static <K> HeterogMap<K> newUnmodifiableMap() {
		return unmodifiableMap(new DefaultHeterogMap<K>(new HashMap<TypedKey<K, ?>, Object>()));
	}
	
	public static <K> HeterogMap<K> newSynchronizedMap() {
		return synchronizedMap(new DefaultHeterogMap<K>(new HashMap<TypedKey<K, ?>, Object>()));
	}
	
	/**
     * @serial include
     */
    static class UnmodifiableHeterogeneousMap<K> implements HeterogMap<K>, Serializable {
        private static final long serialVersionUID = 1820017752578914078L;

        final HeterogMap<K> c;

        UnmodifiableHeterogeneousMap(HeterogMap<K> c) {
        	if (c == null) throw new NullPointerException("source container cannot be null");
        	this.c = c;
        }
        @Override
        public boolean equals(Object o) {return o == this || c.equals(o);}
        @Override
        public int hashCode() {return c.hashCode();}
        @Override
        public String toString() {return c.toString();}
		@Override
		public <T> T putTyped(TypedKey<K, T> key, T value) {
			throw new UnsupportedOperationException();
		}
		@Override
		public void putAllFromSource(HeterogMap<K> source) {
			throw new UnsupportedOperationException();
		}
		@Override
		public <T> T getTyped(TypedKey<K, T> key) {
			return c.getTyped(key);
		}
		@Override
		public <T> T removeTyped(TypedKey<K, T> key) {
			throw new UnsupportedOperationException();
		}
		@Override
		public Set<TypedKey<K, ?>> keySet() {
			return c.keySet();
		}
		@Override
		public int size() {
			return c.size();
		}
		@Override
		public boolean isEmpty() {
			return c.isEmpty();
		}
		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}
		@Override
		public <T> boolean containsKey(TypedKey<K, T> key) {
			return c.containsKey(key);
		}
		@Override
		public Collection<Object> values() {
			return c.values();
		}
		@Override
		public Set<Entry<TypedKey<K, ?>, Object>> entrySet() {
			return c.entrySet();
		}
		
		@Override
		public Map<K, Object> asMap() {
			return c.asMap();
		}
    }
    
    /**
     * @serial include
     */
    static class SynchronizedHeterogeneousMap<K> implements HeterogMap<K>, Serializable {
        private static final long serialVersionUID = 3053995032091335093L;

        private final HeterogMap<K> c;	// backing Collection
        private final Object mutex;     		// object on which to synchronize

        SynchronizedHeterogeneousMap(HeterogMap<K> c) {
            if (c == null) throw new NullPointerException("source container cannot be null");
            this.c = c;
            mutex = this;
        }
        SynchronizedHeterogeneousMap(HeterogMap<K> c, Object mutex) {
            this.c = c;
            this.mutex = mutex;
        }
        @Override
		public boolean equals(Object o) {
            if (this == o)
            	return true;
            synchronized (mutex) { return c.equals(o); }
        }
        @Override
		public int hashCode() {
            synchronized (mutex) { return c.hashCode(); }
        }
        @Override
		public String toString() {
            synchronized (mutex) { return c.toString(); }
        }
		@Override
		public <T> T putTyped(TypedKey<K, T> key, T value) {
			synchronized (mutex) { return c.putTyped(key, value); }
		}
		@Override
		public void putAllFromSource(HeterogMap<K> source) {
			synchronized (mutex) { c.putAllFromSource(source); }
		}
		@Override
		public <T> T getTyped(TypedKey<K, T> key) {
			synchronized (mutex) { return c.getTyped(key); }
		}
		@Override
		public <T> T removeTyped(TypedKey<K, T> key) {
			synchronized (mutex) { return c.removeTyped(key); }
		}
		@Override
		public Set<TypedKey<K, ?>> keySet() {
			synchronized (mutex) { return c.keySet(); }
		}
		@Override
		public int size() {
			synchronized (mutex) { return c.size(); }
		}
		@Override
		public boolean isEmpty() {
			synchronized (mutex) { return c.isEmpty(); }
		}
		@Override
		public void clear() {
			synchronized (mutex) { c.clear(); }
		}
		@Override
		public <T> boolean containsKey(TypedKey<K, T> key) {
			synchronized (mutex) { return c.containsKey(key); }
		}
		@Override
		public Collection<Object> values() {
			synchronized (mutex) { return c.values(); }
		}
		@Override
		public Set<Entry<TypedKey<K, ?>, Object>> entrySet() {
			synchronized (mutex) { return c.entrySet(); }
		}
		
		@Override
		public Map<K, Object> asMap() {
			synchronized (mutex) { return c.asMap(); }
		}
    }
	
	private HeterogCollections() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
