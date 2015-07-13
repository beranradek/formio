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

import java.lang.reflect.Type;
import java.util.List;

/**
 * Factory of collection builders.
 * @author Radek Beran
 */
public interface CollectionBuilders {

	/**
	 * Creates instance of collection according to given specification, type of collection 
	 * item and actual values for items.
	 * @param collSpec
	 * @param itemClass
	 * @param items
	 * @return constructed collection
	 */
	<C, I> C buildCollection(CollectionSpec<C> collSpec, Class<I> itemClass, List<I> items);
	
	/**
	 * Returns true if this collection builders instance can construct collection with given specification.
	 * @param collSpec
	 * @return true if such a collection can be constructed
	 */
	boolean canHandle(CollectionSpec<?> collSpec);

	/**
	 * Returns type of collection item.
	 * This method is useful to customize logic for finding class of collection item, since different JVM languages
	 * can have their own reflection systems. This method should provide sufficient inputs to determine
	 * collection item type.
	 * @param parentClass parent class in which the collection is present as a property
	 * @param propertyName name of property with collection in parent class
	 * @param genericCollectionType generic type of collection
	 * @return type of collection item
	 */
	Class<?> getItemClass(Class<?> parentClass, String propertyName, Type genericCollectionType);
}
