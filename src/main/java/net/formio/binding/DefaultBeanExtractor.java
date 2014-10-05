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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Extracts values from properties using regular expression for an accessor.
 * @author Radek Beran
 */
public class DefaultBeanExtractor implements BeanExtractor {
	
	private final PropertyMethodRegex accessorRegex;
	private static final Object[] NO_ARGS = new Object[0];
	
	/**
	 * Default regular expression for matching name of accessor of a property and 
	 * property name within it.
	 */
	public static final PropertyMethodRegex DEFAULT_ACCESSOR_REGEX = new PropertyMethodRegex("(is|get)([_a-zA-Z][_a-zA-Z0-9]*)", 2);
	
	public DefaultBeanExtractor(final PropertyMethodRegex accessorRegex) {
		if (accessorRegex == null) throw new IllegalArgumentException("accessorRegex cannot be null");
		this.accessorRegex = accessorRegex;
	}
	
	public DefaultBeanExtractor() {
		this(DEFAULT_ACCESSOR_REGEX);
	}
	
	@Override
	public Map<String, Object> extractBean(Object bean, final Set<String> allowedProperties) {
		final Map<String, Object> valuesByNames = new LinkedHashMap<String, Object>();
		if (bean != null) {
			final Map<String, Method> properties = getClassPropertiesInternal(bean.getClass(), allowedProperties);
			for (Map.Entry<String, Method> propEntry : properties.entrySet()) {
				valuesByNames.put(propEntry.getKey(), invokeNoExc(propEntry.getValue(), bean, NO_ARGS));
			}
		}
		return Collections.unmodifiableMap(valuesByNames);
	}
	
	@Override
	public boolean isIgnored(Method method) {
		return method.getAnnotation(Ignored.class) != null;
	}
	
	protected boolean isAccessor(Method method) {
		return accessorRegex.matchesMethod(method.getName()) && method.getParameterTypes().length == 0;
	}
	
	private Map<String, Method> getClassPropertiesInternal(Class<?> beanClass, Set<String> allowedProperties) {
		final Map<String, Method> properties = new LinkedHashMap<String, Method>();
        final Method[] objMethods = beanClass.getMethods();
        for (Method objMethod : objMethods) {
        	if (objMethod.getName().equals("getClass")) continue;
            if (isAccessor(objMethod)) {
            	if (isIgnored(objMethod)) {
            		// ignored property
            	} else {
	            	String propName = accessorRegex.getPropertyName(objMethod.getName());
		            if (propName != null && allowedProperties != null && allowedProperties.contains(propName)) {
		            	properties.put(propName, objMethod);
		            }
            	}
            }
        }
		return Collections.unmodifiableMap(properties);
	}
	
	/**
	 * Invoke method, unwrapping InvocationTargetException
	 * @param method method to call (not null)
	 * @param instance instance to apply on (may be null for static methods)
	 * @param args arguments (vararg)
	 * @return invocation result (null for void method)
	 * @throws RuntimeException exception thrown by method invocation
	 * @throws RuntimeException if unexpected exception or IllegalAccessException is thrown
	 */
	private Object invokeNoExc(Method method, Object instance, Object... args) {
		try {
			return method.invoke(instance, args);
		} catch (InvocationTargetException e) {
			Throwable c = e.getCause();
			if (c instanceof RuntimeException)
				throw (RuntimeException) c;
			if (c instanceof Error)
				throw (Error) c;
			throw new DataExtractionException("invocation of "+method+" failed", c);
		} catch (IllegalAccessException e) {
			throw new DataExtractionException("illegal access: " + e.getMessage(), e);
		}
	}
}
