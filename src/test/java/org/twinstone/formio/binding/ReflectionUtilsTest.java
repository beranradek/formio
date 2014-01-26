package org.twinstone.formio.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class ReflectionUtilsTest {
	
	static class Person {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
	
	enum State {
		UP,
		DOWN
	}
	
	public List<String> getStrings() {
		return null;
	}
	
	public Collection<Integer> getIntegers() {
		return null;
	}
	
	public Set<Person> getPersons() {
		return null;
	}
	
	public Set<State> getStates() {
		return null;
	}
	
	public Long[] getLongsArray() {
		return null;
	}
	
	public void setStates(@SuppressWarnings("unused") Set<State> states) {
		// nothing here, only testing parameter type
	}

	@Test
	public void testGetTypeParameters() throws SecurityException, NoSuchMethodException {
		Method method = getClass().getMethod("getStrings");
		Type[] typeParams = ReflectionUtils.getTypeParameters(method.getGenericReturnType());
		Assert.assertNotNull("Type params are null", typeParams);
		Assert.assertTrue("Type params are empty", typeParams.length > 0);
		Assert.assertEquals(String.class, typeParams[0]);
		
		method = getClass().getMethod("getIntegers");
		// System.out.println(method.getReturnType().getName());
		Assert.assertNull(method.getReturnType().getComponentType());
		typeParams = ReflectionUtils.getTypeParameters(method.getGenericReturnType());
		Assert.assertNotNull("Type params are null", typeParams);
		Assert.assertTrue("Type params are empty", typeParams.length > 0);
		Assert.assertEquals(Integer.class, typeParams[0]);
		
		typeParams = ReflectionUtils.getTypeParameters(getClass().getMethod("getPersons").getGenericReturnType());
		Assert.assertEquals(Person.class, typeParams[0]);
		Assert.assertFalse(String.class.equals(typeParams[0]));
		
		typeParams = ReflectionUtils.getTypeParameters(getClass().getMethod("getStates").getGenericReturnType());
		Assert.assertEquals(State.class, typeParams[0]);
		
		method = getClass().getMethod("getLongsArray");
		Assert.assertEquals(Long.class, method.getReturnType().getComponentType());
	}
	
	@Test
	public void testSetTypeParameters() throws SecurityException {
		Method method = findMethod(getClass(), "setStates");
		Type collClass = method.getGenericParameterTypes()[0];
		Type[] typeParams = ReflectionUtils.getTypeParameters(collClass);
		Assert.assertTrue("Type params are empty", typeParams.length > 0);
		Assert.assertEquals(State.class, typeParams[0]);
	}
	
	private Method findMethod(Class<?> cls, String methodName) {
		Method ret = null;
		Method[] methods = cls.getMethods();
		if (methods != null) {
			for (Method method : methods) {
				if (method.getName().equals(methodName)) { 
					ret = method;
					break;
				}
			}
		}
		return ret;
	}

}
