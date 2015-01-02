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
import java.util.List;

import net.formio.choice.ChoiceProvider;
import net.formio.choice.ChoiceRenderer;
import net.formio.common.heterog.HeterogMap;
import net.formio.format.Formatter;
import net.formio.internal.FormUtils;
import net.formio.props.FieldProperty;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.ValidationResult;

/**
 * Form field. Immutable.
 * @author Radek Beran
 */
public class FormFieldImpl<T> implements FormField<T> {
	// public because of introspection required by some template frameworks, constructors are not public
	
	private final FormMapping<?> parent;
	private final String name;
	private final String type;
	/** Data filled in form field - for e.g. items from a codebook. */
	private final List<T> filledObjects;
	private final String pattern;
	private final Formatter<T> formatter;
	private final ChoiceProvider<T> choiceProvider;
	private final ChoiceRenderer<T> choiceRenderer;
	private final String strValue;
	private final FormProperties formProperties;
	private final int order;

	/**
	 * Returns copy of field with given required flag.
	 * @param src
	 * @param required null if required flag is not specified
	 */
	FormFieldImpl(FormField<T> src, FormMapping<?> parent, Boolean required) {
		this(src, parent, src.getName(), (String)null, src.getOrder(), required);
	}

	/**
	 * Returns copy of this field with new name that contains index after given name prefix.
	 * @param src
	 * @param parent
	 * @param index
	 * @param namePrefixWithoutIndex
	 * @return
	 */
	FormFieldImpl(FormField<T> src, FormMapping<?> parent, int index, String namePrefixWithoutIndex) {
		this(src, parent, namePrefixWithoutIndex + "[" + index + "]" + src.getName().substring(namePrefixWithoutIndex.length()), 
			namePrefixWithoutIndex, src.getOrder(), (Boolean)null);
	}
	
	/**
	 * Returns copy of given field with new name that has given prefix 
	 * prepended to the previous name of source field.
	 * @param src
	 * @param parent
	 * @param namePrefixToPrepend
	 * @param order
	 * @return
	 */
	FormFieldImpl(FormField<T> src, FormMapping<?> parent, String namePrefixToPrepend, int order) {
		this(src, parent, namePrefixToPrepend + Forms.PATH_SEP + src.getName(), namePrefixToPrepend, order, (Boolean)null);
	}
	
	FormFieldImpl(FieldProps<T> fieldProps, String parentPath, int order) {
		this.parent = fieldProps.getParent();
		fieldProps.checkConsistentNames();
		String name = null;
		if (parentPath != null && !parentPath.isEmpty() && fieldProps.getPropertyName() != null && !fieldProps.getPropertyName().isEmpty()) {
			name = formPrefixedName(parentPath, fieldProps.getPropertyName());
			validateName(name, parentPath);
		} else if (fieldProps.getName() != null && !fieldProps.getName().isEmpty()) {
			name = fieldProps.getName();
		} else if (fieldProps.getPropertyName() != null && !fieldProps.getPropertyName().isEmpty()) {
			// parentPath is null or empty
			// Can occur when the form field is defined by user - path is not complete before building the whole form mapping
			name = fieldProps.getPropertyName();
		} else {
			throw new IllegalArgumentException("Cannot determine form field name!");
		}
		this.name = name;
		this.type = fieldProps.getType();
		this.pattern = fieldProps.getPattern();
		this.formatter = fieldProps.getFormatter();
		this.choiceProvider = fieldProps.getChoiceProvider();
		this.choiceRenderer = fieldProps.getChoiceRenderer();
		this.formProperties = new FormPropertiesImpl(fieldProps.getFormProperties());
		this.filledObjects = new ArrayList<T>(fieldProps.filledObjects);
		this.strValue = fieldProps.strValue;
		this.order = order;
	}
	
	/**
	 * Returns copy of given field with given name.
	 * @param src copied form field
	 * @param parent
	 * @param name full name of form field
	 * @param namePrefix if not null, name must start with this prefix
	 * @param order
	 * @param required null if required flag is not specified
	 * @return
	 */
	private FormFieldImpl(FormField<T> src, FormMapping<?> parent, String name, String namePrefix, int order, Boolean required) {
		this(new FieldProps<T>(src)
			.parent(parent)
			.name(validateName(name, namePrefix))
			.order(order)
			.properties(
				// Override required only in case required != null, so the required flag from field props is not
				// overriden by missing NotNull annotation...
				required != null ? 
					((FormPropertiesImpl)src.getFormProperties()).withProperty(FieldProperty.REQUIRED, required) :
					src.getFormProperties()
			)
		);
	}
	
	private FormFieldImpl(FieldProps<T> fieldProps) {
		this(fieldProps, null, fieldProps.getOrder());
	}
	
	@Override
	public FormMapping<?> getParent() {
		return this.parent;
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
	public ChoiceProvider<T> getChoiceProvider() {
		return choiceProvider;
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
	public int getOrder() {
		return this.order;
	}
	
	@Override
	public ValidationResult getValidationResult() {
		ValidationResult result = null;
		if (getParent() != null) {
			result = getParent().getValidationResult();
		}
		return result;
	}
	
	@Override
	public List<ConstraintViolationMessage> getValidationMessages() {
		return FormUtils.getValidationMessages(this);
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
		FormFieldImpl<?> other = (FormFieldImpl<?>) obj;
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
	
	private static String validateName(final String name, final String namePrefix) {
		if (name == null) throw new IllegalArgumentException("name cannot be null");
		if (namePrefix != null && !name.startsWith(namePrefix)) { 
			throw new IllegalArgumentException("name '" + name + "' does not start with given name prefix '" + namePrefix + "'");
		}
		return name;
	}
	
	private static String formPrefixedName(String parentPath, String propertyName) {
		if (propertyName == null) return null;
		return parentPath + Forms.PATH_SEP + propertyName;
	}

}
