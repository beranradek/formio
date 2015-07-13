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

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import net.formio.binding.BindingReflectionUtils;

/**
 * Builds various common collections and arrays from given specification 
 * (collection class and ordering of items) and given items.
 * Different subclasses with different registered collection builders can be
 * created: Method {@link #registerBuilders()} can be overridden.
 * 
 * @author Radek Beran
 */
public class BasicCollectionBuilders implements CollectionBuilders {

	/**
	 * Intentionally left default constructor, that can be used with dependency
	 * injection. Subclasses can define and inject their own mechanisms to
	 * build collections.
	 */
	public BasicCollectionBuilders() {
	}
	
	@Override
	public <C, I> C buildCollection(CollectionSpec<C> collSpec, Class<I> itemClass, List<I> items) {
		ensureBuildersRegistered();
		CollectionSpec<C> cSpec = null;
		if (collSpec.getCollClass().isArray()) {
			cSpec = (CollectionSpec<C>)CollectionSpec.getInstance(Array.class, collSpec.getPreferedItemsOrder());
		} else {
			cSpec = collSpec;
		}
		CollectionBuilder<C> collBuilder = (CollectionBuilder<C>)BUILDERS_CACHE.get(cSpec);
		if (collBuilder == null)
			throw new CollectionBuilderNotFoundException(cSpec);
		return collBuilder.build(itemClass, items);
	}
	
	@Override
	public boolean canHandle(CollectionSpec<?> collSpec) {
		ensureBuildersRegistered();
		return collSpec.getCollClass().isArray() || BUILDERS_CACHE.get(collSpec) != null;
	}
	
	@Override
	public Class<?> getItemClass(Class<?> parentClass, String propertyName, Type genericCollectionType) {
		return BindingReflectionUtils.itemTypeFromGenericCollType(genericCollectionType);
	}
	
	protected Map<CollectionSpec<?>, CollectionBuilder<?>> registerBuilders() {
		if (BUILDERS_CACHE.isEmpty()) {
			BUILDERS_CACHE.put(CollectionSpec.getInstance(List.class, ItemsOrder.LINEAR), ListBuilder.LINEAR);
			BUILDERS_CACHE.put(CollectionSpec.getInstance(ArrayList.class, ItemsOrder.LINEAR), ListBuilder.LINEAR);
			BUILDERS_CACHE.put(CollectionSpec.getInstance(Set.class, ItemsOrder.LINEAR), SetBuilder.LINEAR);
			BUILDERS_CACHE.put(CollectionSpec.getInstance(Set.class, ItemsOrder.HASH), SetBuilder.HASH);
			BUILDERS_CACHE.put(CollectionSpec.getInstance(Set.class, ItemsOrder.SORTED), SetBuilder.SORTED);
			BUILDERS_CACHE.put(CollectionSpec.getInstance(LinkedHashSet.class, ItemsOrder.LINEAR), SetBuilder.LINEAR);
			BUILDERS_CACHE.put(CollectionSpec.getInstance(HashSet.class, ItemsOrder.HASH), SetBuilder.HASH);
			BUILDERS_CACHE.put(CollectionSpec.getInstance(TreeSet.class, ItemsOrder.SORTED), SetBuilder.SORTED);
			BUILDERS_CACHE.put(CollectionSpec.getInstance(Collection.class, ItemsOrder.LINEAR), ListBuilder.LINEAR);
			// Note: Array is auxiliary class for arrays, expected by buildCollection
			BUILDERS_CACHE.put(CollectionSpec.getInstance(Array.class, ItemsOrder.LINEAR), ArrayBuilder.LINEAR);
			BUILDERS_CACHE.put(CollectionSpec.getInstance(Array.class, ItemsOrder.SORTED), ArrayBuilder.SORTED);
		}
		return BUILDERS_CACHE;
	}
	
	private void ensureBuildersRegistered() {
		if (BUILDERS_CACHE.isEmpty()) registerBuilders();
	}
	
	private static final Map<CollectionSpec<?>, CollectionBuilder<?>> BUILDERS_CACHE = new ConcurrentHashMap<CollectionSpec<?>, CollectionBuilder<?>>();
	
}
