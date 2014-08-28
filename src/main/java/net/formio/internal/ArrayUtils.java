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
package net.formio.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for arrays.
 * @author Radek Beran
 */
final class ArrayUtils {

	static <T> List<T> convertPrimitiveArrayToList(Object array) {
		List<T> values = new ArrayList<T>();
		if (array.getClass().equals(boolean[].class)) {
			boolean[] arr = (boolean[])array;
			for (int i = 0; i < arr.length; i++)
				values.add((T)Boolean.valueOf(arr[i]));
		} else if (array.getClass().equals(byte[].class)) {
			byte[] arr = (byte[])array;
			for (int i = 0; i < arr.length; i++)
				values.add((T)Byte.valueOf(arr[i]));
		} else if (array.getClass().equals(short[].class)) { // NOPMD by Radek on 2.3.14 18:24
			final short[] arr = (short[])array; // NOPMD by Radek on 2.3.14 18:25
			for (int i = 0; i < arr.length; i++)
				values.add((T)Short.valueOf(arr[i]));
		} else if (array.getClass().equals(int[].class)) {
			int[] arr = (int[])array;
			for (int i = 0; i < arr.length; i++)
				values.add((T)Integer.valueOf(arr[i]));
		} else if (array.getClass().equals(long[].class)) {
			long[] arr = (long[])array;
			for (int i = 0; i < arr.length; i++)
				values.add((T)Long.valueOf(arr[i]));
		} else if (array.getClass().equals(float[].class)) {
			float[] arr = (float[])array;
			for (int i = 0; i < arr.length; i++)
				values.add((T)Float.valueOf(arr[i]));
		} else if (array.getClass().equals(double[].class)) {
			double[] arr = (double[])array;
			for (int i = 0; i < arr.length; i++)
				values.add((T)Double.valueOf(arr[i]));
		} else if (array.getClass().equals(char[].class)) {
			char[] arr = (char[])array;
			for (int i = 0; i < arr.length; i++)
				values.add((T)Character.valueOf(arr[i]));
		}
		return values;
	}
	
	private ArrayUtils() {
	}
}
