package org.twinstone.formio.binding;

import java.util.HashMap;
import java.util.Map;

/**
 * Primitive types information.
 * @param T
 * @author Radek Beran
 */
public class PrimitiveType {
	
	// Must precede value calls...
	private static Map<Class<?>, PrimitiveType> typesByPrimitiveClass = new HashMap<Class<?>, PrimitiveType>();
	private final Class<?> primitiveClass;
	private final Class<?> wrapperClass;
	private final Object initialValue;

	public static PrimitiveType BOOLEAN = value(boolean.class, Boolean.class, Boolean.FALSE);
	public static PrimitiveType BYTE = value(byte.class, Byte.class, Byte.valueOf((byte) 0));
	public static PrimitiveType SHORT = value(short.class, Short.class, Short.valueOf((short) 0));
	public static PrimitiveType INTEGER = value(int.class, Integer.class, Integer.valueOf(0));
	public static PrimitiveType LONG = value(long.class, Long.class, Long.valueOf(0L));
	public static PrimitiveType FLOAT = value(float.class, Float.class, Float.valueOf(0.0f));
	public static PrimitiveType DOUBLE = value(double.class, Double.class, Double.valueOf(0.0d));
	public static PrimitiveType CHARACTER = value(char.class, Character.class, Character.valueOf((char)0));
	
	public static boolean isPrimitiveType(Class<?> cls) {
		return byPrimitiveClass(cls) != null;
	}
	
	public static PrimitiveType byPrimitiveClass(Class<?> cls) {
		return typesByPrimitiveClass.get(cls);
	}
	
	public static PrimitiveType byClasses(Class<?> primitiveClass, Class<?> wrapperClass) {
		PrimitiveType type = byPrimitiveClass(primitiveClass);
		if (type != null && !type.getWrapperClass().equals(wrapperClass)) {
			type = null;
		}
		return type;
	}
	
	PrimitiveType(Class<?> primitiveClass, Class<?> wrapperClass,
			Object initialValue) {
		if (primitiveClass == null) throw new IllegalArgumentException("primitiveClass cannot be null");
		if (wrapperClass == null) throw new IllegalArgumentException("wrapperClass cannot be null");
		this.primitiveClass = primitiveClass;
		this.wrapperClass = wrapperClass;
		this.initialValue = initialValue;
	}

	public Class<?> getPrimitiveClass() {
		return primitiveClass;
	}

	public Class<?> getWrapperClass() {
		return wrapperClass;
	}

	public Object getInitialValue() {
		return initialValue;
	}

	private static PrimitiveType value(Class<?> primitiveClass, Class<?> wrapperClass, Object initialValue) {
		PrimitiveType type = new PrimitiveType(primitiveClass, wrapperClass, initialValue);
		typesByPrimitiveClass.put(primitiveClass, type);
		return type;
	}
}
