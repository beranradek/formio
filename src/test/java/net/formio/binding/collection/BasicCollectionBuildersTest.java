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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.Assert;
import net.formio.binding.collection.BasicCollectionBuilders;
import net.formio.binding.collection.CollectionSpec;
import net.formio.binding.collection.ItemsOrder;

import org.junit.Test;

public class BasicCollectionBuildersTest {

	/**
	 * Tests building collections from specified type of collection, type of elements, list of items and optional collection type hints.
	 */
	@Test
	public void testBuildCollections() {
		BasicCollectionBuilders collBuilders = new BasicCollectionBuilders();
		List<Integer> items = Arrays.asList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
		
		@SuppressWarnings("unchecked")
		List<Integer> builtList = collBuilders.buildCollection(CollectionSpec.getInstance(List.class, ItemsOrder.LINEAR), Integer.class, items);
		Assert.assertNotNull(builtList);
		Assert.assertTrue(builtList.getClass().equals(ArrayList.class));
		Assert.assertEquals(builtList, new ArrayList<Integer>(items));
		
		@SuppressWarnings("unchecked")
		Set<Integer> builtSet = collBuilders.buildCollection(CollectionSpec.getInstance(Set.class, ItemsOrder.HASH), Integer.class, items);
		Assert.assertNotNull(builtSet);
		Assert.assertTrue(builtSet.getClass().equals(HashSet.class));
		Assert.assertEquals(builtSet, new HashSet<Integer>(items));
		
		@SuppressWarnings("unchecked")
		Set<Integer> builtLinearSet = collBuilders.buildCollection(CollectionSpec.getInstance(Set.class, ItemsOrder.LINEAR), Integer.class, items);
		Assert.assertNotNull(builtLinearSet);
		Assert.assertTrue(builtLinearSet.getClass().equals(LinkedHashSet.class));
		Assert.assertEquals(builtLinearSet, new LinkedHashSet<Integer>(items));
		
		@SuppressWarnings("unchecked")
		Set<Integer> builtSortedSet = collBuilders.buildCollection(CollectionSpec.getInstance(Set.class, ItemsOrder.SORTED), Integer.class, items);
		Assert.assertNotNull(builtSortedSet);
		Assert.assertTrue(builtSortedSet.getClass().equals(TreeSet.class));
		Assert.assertEquals(builtSortedSet, new TreeSet<Integer>(items));
		
		Integer[] arr = collBuilders.buildCollection(CollectionSpec.getInstance(new Integer[]{}.getClass(), ItemsOrder.LINEAR), Integer.class, items);
		Assert.assertNotNull(arr);
		Assert.assertTrue(arr.getClass().equals(new Integer[]{}.getClass()));
		Assert.assertTrue(Arrays.equals(arr, new Integer[]{Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)}));
		
		int[] arrPrimitive = collBuilders.buildCollection(CollectionSpec.getInstance(new int[]{}.getClass(), ItemsOrder.LINEAR), int.class, items);
		Assert.assertNotNull(arrPrimitive);
		Assert.assertTrue(arrPrimitive.getClass().equals(new int[]{}.getClass()));
		Assert.assertTrue(Arrays.equals(arrPrimitive, new int[]{1, 2, 3}));
	}

}
