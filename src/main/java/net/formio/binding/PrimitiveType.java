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
package net.formio.binding;

import java.util.HashMap;
import java.util.Map;

/**
 * Primitive types information.
 * 
 * @param T
 * @author Radek Beran
 */
public class PrimitiveType {

	// Must precede value calls...
	private static Map<Class<?>, PrimitiveType> typesByPrimitiveClass = new HashMap<Class<?>, PrimitiveType>();
	private static Map<Class<?>, PrimitiveType> typesByWrapperClass = new HashMap<Class<?>, PrimitiveType>();
	private final Class<?> primitiveClass;
	private final Class<?> wrapperClass;
	private final Object initialValue;
	private final HumanReadableType humanReadableType;

	public static final PrimitiveType BOOLEAN = value(boolean.class, Boolean.class,
			Boolean.FALSE, HumanReadableType.LOGICAL_VALUE);
	public static final PrimitiveType BYTE = value(byte.class, Byte.class,
			Byte.valueOf((byte) 0), HumanReadableType.NUMBER);
	public static final PrimitiveType SHORT = value(short.class, Short.class, // NOPMD by Radek on 2.3.14 19:08
			Short.valueOf((short) 0), HumanReadableType.NUMBER); // NOPMD by Radek on 2.3.14 19:08
	public static final PrimitiveType INTEGER = value(int.class, Integer.class,
			Integer.valueOf(0), HumanReadableType.NUMBER);
	public static final PrimitiveType LONG = value(long.class, Long.class,
			Long.valueOf(0L), HumanReadableType.NUMBER);
	public static final PrimitiveType FLOAT = value(float.class, Float.class,
			Float.valueOf(0.0f), HumanReadableType.DECIMAL_NUMBER);
	public static final PrimitiveType DOUBLE = value(double.class, Double.class,
			Double.valueOf(0.0d), HumanReadableType.DECIMAL_NUMBER);
	public static final PrimitiveType CHARACTER = value(char.class, Character.class,
			Character.valueOf((char) 0), HumanReadableType.CHARACTER);

	public static boolean isPrimitiveType(Class<?> cls) {
		return byPrimitiveClass(cls) != null;
	}

	public static PrimitiveType byPrimitiveClass(Class<?> cls) {
		return typesByPrimitiveClass.get(cls);
	}
	
	public static PrimitiveType byWrapperClass(Class<?> cls) {
		return typesByWrapperClass.get(cls);
	}

	public static PrimitiveType byClasses(Class<?> primitiveClass, Class<?> wrapperClass) {
		PrimitiveType type = byPrimitiveClass(primitiveClass);
		if (type != null && !type.getWrapperClass().equals(wrapperClass)) {
			type = null;
		}
		return type;
	}

	PrimitiveType(Class<?> primitiveClass, Class<?> wrapperClass,
			Object initialValue, HumanReadableType humanReadableType) {
		if (primitiveClass == null)
			throw new IllegalArgumentException("primitiveClass cannot be null");
		if (wrapperClass == null)
			throw new IllegalArgumentException("wrapperClass cannot be null");
		this.primitiveClass = primitiveClass;
		this.wrapperClass = wrapperClass;
		this.initialValue = initialValue;
		this.humanReadableType = humanReadableType;
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

	public HumanReadableType getHumanReadableType() {
		return humanReadableType;
	}

	private static PrimitiveType value(Class<?> primitiveClass,
			Class<?> wrapperClass, Object initialValue,
			HumanReadableType humanReadableType) {
		PrimitiveType type = new PrimitiveType(primitiveClass, wrapperClass,
				initialValue, humanReadableType);
		typesByPrimitiveClass.put(primitiveClass, type);
		typesByWrapperClass.put(wrapperClass, type);
		return type;
	}
}
