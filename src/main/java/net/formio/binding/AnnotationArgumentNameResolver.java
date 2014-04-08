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

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Resolves name of argument using {@link ArgumentName} annotation.
 * @author Radek Beran
 */
public class AnnotationArgumentNameResolver implements ArgumentNameResolver {

	@Override
	public String getArgumentName(AccessibleObject constructionMethod, int argIndex) {
		if (constructionMethod == null)
			throw new IllegalArgumentException("constructionMethod cannot be null");
		String argName = null;
		Annotation[][] ann = null;
		if (constructionMethod instanceof Constructor) {
			ann = ((Constructor<?>)constructionMethod).getParameterAnnotations();
		} else if (constructionMethod instanceof Method) {
			ann = ((Method)constructionMethod).getParameterAnnotations();
		} else throw new IllegalStateException("Unsupported construction method '" + constructionMethod + "'");
		Annotation[] argAnnotations = ann[argIndex];
		for (int j = 0; j < argAnnotations.length; j++) {
			Annotation a = argAnnotations[j];
			if (a instanceof ArgumentName) {
				argName = ((ArgumentName) a).value();
				// ArgumentName annotation with argName found
				break;
			}
		}
		return argName;
	}
	
}
