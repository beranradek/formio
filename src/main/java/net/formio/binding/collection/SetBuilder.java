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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Builder of {@link Set}.
 * @author Radek Beran
 */
public class SetBuilder implements CollectionBuilder<Set<?>> {

	public static final SetBuilder HASH = new SetBuilder(ItemsOrder.HASH);
	public static final SetBuilder LINEAR = new SetBuilder(ItemsOrder.LINEAR);
	public static final SetBuilder SORTED = new SetBuilder(ItemsOrder.SORTED);
	
	protected SetBuilder(ItemsOrder itemsOrder) { this.itemsOrder = itemsOrder; }
	
	@Override
	public <I> Set<I> build(Class<I> itemClass, List<I> items) {
		Set<I> set = null;
		switch (itemsOrder) {
			case HASH:
				set = new HashSet<I>(items);
				break;
			case LINEAR:
				set = new LinkedHashSet<I>(items);
				break;
			case SORTED:
				set = new TreeSet<I>(items);
				break;
			default:
				throw new IllegalStateException("Unknown items order '" + itemsOrder + "'");
		}
		return set;
	}
	
	private ItemsOrder itemsOrder;
	
}
