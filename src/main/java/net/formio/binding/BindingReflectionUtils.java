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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Reflection utilities for binding purposes.
 * @author Radek Beran
 */
public final class BindingReflectionUtils {

	/**
	 * Creates new instance using specified constructor, unwrapping
	 * InvocationTargetException
	 * 
	 * @param constructor (not null)
	 * @param args arguments (vararg)
	 * @return new instance
	 * @throws RuntimeException exception thrown by method invocation
	 * @throws BindingException if unexpected exception or IllegalAccessException is thrown
	 */
	public static <T> T requireNewInstance(Constructor<T> constructor, Object... args) {
		if (args == null || args.length == 0) {
			return requireNewInstance(constructor.getDeclaringClass());
		}
		try {
			return constructor.newInstance(args);
		} catch (InvocationTargetException ex) {
			Throwable c = ex.getCause();
			if (c instanceof RuntimeException) throw (RuntimeException) c;
			if (c instanceof Error) throw (Error) c;
			if (c != null)
				throw new BindingException("Instantiating failed when binding using constructor " + constructor + ": " + c.getMessage(), c);
			throw new BindingException("Instantiating failed when binding using constructor " + constructor + ": " + ex.getMessage(), ex);
		} catch (InstantiationException ex) {
			throw new BindingException("Instantiating failed when binding using constructor " + constructor + ": " + ex.getMessage(), ex);
		} catch (IllegalAccessException ex) {
			throw new BindingException("Illegal access when binding using constructor " + constructor + ": " + ex.getMessage(), ex);
		} catch (IllegalArgumentException ex) {
			throw new BindingException("Illegal argument when binding using constructor " + constructor + ": " + ex.getMessage(), ex);
		}
	}
	
	public static <T> T requireNewInstance(Class<T> objClass) {
		T obj = null;
		try {
			obj = objClass.newInstance();
		} catch (IllegalAccessException ex) {
			throw new BindingException(
				"Default (nullary) constructor for class "
					+ objClass.getSimpleName()
					+ " not found or not accessible.", ex);
		} catch (InstantiationException ex) {
			throw new BindingException(
				"Default (nullary) constructor for class "
					+ objClass.getSimpleName()
					+ " not found or class is not instantiable with default constructor" 
					+ " or with another constructor (with resolvable arguments).", ex);
		}
		return obj;
	}
	
	public static <T> T invokeStaticMethod(Method method, Object ... args) {
		T obj = null;
		try {
			obj = (T)method.invoke(null, args);
		} catch (InvocationTargetException ex) {
			Throwable c = ex.getCause();
			if (c instanceof RuntimeException) throw (RuntimeException) c;
			if (c instanceof Error) throw (Error) c;
			if (c != null)
				throw new BindingException("Method " + method.getName() + " could not be invoked: " + c.getMessage(), c);
			throw new BindingException("Method " + method.getName() + " could not be invoked: " + ex.getMessage(), ex);
		} catch (IllegalAccessException ex) {
			throw new BindingException("Method " + method.getName() + " not found or not accessible: " + ex.getMessage(), ex);
		}
		return obj;
	}
	
	/**
	 * Returns type parameters of given type acquired via reflection.
	 * @param type
	 * @return
	 */
	public static Type[] getTypeParameters(Type type) {
		Type[] ret = null;
		if (type instanceof ParameterizedType) {  
			ParameterizedType pt = (ParameterizedType)type;
	        ret = pt.getActualTypeArguments();
	    } else {
	    	ret = new Type[0];
	    }
		return ret;
	}
	
	public static <I> Class<I> itemTypeFromGenericCollType(Type collectionType) {
		Type ret = null;
		Type[] typeParams = getTypeParameters(collectionType);
		if (typeParams != null && typeParams.length > 0) {
			ret = typeParams[0];
		}
		if (ret == null) {
			if (collectionType.equals(boolean[].class)) ret = boolean.class;
			else if (collectionType.equals(byte[].class)) ret = byte.class;
			else if (collectionType.equals(short[].class)) ret = short.class; // NOPMD by Radek on 2.3.14 19:04
			else if (collectionType.equals(int[].class)) ret = int.class;
			else if (collectionType.equals(long[].class)) ret = long.class;
			else if (collectionType.equals(float[].class)) ret = float.class;
			else if (collectionType.equals(double[].class)) ret = double.class;
			else if (collectionType.equals(char[].class)) ret = char.class;
		}
		return (Class<I>)ret;
	}
	
	private BindingReflectionUtils() {
		throw new AssertionError("Not instantiable, use static members.");
	}
}
