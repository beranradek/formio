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

import net.formio.choice.ChoiceRenderer;
import net.formio.common.heterog.HeterogCollections;
import net.formio.common.heterog.HeterogMap;
import net.formio.format.Formatter;
import net.formio.props.FieldProperty;

/**
 * Specification of formProperties used to construct a {@link FormField}.
 * 
 * @author Radek Beran
 */
public class FieldProps<T> implements Serializable {
	private static final long serialVersionUID = 2328756250255932689L;
	private final String propertyName;
	private final String type;
	private final String pattern;
	private final Formatter<T> formatter;
	private final ChoiceRenderer<T> choiceRenderer;
	private final HeterogMap<String> properties;
	
	@SuppressWarnings("synthetic-access")
	FieldProps(Builder<T> builder) {
		this.propertyName = builder.propertyName;
		this.type = builder.type;
		this.pattern = builder.pattern;
		this.formatter = builder.formatter;
		this.choiceRenderer = builder.choiceRenderer;
		this.properties = HeterogCollections.unmodifiableMap(builder.properties);
	}
	
	public static class Builder<T> {
		private final String propertyName;
		private String type = null;
		private String pattern = null;
		private Formatter<T> formatter = null;
		private ChoiceRenderer<T> choiceRenderer = null;
		private final HeterogMap<String> properties;

		Builder(String propertyName) {
			this(propertyName, (String)null);
		}
		
		Builder(String propertyName, String type) {
			// package-default access so only Forms (and classes in current package) can create the builder
			if (propertyName == null || propertyName.isEmpty()) throw new IllegalArgumentException("propertyName must be specified");
			this.propertyName = propertyName;
			this.type = type;
			this.properties = FieldProperty.createDefaultFieldProperties();
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
		
		public Builder<T> choiceRenderer(ChoiceRenderer<T> choiceRenderer) {
			this.choiceRenderer = choiceRenderer;
			return this;
		}
		
		public <U> Builder<T> property(FieldProperty<U> fieldProperty, U value) {
			this.properties.putTyped(fieldProperty, value);
			return this;
		}
		
		public Builder<T> visible(boolean visible) {
			return property(FieldProperty.VISIBLE, Boolean.valueOf(visible));
		}
		
		public Builder<T> enabled(boolean enabled) {
			return property(FieldProperty.ENABLED, Boolean.valueOf(enabled));
		}
		
		public Builder<T> readonly(boolean readonly) {
			return property(FieldProperty.READ_ONLY, Boolean.valueOf(readonly));
		}
	
		public Builder<T> required(boolean required) {
			return property(FieldProperty.REQUIRED, Boolean.valueOf(required));
		}
		
		public Builder<T> help(String help) {
			return property(FieldProperty.HELP, help);
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
	
	/**
	 * Returns renderer that prepares ids and titles of items when this
	 * form field represents a choice from the codebook item(s); or {@code null} if this
	 * form field does not represent such a choice.
	 * @return
	 */
	public ChoiceRenderer<T> getChoiceRenderer() {
		return this.choiceRenderer;
	}
	
	/**
	 * Field formProperties (flags like required, ... - see {@link FieldProperty}).
	 * @return
	 */
	public HeterogMap<String> getProperties() {
		return this.properties;
	}
}
