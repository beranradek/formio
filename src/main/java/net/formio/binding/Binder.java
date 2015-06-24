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

import java.util.Map;

/**
 * Binds given values to new/existing instance of class.
 * @author Radek Beran
 */
public interface Binder {
	
	/**
	 * Binds given values to new instance of class.
	 * @param objClass instantiated class
	 * @param instantiator instantiator of class T, {@code null} if default {@link ConstructorInstantiator} should be used
	 * @param values values to bind to new instance
	 * @return
	 */
	<T> BoundData<T> bindToNewInstance(Class<T> objClass, Instantiator instantiator, Map<String, BoundValuesInfo> values);
}
