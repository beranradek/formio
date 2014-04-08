/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.binding.collection;

/**
 * Collection type specification.
 * 
 * @param <C> type of collection
 * @author Radek Beran
 */
public final class CollectionSpec<C> {
	private final Class<C> collClass;
	private final ItemsOrder preferedItemsOrder;

	public static <C> CollectionSpec<C> getInstance(Class<C> collClass,
			ItemsOrder preferedItemsOrder) {
		return new CollectionSpec<C>(collClass, preferedItemsOrder);
	}

	private CollectionSpec(Class<C> collClass, ItemsOrder preferedItemsOrder) {
		if (collClass == null) throw new IllegalArgumentException("collClass cannot be null");
		if (preferedItemsOrder == null) throw new IllegalArgumentException("preferedItemsOrder cannot be null");
		this.collClass = collClass;
		this.preferedItemsOrder = preferedItemsOrder;
	}

	public Class<C> getCollClass() {
		return collClass;
	}

	public ItemsOrder getPreferedItemsOrder() {
		return preferedItemsOrder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((collClass == null) ? 0 : collClass.hashCode());
		result = prime
				* result
				+ ((preferedItemsOrder == null) ? 0 : preferedItemsOrder
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CollectionSpec))
			return false;
		CollectionSpec<?> other = (CollectionSpec<?>) obj;
		if (collClass == null) {
			if (other.collClass != null)
				return false;
		} else if (!collClass.equals(other.collClass))
			return false;
		if (preferedItemsOrder != other.preferedItemsOrder)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return collClass.getName() + " " + preferedItemsOrder;
	}

}
