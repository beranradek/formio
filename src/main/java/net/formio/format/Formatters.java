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
package net.formio.format;

import java.util.Locale;

/**
 * Object capable to convert value to a String
 * according to given localization parameters,
 * and parse the string back to value of given type.
 * 
 * @author Radek Beran
 */
public interface Formatters {

	/**
	 * Parse value from a String.
	 * @param str string to parse
	 * @param destClass class of resulting parsed value
	 * @param formatPattern format of value in the string
	 * @param locale locale of value in the string
	 * @return
	 */
	<T> T parseFromString(String str, Class<T> destClass, String formatPattern, Locale locale);
	
	/**
	 * Creates String from the given value.
	 * @param value value to convert
	 * @param formatPattern format of value in the string
	 * @param locale locale of value in the string
	 * @return
	 */
	<T> String makeString(T value, String formatPattern, Locale locale);
	
	/**
	 * Returns true if formatter for given class is supported.
	 * @param cls
	 * @return
	 */
	boolean canHandle(Class<?> cls);
}
