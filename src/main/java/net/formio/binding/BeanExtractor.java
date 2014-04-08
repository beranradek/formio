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
import java.util.Map;
import java.util.Set;

/**
 * Extracts values from bean's properties.
 * @author Radek Beran
 */
public interface BeanExtractor {

	/**
	 * Extracts values from given bean.
	 * @param bean extracted bean
	 * @param allowedProperties names of properties that should be extracted (whitelist)
	 * @return values by property names
	 */
	Map<String, Object> extractBean(Object bean, Set<String> allowedProperties);
	
	boolean isIgnored(Method method);
}
