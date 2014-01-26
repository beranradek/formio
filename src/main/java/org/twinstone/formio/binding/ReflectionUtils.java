package org.twinstone.formio.binding;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Reflection utilities for form library's purposes (default visibility).
 * @author Radek Beran
 */
class ReflectionUtils {

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
	static <T> T requireNewInstance(Constructor<T> constructor,
			Object... args) {
		try {
			return constructor.newInstance(args);
		} catch (InvocationTargetException ex) {
			Throwable c = ex.getCause();
			if (c instanceof RuntimeException)
				throw (RuntimeException) c;
			if (c instanceof Error)
				throw (Error) c;
			throw new BindingException("Instantiating failed", c);
		} catch (InstantiationException ex) {
			throw new BindingException("Instantiating failed", ex);
		} catch (IllegalAccessException ex) {
			throw new BindingException("Illegal access", ex);
		}
	}
	
	/**
	 * Returns type parameters of given type acquired via reflection.
	 * @param type
	 * @return
	 */
	static Type[] getTypeParameters(Type type) {
		Type[] ret = null;
		if (type instanceof ParameterizedType) {  
			ParameterizedType pt = (ParameterizedType)type;
	        ret = pt.getActualTypeArguments();
	    } else {
	    	ret = new Type[0];
	    }
		return ret;
	}
	
	private ReflectionUtils() {
		throw new AssertionError("Not instantiable, use static members.");
	}
}
