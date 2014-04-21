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

import java.io.Serializable;

import net.formio.format.Formatter;

/**
 * Specification of properties used to construct a {@link FormField}.
 * 
 * @author Radek Beran
 */
public class FieldProps<T> implements Serializable {
	private static final long serialVersionUID = 2328756250255932689L;
	private final String propertyName;
	private final String type;
	private final String pattern;
	private final Formatter<T> formatter;
	
	FieldProps(Builder<T> builder) {
		this.propertyName = builder.propertyName;
		this.type = builder.type;
		this.pattern = builder.pattern;
		this.formatter = builder.formatter;
	}
	
	public static class Builder<T> {
		private final String propertyName;
		private String type = null;
		private String pattern = null;
		private Formatter<T> formatter = null;

		Builder(String propertyName) {
			this(propertyName, (String)null);
		}
		
		Builder(String propertyName, String type) {
			// package-default access so only Forms (and classes in current package) can create the builder
			if (propertyName == null || propertyName.isEmpty()) throw new IllegalArgumentException("propertyName must be specified");
			this.propertyName = propertyName;
			this.type = type;
		}
		
		public Builder<T> type(String type) {
			this.type = type;
			return this;
		}
		
		public Builder<T> pattern(String pattern) {
			this.pattern = pattern;
			return this;
		}
		
		public Builder<T> formatter(Formatter<T> formatter) {
			this.formatter = formatter;
			return this;
		}
		
		public FieldProps<T> build() {
			FieldProps<T> fieldProps = new FieldProps<T>(this);
			return fieldProps;
		}
	}

	/**
	 * Name of mapped property of edited object.
	 * 
	 * @return
	 */
	public String getPropertyName() {
		return this.propertyName;
	}

	/**
	 * Type of form field, for e.g.: text, checkbox, textarea, ...
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Pattern for formatting the value.
	 * 
	 * @return
	 */
	public String getPattern() {
		return this.pattern;
	}

	/**
	 * Formatter that formats object to String and vice versa.
	 * 
	 * @return
	 */
	public Formatter<T> getFormatter() {
		return this.formatter;
	}
}
