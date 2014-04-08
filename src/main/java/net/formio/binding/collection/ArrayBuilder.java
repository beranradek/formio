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
import java.util.Arrays;
import java.util.List;

import net.formio.binding.PrimitiveType;

/**
 * Builder of an array.
 * @author Radek Beran
 */
public class ArrayBuilder implements CollectionBuilder<Object> {

	public static final ArrayBuilder LINEAR = new ArrayBuilder(false);
	public static final ArrayBuilder SORTED = new ArrayBuilder(true);
	
	@Override
	public <I> Object build(Class<I> itemClass, List<I> items) {
		final int length = items.size();
		if (PrimitiveType.isPrimitiveType(itemClass)) {
			return newPrimitiveArray(itemClass, items, this.sorted);
		}
		@SuppressWarnings("unchecked") // from Class<I> item type only I[] can be constructed using Array.newInstance
		I[] arr = (I[])Array.newInstance(itemClass, length);
		for (int i = 0; i < length; i++) {
			arr[i] = items.get(i);
		}
		if (this.sorted) Arrays.sort(arr);
		return arr;
	}
	
	protected ArrayBuilder(boolean sorted) { this.sorted = sorted; }
	
	private boolean sorted;
	
	Object newPrimitiveArray(Class<?> itemClass, List<?> items, boolean sorted) {
		if (itemClass.equals(boolean.class)) {
			boolean[] arr = new boolean[items.size()];
			for (int i = 0; i < items.size(); i++) {
			  arr[i] = ((Boolean)items.get(i)).booleanValue();	
			}
			// boolean array cannot be sorted
			return arr;
		}
		if (itemClass.equals(byte.class)) {
			byte[] arr = new byte[items.size()];
			for (int i = 0; i < items.size(); i++) {
			  arr[i] = ((Byte)items.get(i)).byteValue();	
			}
			if (sorted) Arrays.sort(arr);
			return arr;
		}
		if (itemClass.equals(short.class)) { // NOPMD by Radek on 2.3.14 18:49
			final short[] arr = new short[items.size()]; // NOPMD by Radek on 2.3.14 18:49
			for (int i = 0; i < items.size(); i++) {
			  arr[i] = ((Short)items.get(i)).shortValue();
			}
			if (sorted) Arrays.sort(arr);
			return arr;
		}
		if (itemClass.equals(int.class)) {
			int[] arr = new int[items.size()];
			for (int i = 0; i < items.size(); i++) {
			  arr[i] = ((Integer)items.get(i)).intValue();
			}
			if (sorted) Arrays.sort(arr);
			return arr;
		}
		if (itemClass.equals(long.class)) {
			long[] arr = new long[items.size()];
			for (int i = 0; i < items.size(); i++) {
			  arr[i] = ((Long)items.get(i)).longValue();
			}
			if (sorted) Arrays.sort(arr);
			return arr;
		}
		if (itemClass.equals(float.class)) {
			float[] arr = new float[items.size()];
			for (int i = 0; i < items.size(); i++) {
			  arr[i] = ((Float)items.get(i)).floatValue();
			}
			if (sorted) Arrays.sort(arr);
			return arr;
		}
		if (itemClass.equals(double.class)) {
			double[] arr = new double[items.size()];
			for (int i = 0; i < items.size(); i++) {
			  arr[i] = ((Double)items.get(i)).doubleValue();
			}
			if (sorted) Arrays.sort(arr);
			return arr;
		}
		if (itemClass.equals(char.class)) {
			char[] arr = new char[items.size()];
			for (int i = 0; i < items.size(); i++) {
			  arr[i] = ((Character)items.get(i)).charValue();
			}
			if (sorted) Arrays.sort(arr);
			return arr;
		}
		throw new IllegalStateException("Unsupported primitive type " + itemClass.getName() + " for array creation.");
	}
	
}
