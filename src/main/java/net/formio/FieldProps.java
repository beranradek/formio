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
package net.formio;

import net.formio.format.Formatter;


/**
 * Specification of form field used to construct {@link FormField}.
 * @author Radek Beran
 */
public class FieldProps<T> {
	private final String propertyName;
	private final String pattern;
	private final Formatter<T> formatter;
	
	private FieldProps(String propertyName, String pattern, Formatter<T> formatter) {
		if (propertyName == null || propertyName.isEmpty()) {
			throw new IllegalArgumentException("propertyName must be specified");
		}
		this.propertyName = propertyName;
		this.pattern = pattern;
		this.formatter = formatter;
	}
	
	public FieldProps(String propertyName, Formatter<T> formatter) {
		this(propertyName, null, formatter);
	}
	
	public FieldProps(String propertyName, String pattern) {
		this(propertyName, pattern, null);
	}
	
	public FieldProps(String propertyName) {
		this(propertyName, (String)null);
	}

	/**
	 * Name of mapped property.
	 * @return
	 */
	public String getPropertyName() {
		return this.propertyName;
	}

	/**
	 * Pattern for formatting the value.
	 * @return
	 */
	public String getPattern() {
		return this.pattern;
	}
	
	/**
	 * Formatter that formats object to String and vice versa.
	 * @return
	 */
	public Formatter<T> getFormatter() {
		return this.formatter;
	}
}
