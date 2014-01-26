package org.twinstone.formio.binding.collection;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import org.twinstone.formio.binding.PrimitiveType;

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
		if (itemClass.equals(short.class)) {
			short[] arr = new short[items.size()];
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
