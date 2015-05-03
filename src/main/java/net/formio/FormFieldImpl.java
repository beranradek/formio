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
import net.formio.format.Formatter;
import net.formio.internal.FormUtils;
import net.formio.props.FormFieldProperties;
import net.formio.props.FormFieldPropertiesImpl;
import net.formio.validation.ValidationResult;

/**
 * Form field. Immutable.
 * @author Radek Beran
 */
public class FormFieldImpl<T> extends AbstractFormElement<T> implements FormField<T> {
	// public because of introspection required by some template frameworks, constructors are not public
	
	private final String type;
	/** Data filled in form field - for e.g. items from a codebook. */
	private final List<T> filledObjects;
	private final String pattern;
	private final Formatter<T> formatter;
	private final ChoiceProvider<T> choiceProvider;
	private final ChoiceRenderer<T> choiceRenderer;
	private final String strValue;
	private final FormFieldProperties properties;
	private final int order;

	/**
	 * Returns copy of field with given parent.
	 * @param src
	 * @param parent
	 */
	FormFieldImpl(FormField<T> src, FormMapping<?> parent) {
		this(src, parent, src.getOrder());
	}
	
	/**
	 * Returns copy of given field with given parent and order.
	 * @param src
	 * @param parent
	 * @param order
	 * @return
	 */
	FormFieldImpl(FormField<T> src, FormMapping<?> parent, int order) {
		this(new FieldProps<T>(src)
			.parent(parent)
			.order(order)
			.properties(src.getProperties())
		);
	}
	
	FormFieldImpl(FieldProps<T> fieldProps, int order) {
		super(fieldProps.getParent(), fieldProps.getPropertyName(), fieldProps.getValidators());
		this.type = fieldProps.getType() != null ? fieldProps.getType() : Field.TEXT.getType();
		this.pattern = fieldProps.getPattern();
		this.formatter = fieldProps.getFormatter();
		this.choiceProvider = fieldProps.getChoices();
		this.choiceRenderer = fieldProps.getChoiceRenderer();
		this.properties = new FormFieldPropertiesImpl(fieldProps.getFormProperties());
		this.filledObjects = new ArrayList<T>(fieldProps.filledObjects);
		this.strValue = fieldProps.strValue;
		this.order = order;
	}
	
	private FormFieldImpl(FieldProps<T> fieldProps) {
		this(fieldProps, fieldProps.getOrder());
	}
	
	@Override
	public FormMapping<?> getParent() {
		return this.parent;
	}

	@Override
	public String getName() {
		String name = null;
		if (getParent() != null) {
			name = getParent().getName() + Forms.PATH_SEP + propertyName;
		} else {
			name = propertyName;
		}
		if (name == null || name.isEmpty()) {
			throw new IllegalStateException("Name must be filled");
		}
		return name;
	}
	
	@Override
	public String getPropertyName() {
		return propertyName;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public String getLabelKey() {
		return FormUtils.labelKeyForName(getName());
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
	public boolean isFilledWithTrue() {
		Boolean checked = Boolean.FALSE;
		if (getValue() != null && !getValue().isEmpty()) {
			String lc = getValue().toLowerCase();
			checked = getParent().getConfig().getFormatters().parseFromString(
				lc, Boolean.class, (String)null, getParent().getConfig().getLocale());
		}
		return checked != null && checked.booleanValue();
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
	public ChoiceProvider<T> getChoices() {
		return choiceProvider;
	}
	
	@Override
	public ChoiceRenderer<T> getChoiceRenderer() {
		return choiceRenderer;
	}
	
	@Override
	public FormFieldProperties getProperties() {
		return this.properties;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
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
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return new FormFieldStringBuilder().build(this);
	}

}
