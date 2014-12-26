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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.formio.choice.ChoiceRenderer;
import net.formio.common.heterog.HeterogMap;
import net.formio.format.Formatter;
import net.formio.format.Formatters;
import net.formio.internal.FormUtils;
import net.formio.props.FieldProperty;

/**
 * Form field. Immutable.
 * @author Radek Beran
 */
public class FormFieldImpl<T> implements FormField<T> {
	// public because of introspection required by some template frameworks, constructors are not public
	
	private final String name;
	private final String type;
	/** Data filled in form field - for e.g. items from a codebook. */
	private final List<T> filledObjects;
	private final String pattern;
	private final Formatter<T> formatter;
	/** Renderer of items, if this form field represents a choice from a codebook. */
	private final ChoiceRenderer<T> choiceRenderer;
	private final String strValue;
	private final FormProperties formProperties;
	
	static <T> FormFieldImpl<T> getInstance(
		String name, 
		String type, 
		String pattern, 
		Formatter<T> formatter,
		ChoiceRenderer<T> choiceRenderer,
		FormProperties properties) {
		return getFilledInstance(name, type, pattern, formatter, choiceRenderer, properties, 
			Collections.<T>emptyList(), (Locale)null, (Formatters)null, (String)null);
	}
	
	static <T> FormFieldImpl<T> getFilledInstance(
		String name, 
		String type, 
		String pattern, 
		Formatter<T> formatter,
		ChoiceRenderer<T> choiceRenderer,
		FormProperties properties, 
		List<T> values, 
		Locale locale, 
		Formatters formatters, 
		String preferedStringValue) {
		
		String strValue = null;
		if (preferedStringValue != null) {
			strValue = preferedStringValue; 
		} else if (values.size() > 0) {
			strValue = valueAsString(values.get(0), pattern, formatter, locale, formatters);
		}
		return new FormFieldImpl<T>(name, type, pattern, formatter, choiceRenderer, properties, values, strValue);
	}

	/**
	 * Returns copy of field with given required flag.
	 * @param src
	 * @param required null if required flag is not specified
	 */
	FormFieldImpl(FormField<T> src, Boolean required) {
		this(src, src.getName(), (String)null, required);
	}

	/**
	 * Returns copy of this field with new name that contains index after given name prefix.
	 * @param src
	 * @param index
	 * @param namePrefixWithoutIndex
	 * @return
	 */
	FormFieldImpl(FormField<T> src, int index, String namePrefixWithoutIndex) {
		this(src, namePrefixWithoutIndex + "[" + index + "]" + src.getName().substring(namePrefixWithoutIndex.length()), 
			namePrefixWithoutIndex, (Boolean)null);
	}
	
	/**
	 * Returns copy of given field with new name that has given prefix 
	 * prepended to the previous name of source field.
	 * @param src
	 * @param namePrefixToPrepend
	 * @return
	 */
	FormFieldImpl(FormField<T> src, String namePrefixToPrepend) {
		this(src, namePrefixToPrepend + Forms.PATH_SEP + src.getName(), namePrefixToPrepend, (Boolean)null);
	}
	
	/**
	 * Returns copy of given field with given name.
	 * @param src copied form field
	 * @param name full name of form field
	 * @param namePrefix if not null, name must start with this prefix
	 * @param required null if required flag is not specified
	 * @return
	 */
	private FormFieldImpl(FormField<T> src, String name, String namePrefix, Boolean required) {
		this(validateName(name, namePrefix), 
			src.getType(), 
			src.getPattern(), 
			src.getFormatter(),
			src.getChoiceRenderer(),
			// Override required only in case required != null, so the required flag from field props is not
			// overriden by missing NotNull annotation...
			required != null ? 
				((FormPropertiesImpl)src.getFormProperties()).withProperty(FieldProperty.REQUIRED, required) :
				src.getFormProperties(),
			new ArrayList<T>(src.getFilledObjects()), 
			src.getValue());
	}
	
	private FormFieldImpl(
		String name, 
		String type, 
		String pattern, 
		Formatter<T> formatter, 
		ChoiceRenderer<T> choiceRenderer,
		FormProperties properties, 
		List<T> filledObjects, 
		String strValue) {
			
		if (properties == null) throw new IllegalArgumentException("formProperties cannot be null, only empty");
		if (filledObjects == null) throw new IllegalArgumentException("filledObjects cannot be null, only empty");
		this.name = name;
		this.type = type;
		this.pattern = pattern;
		this.formatter = formatter;
		this.choiceRenderer = choiceRenderer;
		this.filledObjects = filledObjects;
		this.strValue = strValue;
		this.formProperties = properties;
	}

	/**
	 * Name of edited property prefixed with form name.
	 */
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public String getLabelKey() {
		return FormUtils.labelKeyForName(this.name);
	}
	
	@Override
	public List<T> getFilledObjects() {
		return filledObjects;
	}
	
	@Override
	public T getFilledObject() {
		T obj = null;
		if (this.filledObjects != null && !this.filledObjects.isEmpty()) {
			obj = this.filledObjects.get(0);
		}
		return obj;
	}

	@Override
	public String getValue() {
		return strValue;
	}

	@Override
	public String getPattern() {
		return pattern;
	}
	
	@Override
	public Formatter<T> getFormatter() {
		return formatter;
	}
	
	@Override
	public ChoiceRenderer<T> getChoiceRenderer() {
		return choiceRenderer;
	}
	
	@Override
	public boolean isVisible() {
		return this.formProperties.isVisible();
	}
	
	@Override
	public boolean isEnabled() {
		return this.formProperties.isEnabled();
	}
	
	@Override
	public boolean isReadonly() {
		return this.formProperties.isReadonly();
	}
	
	@Override
	public boolean isRequired() {
		return this.formProperties.isRequired();
	}
	
	@Override
	public String getHelp() {
		return this.formProperties.getHelp();
	}
	
	@Override
	public HeterogMap<String> getProperties() {
		return this.formProperties.getProperties();
	}
	
	@Override
	public FormProperties getFormProperties() {
		return this.formProperties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FormFieldImpl))
			return false;
		FormFieldImpl<T> other = (FormFieldImpl<T>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return new FormFieldStringBuilder().build(this);
	}
	
	private static <T> String valueAsString(T value, String pattern, Formatter<T> formatter, Locale locale, Formatters formatters) {
		if (value == null) return null;
		String str = null;
		if (formatter != null) {
			// formatter is specified by user
			str = formatter.makeString(value, pattern, locale);
		} else {
			str = formatters.makeString(value, pattern, locale);
		}
		return str;
	}
	
	private static String validateName(String name, String namePrefix) {
		if (name == null) throw new IllegalArgumentException("name cannot be null");
		if (namePrefix != null && !name.startsWith(namePrefix)) { 
			throw new IllegalArgumentException("name '" + name + "' does not start with given name prefix '" + namePrefix + "'");
		}
		return name;
	}

}
