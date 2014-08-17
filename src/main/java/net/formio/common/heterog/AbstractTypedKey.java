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

/**
 * Convenience basic implementation of {@link TypedKey}.
 * @author Radek Beran
 *
 * @param <K>
 * @param <V>
 */
public abstract class AbstractTypedKey<K, V> implements TypedKey<K, V> {

	private final K key;
	private final Class<V> valueClass;
	
	public AbstractTypedKey(K key, Class<V> valueClass) {
		if (key == null) throw new IllegalArgumentException("key cannot be null");
		if (valueClass == null) throw new IllegalArgumentException("valueClass cannot be null");
		this.key = key;
		this.valueClass = valueClass;
	}
	
	@Override
	public K getKey() {
		return key;
	}

	@Override
	public Class<V> getValueClass() {
		return valueClass;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getKey() == null) ? 0 : (getKey().hashCode()));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TypedKey<?, ?>))
			return false;
		TypedKey<?, ?> other = (TypedKey<?, ?>)obj;
		if (getKey() == null) {
			if (other.getKey() != null)
				return false;
		} else if (!getKey().equals(other.getKey()))
			return false;
		return true;
	}
	
	/**
	 * Has format of: &lt;key&gt;&lt;space&gt;(&lt;full class name&gt;)
	 * <p>
	 * Note that there is on space between key and class full name which is in parenthesis.
	 */
	@Override
	public String toString() {
		return getKey().toString() + " (" + getValueClass().getName() + ")";
	}
}
