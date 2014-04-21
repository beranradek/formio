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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public class ConstructionDescription {

	private final AccessibleObject constructionMethod;
	private final List<String> argNames;

	protected ConstructionDescription(final AccessibleObject constructionMethod, final List<String> argNames) {
		if (argNames == null) throw new IllegalArgumentException("argNames cannot be null");
		this.constructionMethod = constructionMethod; // can be null if instantiator for e.g. already holds pre-prepared instance
		this.argNames = argNames;
	}

	public Type[] getGenericParamTypes() {
		Type[] paramTypes = null;
		if (constructionMethod == null) {
			paramTypes = new Type[0];
		} else {
			if (constructionMethod instanceof Constructor) {
				paramTypes = ((Constructor<?>)constructionMethod).getGenericParameterTypes();
			} else if (constructionMethod instanceof Method) {
				paramTypes = ((Method)constructionMethod).getGenericParameterTypes();
			} else throw new IllegalStateException("Unsupported construction method '" + constructionMethod + "'");
		}
		return paramTypes;
	}

	public List<String> getArgNames() {
		return argNames;
	}
	
	public Class<?>[] getArgTypes() {
		Class<?>[] argTypes = null;
		if (constructionMethod == null) {
			argTypes = new Class<?>[0];
		} else {
			if (constructionMethod instanceof Constructor) {
				argTypes = ((Constructor<?>)constructionMethod).getParameterTypes();
			} else if (constructionMethod instanceof Method) {
				argTypes = ((Method)constructionMethod).getParameterTypes();
			} else throw new IllegalStateException("Unsupported construction method '" + constructionMethod + "'");
		}
		return argTypes;
	}
	
	/**
	 * Auxiliary method, should be used only by instantiators that produce this construction description.
	 * @return
	 */
	AccessibleObject getConstructionMethod() {
		return this.constructionMethod;
	}

}
