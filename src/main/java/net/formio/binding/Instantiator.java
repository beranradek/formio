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

/**
 * Instantiates a class of type T.
 * @author Radek Beran
 * @param <T>
 */
public interface Instantiator<T> {
	
	/**
	 * Instantiates object using given description and arguments.
	 * @param cd
	 * @param args
	 * @return
	 */
	T instantiate(ConstructionDescription cd, Object ... args);
	
	/**
	 * Returns description of construction method with max. usable arguments according to given argument name resolver
	 * (also construction method with zero arguments can be returned). If no suitable method can be found,
	 * {@link IllegalStateException} should be thrown.
	 * @param argNameResolver
	 * @return
	 */
	ConstructionDescription getDescription(ArgumentNameResolver argNameResolver);
}
