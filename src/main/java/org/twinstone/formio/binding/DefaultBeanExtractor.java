package org.twinstone.formio.binding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Extracts values from properties using regular expression for an accessor.
 * @author Radek Beran
 */
public class DefaultBeanExtractor implements BeanExtractor {
	
	private final PropertyMethodRegex accessorRegex;
	
	/**
	 * Default regular expression for matching name of accessor of a property and property name within it.
	 */
	public static final PropertyMethodRegex defaultAccessorRegex = new PropertyMethodRegex("(is|get)([_a-zA-Z][_a-zA-Z0-9]*)", 2);
	
	public DefaultBeanExtractor(PropertyMethodRegex accessorRegex) {
		if (accessorRegex == null) throw new IllegalArgumentException("accessorRegex cannot be null");
		this.accessorRegex = accessorRegex;
	}
	
	public DefaultBeanExtractor() {
		this(defaultAccessorRegex);
	}
	
	@Override
	public Map<String, Object> extractBean(Object bean, Set<String> allowedProperties) {
		final Map<String, Object> valuesByNames = new HashMap<String, Object>();
		if (bean != null) {
			final Map<String, Method> properties = getClassPropertiesInternal(bean.getClass(), allowedProperties);
			for (Map.Entry<String, Method> propEntry : properties.entrySet()) {
				valuesByNames.put(propEntry.getKey(), invokeNoExc(propEntry.getValue(), bean, new Object[0]));
			}
		}
		return Collections.unmodifiableMap(valuesByNames);
	}
	
	@Override
	public Map<String, Class<?>> getPropertyClasses(Class<?> beanClass, Set<String> allowedProperties) {
		final Map<String, Method> properties = getClassPropertiesInternal(beanClass, allowedProperties);
		Map<String, Class<?>> ret = new HashMap<String, Class<?>>();
		for (Map.Entry<String, Method> prop : properties.entrySet()) {
			ret.put(prop.getKey(), prop.getValue().getReturnType());
		}
		return Collections.unmodifiableMap(ret);
	}
	
	protected boolean isAccessor(Method method) {
		return accessorRegex.matchesMethod(method.getName()) && method.getParameterTypes().length == 0;
	}
	
	private Map<String, Method> getClassPropertiesInternal(Class<?> beanClass, Set<String> allowedProperties) {
		final Map<String, Method> properties = new HashMap<String, Method>();
        final Method[] objMethods = beanClass.getMethods();
        for (Method objMethod : objMethods) {
        	if (objMethod.getName().equals("getClass")) continue;
            if (isAccessor(objMethod)) {
            	String propName = accessorRegex.getPropertyName(objMethod.getName());
            	if (propName != null && allowedProperties != null && allowedProperties.contains(propName)) {
            		properties.put(propName, objMethod);
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
			throw new RuntimeException("invocation of "+method+" failed", c);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("illegal access", e);
		}
	}
}
