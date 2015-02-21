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
import net.formio.props.FormElementProperty;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.ValidationResult;

/**
 * Form field. Immutable.
 * @author Radek Beran
 */
public class FormFieldImpl<T> implements FormField<T> {
	// public because of introspection required by some template frameworks, constructors are not public
	
	private final FormMapping<?> parent;
	private final String propertyName;
	private final String type;
	/** Data filled in form field - for e.g. items from a codebook. */
	private final List<T> filledObjects;
	private final String pattern;
	private final Formatter<T> formatter;
	private final ChoiceProvider<T> choiceProvider;
	private final ChoiceRenderer<T> choiceRenderer;
	private final String strValue;
	private final FormFieldProperties formProperties;
	private final int order;

	/**
	 * Returns copy of field with given parent and required flag.
	 * @param src
	 * @param parent
	 * @param required null if required flag is not specified
	 */
	FormFieldImpl(FormField<T> src, FormMapping<?> parent, Boolean required) {
		this(src, parent, src.getOrder(), required);
	}
	
	/**
	 * Returns copy of given field with given parent and order.
	 * @param src
	 * @param parent
	 * @param order
	 * @return
	 */
	FormFieldImpl(FormField<T> src, FormMapping<?> parent, int order) {
		this(src, parent, order, (Boolean)null);
	}
	
	FormFieldImpl(FieldProps<T> fieldProps, int order) {
		this.parent = fieldProps.getParent();
		this.propertyName = fieldProps.getPropertyName();
		this.type = fieldProps.getType();
		this.pattern = fieldProps.getPattern();
		this.formatter = fieldProps.getFormatter();
		this.choiceProvider = fieldProps.getChoices();
		this.choiceRenderer = fieldProps.getChoiceRenderer();
		this.formProperties = new FormFieldPropertiesImpl(fieldProps.getFormProperties());
		this.filledObjects = new ArrayList<T>(fieldProps.filledObjects);
		this.strValue = fieldProps.strValue;
		this.order = order;
	}
	
	/**
	 * Returns copy of given field with given parent, order and required flag.
	 * @param src copied form field
	 * @param parent
	 * @param order
	 * @param required null if required flag is not specified
	 * @return
	 */
	private FormFieldImpl(FormField<T> src, FormMapping<?> parent, int order, Boolean required) {
		this(new FieldProps<T>(src)
			.parent(parent)
			.order(order)
			.properties(
				// Override required only in case required != null, so the required flag from field props is not
				// overriden by missing NotNull annotation...
				required != null ? 
					((FormFieldPropertiesImpl)src.getFormProperties()).withProperty(FormElementProperty.REQUIRED, required) :
					src.getFormProperties()
			)
		);
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
	public boolean isVisible() {
		return FormElementImpl.isVisible(this);
	}
	
	@Override
	public boolean isEnabled() {
		return FormElementImpl.isEnabled(this);
	}
	
	@Override
	public boolean isReadonly() {
		return FormElementImpl.isReadonly(this);
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
	public boolean isChooseOptionDisplayed() {
		return this.formProperties.isChooseOptionDisplayed();
	}
	
	@Override
	public String getChooseOptionTitle() {
		return this.formProperties.getChooseOptionTitle();
	}
	
	@Override
	public String getDataAjaxUrl() {
		return this.formProperties.getDataAjaxUrl();
	}
	
	@Override
	public String getDataRelatedElement() {
		return this.formProperties.getDataRelatedElement();
	}
	
	@Override
	public String getDataRelatedAncestor() {
		return this.formProperties.getDataRelatedAncestor();
	}
	
	@Override
	public String getDataConfirm() {
		return this.formProperties.getDataConfirm();
	}
	
	@Override
	public HeterogMap<String> getProperties() {
		return this.formProperties.getProperties();
	}
	
	@Override
	public FormFieldProperties getFormProperties() {
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
		return FormElementImpl.getValidationMessages(this);
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
