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
 * Formatter that formats object to String and vice versa.
 * @author Radek Beran
 *
 * @param <T> type of object
 */
public interface Formatter<T> {
	/**
	 * Parses object from given string.
	 * @param str string to parse
	 * @param destClass desired class of parsed object (useful for specifying concrete enumeration class)
	 * @param formatPattern specified format of string; or {@code null}
	 * @param locale specified locale of string; or {@code null}
	 * @return parsed object
	 */
	T parseFromString(String str, Class<T> destClass, String formatPattern, Locale locale);
	
	/**
	 * Creates string from given value.
	 * @param value value to convert to string
	 * @param formatPattern specified format of string; or {@code null}
	 * @param locale specified locale of string; or {@code null}
	 * @return result string
	 */
	String makeString(T value, String formatPattern, Locale locale);
}
