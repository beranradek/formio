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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.formio.choice.ChoiceProvider;
import net.formio.choice.ChoiceRenderer;
import net.formio.format.Formatter;
import net.formio.format.Formatters;
import net.formio.internal.FormUtils;
import net.formio.props.FieldProperty;

/**
 * Specification of properties used to construct a {@link FormField}.
 * Builder of {@link FormField}.
 * 
 * @author Radek Beran
 */
public class FieldProps<T> implements Serializable {
	private static final long serialVersionUID = 2328756250255932689L;
	private String propertyName;
	private String name;
	private String type;
	private String pattern;
	private Formatter<T> formatter;
	private ChoiceProvider<T> choiceProvider;
	private ChoiceRenderer<T> choiceRenderer;
	private FormProperties formProperties = new FormPropertiesImpl(FieldProperty.createDefaultFieldProperties());
	List<T> filledObjects = new ArrayList<T>();
	String strValue;
	
	FieldProps(String propertyName) {
		this(propertyName, (String)null);
	}
		
	FieldProps(String propertyName, String type) {
		// package-default access so only Forms (and classes in current package) can create the builder
		if (propertyName == null || propertyName.isEmpty()) throw new IllegalArgumentException("propertyName must be specified");
		this.propertyName = propertyName;
		this.type = type;
	}
	
	FieldProps(FormField<T> field) {
		initFromField(field);
	}
	
	FieldProps(FormField<T> field, 
		List<T> values, 
		Locale locale, 
		Formatters formatters, 
		String preferedStringValue) {
		String strValue = null;
		if (preferedStringValue != null) {
			strValue = preferedStringValue; 
		} else if (values.size() > 0) {
			strValue = valueAsString(values.get(0), field.getPattern(), field.getFormatter(), locale, formatters);
		}
		// "this" cannot be used before this initialization of fields:
		initFromField(field).value(strValue);
	}
	
	// only for internal usage
	FieldProps<T> name(String name) {
		this.name = name;
		return this;
	}
	
	// only for internal usage
	FieldProps<T> value(String value) {
		this.strValue = value;
		return this;
	}
		
	public FieldProps<T> type(String type) {
		this.type = type;
		return this;
	}
		
	public FieldProps<T> pattern(String pattern) {
		this.pattern = pattern;
		return this;
	}
		
	public FieldProps<T> formatter(Formatter<T> formatter) {
		this.formatter = formatter;
		return this;
	}
	
	public FieldProps<T> choiceProvider(ChoiceProvider<T> choiceProvider) {
		this.choiceProvider = choiceProvider;
		return this;
	}
	
	public FieldProps<T> choiceRenderer(ChoiceRenderer<T> choiceRenderer) {
		this.choiceRenderer = choiceRenderer;
		return this;
	}
	
	public <U> FieldProps<T> property(FieldProperty<U> fieldProperty, U value) {
		this.formProperties = new FormPropertiesImpl(this.formProperties, fieldProperty, value);
		return this;
	}
	
	public FieldProps<T> visible(boolean visible) {
		return property(FieldProperty.VISIBLE, Boolean.valueOf(visible));
	}
	
	public FieldProps<T> enabled(boolean enabled) {
		return property(FieldProperty.ENABLED, Boolean.valueOf(enabled));
	}
	
	public FieldProps<T> readonly(boolean readonly) {
		return property(FieldProperty.READ_ONLY, Boolean.valueOf(readonly));
	}

	public FieldProps<T> required(boolean required) {
		return property(FieldProperty.REQUIRED, Boolean.valueOf(required));
	}
	
	public FieldProps<T> help(String help) {
		return property(FieldProperty.HELP, help);
	}

	/**
	 * Name of mapped property of edited object.
	 * 
	 * @return
	 */
	public String getPropertyName() {
		String propName = null;
		if (this.propertyName != null && !this.propertyName.isEmpty()) {
			propName = this.propertyName;
		} else if (this.name != null && !this.name.isEmpty()) {
			propName = FormUtils.fieldNameToLastPropertyName(this.name);
		}
		return propName;
	}
	
	/**
	 * Whole path (name) of form field.
	 * @return
	 */
	public String getName() {
		return this.name;
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
	 * Returns provider of all possible codebook items used when rendering this form field;
	 * or {@code null}.
	 * @return
	 */
	public ChoiceProvider<T> getChoiceProvider() {
		return this.choiceProvider;
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
	 * Field form properties (flags like required, ... - see {@link FieldProperty}).
	 * @return
	 */
	public FormProperties getFormProperties() {
		return this.formProperties;
	}
	
	/**
	 * Constructs new immutable form field.
	 * @return
	 */
	public FormField<T> build() {
		return build(null);
	}
	
	/**
	 * Constructs new immutable form field.
	 * @return
	 */
	FormField<T> build(String parentPath) {
		return new FormFieldImpl<T>(this, parentPath);
	}
	
	void checkConsistentNames() {
		if (name != null && !name.isEmpty() && 
			propertyName != null && !propertyName.isEmpty()) {
			String propNameFromFullName = FormUtils.fieldNameToLastPropertyName(name);
			if (!propNameFromFullName.equals(propertyName)) {
				throw new IllegalStateException("Property name '" + propertyName + "' is not consistent with full field name '" + name + "'!");
			}
		}
	}
	
	private static <T> String valueAsString(T value, String pattern, Formatter<T> formatter, Locale locale, Formatters formatters) {
		if (value == null) return null;
		String str = null;
		if (formatter != null) {
			// formatter is specified explicitly by user
			str = formatter.makeString(value, pattern, locale);
		} else {
			// choose a suitable formatter from available formatters
			str = formatters.makeString(value, pattern, locale);
		}
		return str;
	}
	
	private FieldProps<T> initFromField(FormField<T> field) {
		this.propertyName = null;
		this.name = field.getName();
		this.type = field.getType();
		this.pattern = field.getPattern();
		this.formatter = field.getFormatter();
		this.choiceProvider = field.getChoiceProvider();
		this.choiceRenderer = field.getChoiceRenderer();
		this.formProperties = field.getFormProperties();
		this.filledObjects = field.getFilledObjects();
		this.strValue = field.getValue();
		return this;
	}
}
