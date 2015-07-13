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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Instantiates a class of type T using static factory method with given name.
 * @author Radek Beran
 * @param <T>
 */
public class StaticFactoryMethod extends AbstractInstantiator {

	private final Class<?> factoryClass;
	private final String methodName;
	private final Method[] instMethods;
	
	public StaticFactoryMethod(Class<?> factoryClass, String methodName) {
		if (factoryClass == null) throw new IllegalArgumentException("factoryClass cannot be null");
		if (methodName == null) throw new IllegalArgumentException("methodName cannot be null");
		this.factoryClass = factoryClass;
		this.methodName = methodName;
		
		List<Method> instMethods = new ArrayList<Method>();
		Method[] methods = factoryClass.getMethods();
		if (methods != null) {
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					instMethods.add(method);
				}
			}
		}
		this.instMethods = instMethods.toArray(new Method[0]);
	}
	
	@Override
	public <T> T instantiate(Class<T> objClass, ConstructionDescription cd, Object ... args) {
		return BindingReflectionUtils.invokeStaticMethod(
			(Method)((DefaultConstructionDescription)cd).getConstructionMethod(), 
			prepareArgs(cd.getArgTypes(), args));
	}

	@Override
	public <T> ConstructionDescription getDescription(Class<T> objClass, ArgumentNameResolver argNameResolver) {
		DefaultConstructionDescription desc = null;
		int maxArgCnt = -1; // we will choose the construction method with the max. count of usable named arguments
		for (Method c : this.instMethods) { // all public constructors
			Class<?>[] argTypes = c.getParameterTypes();
			if (argTypes.length == 0 && 0 > maxArgCnt) {
				maxArgCnt = 0;
				desc = new DefaultConstructionDescription(objClass, c, Collections.<String>emptyList());
			} else {
				// For each parameter of construction method
				List<String> argNames = new ArrayList<String>();
				for (int i = 0; i < argTypes.length; i++) {
					String argName = argNameResolver.getArgumentName(c, i);
					if (argName == null) {
						// Constructor contains argument
						// that cannot be bound and used in construction
						argNames.clear();
						break;
					}
					argNames.add(argName);
				}
				if (argNames.size() > 0 && argNames.size() > maxArgCnt) {
					// more than 0 arguments
					maxArgCnt = argNames.size();
					desc = new DefaultConstructionDescription(objClass, c, argNames);
				}
			}
		}
		if (desc == null) throw new IllegalStateException("No usable construction method " + methodName + " in class " + this.factoryClass.getName() + " was found.");
		return desc;
	}
	
}
