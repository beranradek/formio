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

import net.formio.common.heterog.HeterogCollections;
import net.formio.common.heterog.HeterogMap;
import net.formio.format.Formatter;
import net.formio.format.Formatters;
import net.formio.internal.FormUtils;
import net.formio.props.FieldProperty;

/**
 * Form field. Immutable.
 * @author Radek Beran
 */
class FormFieldImpl<T> implements FormField<T> {
	private final String name;
	private final String type;
	private final List<T> filledObjects;
	private final String pattern;
	private final Formatter<T> formatter;
	private final String strValue;
	private final HeterogMap<String> properties;
	
	static <T> FormFieldImpl<T> getInstance(String name, String type, String pattern, Formatter<T> formatter, HeterogMap<String> properties) {
		return new FormFieldImpl<T>(name, type, pattern, formatter, properties, Collections.<T>emptyList(), null);
	}
	
	static <T> FormFieldImpl<T> getFilledInstance(String name, String type, String pattern, Formatter<T> formatter, HeterogMap<String> properties, List<T> values, Locale locale, Formatters formatters, String preferedStringValue) {
		String strValue = null;
		if (preferedStringValue != null) {
			strValue = preferedStringValue; 
		} else if (values.size() > 0) {
			strValue = valueAsString(values.get(0), pattern, formatter, locale, formatters);
		}
		return new FormFieldImpl<T>(name, type, pattern, formatter, properties, values, strValue);
	}
	
	private FormFieldImpl(String name, String type, String pattern, Formatter<T> formatter, HeterogMap<String> properties, List<T> values, String strValue) {
		if (values == null) throw new IllegalArgumentException("values cannot be null, only empty");
		this.name = name;
		this.type = type;
		this.pattern = pattern;
		this.formatter = formatter;
		this.filledObjects = values;
		this.strValue = strValue;
		this.properties = properties;
	}

	/**
	 * Returns copy of given field with new name that has given prefix prepended.
	 * @param src
	 * @param namePrefix
	 * @return
	 */
	FormFieldImpl(FormField<T> src, String namePrefix) {
		if (namePrefix == null) throw new IllegalArgumentException("namePrefix cannot be null");
		this.name = namePrefix + Forms.PATH_SEP + src.getName();
		this.type = src.getType();
		this.filledObjects = new ArrayList<T>(src.getFilledObjects());
		this.pattern = src.getPattern();
		this.formatter = src.getFormatter();
		this.strValue = src.getValue();
		this.properties = copyProperties(src.getProperties());
	}

	/**
	 * Returns copy of field with given required flag.
	 * @param src
	 * @param required
	 */
	FormFieldImpl(FormField<T> src, boolean required) {
		this.name = src.getName();
		this.type = src.getType();
		this.filledObjects = new ArrayList<T>(src.getFilledObjects());
		this.pattern = src.getPattern();
		this.formatter = src.getFormatter();
		this.strValue = src.getValue();
		this.properties = copyProperties(src.getProperties(), required);
	}

	/**
	 * Returns copy of this field with new name that contains index after given name prefix.
	 * @param src
	 * @param index
	 * @param namePrefix
	 * @return
	 */
	FormFieldImpl(FormField<T> src, int index, String namePrefix) {
		if (namePrefix == null) throw new IllegalArgumentException("namePrefix cannot be null");
		if (!src.getName().startsWith(namePrefix))
			throw new IllegalStateException("FormField name '" + src.getName() + "' must start with prefix '" + namePrefix + ".'");
		this.name = namePrefix + "[" + index + "]" + src.getName().substring(namePrefix.length());
		this.type = src.getType();
		this.filledObjects = new ArrayList<T>(src.getFilledObjects());
		this.pattern = src.getPattern();
		this.formatter = src.getFormatter();
		this.strValue = src.getValue();
		this.properties = copyProperties(src.getProperties());
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
	public boolean isVisible() {
		return this.properties.getTyped(FieldProperty.VISIBLE).booleanValue();
	}
	
	@Override
	public boolean isEnabled() {
		return this.properties.getTyped(FieldProperty.ENABLED).booleanValue();
	}
	
	@Override
	public boolean isReadonly() {
		return this.properties.getTyped(FieldProperty.READ_ONLY).booleanValue();
	}
	
	@Override
	public boolean isRequired() {
		return this.properties.getTyped(FieldProperty.REQUIRED).booleanValue();
	}
	
	@Override
	public String getHelp() {
		return this.properties.getTyped(FieldProperty.HELP);
	}
	
	@Override
	public HeterogMap<String> getProperties() {
		return this.properties;
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
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		boolean firstParam = true;
		sb.append(" (");
		if (pattern != null && !pattern.isEmpty()) {
			if (!firstParam) sb.append(", ");
			sb.append("pattern=" + pattern);
			firstParam = false;
		}
		if (strValue != null && !strValue.isEmpty()) {
			if (!firstParam) sb.append(", ");
			int cnt = 0;
			if (filledObjects != null) {
				cnt = filledObjects.size();
			}
			sb.append("value=" + (strValue.length() > 17 ? strValue.substring(0, 17) + "..." : strValue) + " /count: " + cnt  + "/");
			firstParam = false;
		}
		sb.append(")");
		return sb.toString();
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
	
	private HeterogMap<String> copyProperties(HeterogMap<String> source) {
		HeterogMap<String> map = HeterogCollections.<String>newLinkedMap();
		map.putAllFromSource(source);
		return HeterogCollections.unmodifiableMap(map);
	}
	
	private HeterogMap<String> copyProperties(HeterogMap<String> source, boolean required) {
		HeterogMap<String> map = HeterogCollections.<String>newLinkedMap();
		map.putAllFromSource(source);
		map.putTyped(FieldProperty.REQUIRED, Boolean.valueOf(required));
		return HeterogCollections.unmodifiableMap(map);
	}

}
