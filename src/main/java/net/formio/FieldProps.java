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

import net.formio.ajax.JsEvent;
import net.formio.ajax.action.HandledJsEvent;
import net.formio.choice.ChoiceProvider;
import net.formio.choice.ChoiceRenderer;
import net.formio.choice.DefaultChoiceProvider;
import net.formio.choice.DefaultChoiceRenderer;
import net.formio.format.Formatter;
import net.formio.format.Formatters;
import net.formio.props.FormElementProperty;
import net.formio.props.FormFieldProperties;
import net.formio.props.FormFieldPropertiesImpl;
import net.formio.props.InlinePosition;
import net.formio.props.JsEventToUrl;
import net.formio.validation.Validator;
import net.formio.validation.validators.RequiredValidator;

/**
 * Specification of properties used to construct a {@link FormField}.
 * Builder of {@link FormField}.
 * 
 * @author Radek Beran
 */
public class FieldProps<T> implements Serializable {
	private static final long serialVersionUID = 2328756250255932689L;
	private FormMapping<?> parent;
	private String propertyName;
	private String type;
	private String inputType;
	private String pattern;
	private Formatter<T> formatter;
	private ChoiceProvider<T> choiceProvider;
	private ChoiceRenderer<T> choiceRenderer;
	private FormFieldProperties formProperties = new FormFieldPropertiesImpl(FormElementProperty.createDefaultProperties());
	List<T> filledObjects = new ArrayList<T>();
	String strValue;
	int order;
	List<Validator<T>> validators;
	
	FieldProps(String propertyName) {
		this(propertyName, Field.TEXT.getType());
	}
	
	FieldProps(String propertyName, String type) {
		// when the inputType is null, it will be taken from the found Field enum constant,
		// see the implementation of getInputType()
		this(propertyName, type, null);
	}
		
	FieldProps(String propertyName, String type, String inputType) {
		// package-default access so only Forms (and classes in current package) can create the builder
		if (propertyName == null || propertyName.isEmpty()) throw new IllegalArgumentException("propertyName must be specified");
		this.propertyName = propertyName;
		this.type = type;
		if (inputType == null && type != null) {
			Field fld = Field.findByType(type);
			if (fld != null) {
				inputType = fld.getInputType();
			}
		}
		this.inputType = inputType;
		this.validators = new ArrayList<Validator<T>>();
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
		initFromField(field).value(strValue).filledObjects(values);
		if (field.getChoiceRenderer() instanceof DefaultChoiceRenderer) {
			choiceRenderer(new DefaultChoiceRenderer<T>(locale));
		}
	}
		
	public FieldProps<T> type(String type) {
		this.type = type;
		return this;
	}
	
	public FieldProps<T> inputType(String inputType) {
		this.inputType = inputType;
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
	
	public FieldProps<T> choices(ChoiceProvider<T> choiceProvider) {
		this.choiceProvider = choiceProvider;
		return this;
	}
	
	public FieldProps<T> choices(List<? extends T> choices) {
		return choices(new DefaultChoiceProvider<T>(choices));
	}
	
	public FieldProps<T> choiceRenderer(ChoiceRenderer<T> choiceRenderer) {
		this.choiceRenderer = choiceRenderer;
		return this;
	}
	
	public FieldProps<T> validator(Validator<T> validator) {
		if (validator != null) {
			this.validators.add(validator);
		}
		return this;
	}
	
	public <U> FieldProps<T> property(FormElementProperty<U> fieldProperty, U value) {
		this.formProperties = new FormFieldPropertiesImpl(this.formProperties, fieldProperty, value);
		return this;
	}
	
	public FieldProps<T> visible(boolean visible) {
		return property(FormElementProperty.VISIBLE, Boolean.valueOf(visible));
	}
	
	public FieldProps<T> enabled(boolean enabled) {
		return property(FormElementProperty.ENABLED, Boolean.valueOf(enabled));
	}
	
	public FieldProps<T> readonly(boolean readonly) {
		return property(FormElementProperty.READ_ONLY, Boolean.valueOf(readonly));
	}

	public FieldProps<T> required(boolean required) {
		if (required) {
			Validator<T> validator = RequiredValidator.getInstance();
			if (!validators.contains(validator)) {
				validators.add(validator);
			}
		}
		return this;
	}
	
	public FieldProps<T> help(String help) {
		return property(FormElementProperty.HELP, help);
	}
	
	public FieldProps<T> chooseOptionDisplayed(boolean displayed) {
		return property(FormElementProperty.CHOOSE_OPTION_DISPLAYED, Boolean.valueOf(displayed));
	}
	
	public FieldProps<T> chooseOptionTitle(String title) {
		return property(FormElementProperty.CHOOSE_OPTION_TITLE, title);
	}
	
	public FieldProps<T> dataAjaxActions(HandledJsEvent action) {
		return dataAjaxActions(new HandledJsEvent[] { action });
	}
	
	public FieldProps<T> dataAjaxActions(JsEvent eventType, String url) {
		return dataAjaxActions(new HandledJsEvent[] { new JsEventToUrl(eventType, url) });
	}
	
	public FieldProps<T> dataAjaxActions(HandledJsEvent[] actions) {
		return property(FormElementProperty.DATA_AJAX_ACTIONS, actions);
	}
	
	public FieldProps<T> dataAjaxActions(List<? extends HandledJsEvent> actions) {
		return property(FormElementProperty.DATA_AJAX_ACTIONS, actions.toArray(new HandledJsEvent[0]));
	}
	
	public FieldProps<T> dataRelatedElement(String dataRelatedElement) {
		return property(FormElementProperty.DATA_RELATED_ELEMENT, dataRelatedElement);
	}
	
	public FieldProps<T> dataRelatedAncestor(String dataRelatedAncestor) {
		return property(FormElementProperty.DATA_RELATED_ANCESTOR, dataRelatedAncestor);
	}
	
	public FieldProps<T> dataConfirm(String dataConfirm) {
		return property(FormElementProperty.DATA_CONFIRM, dataConfirm);
	}
	
	public FieldProps<T> placeholder(String placeholderText) {
		return property(FormElementProperty.PLACEHOLDER, placeholderText);
	}
	
	public FieldProps<T> labelVisible(boolean visible) {
		return property(FormElementProperty.LABEL_VISIBLE, Boolean.valueOf(visible));
	}
	
	/**
	 * True if this form field is not attached to underlying property of form data object
	 * (it is not filled nor bound).
	 * @param detached
	 * @return
	 */
	public FieldProps<T> detached(boolean detached) {
		return property(FormElementProperty.DETACHED, Boolean.valueOf(detached));
	}
	
	/**
	 * Sets the inline position to non-{@code null} value if this is the inline field.
	 * @param inlinePos
	 * @return
	 */
	public FieldProps<T> inline(InlinePosition inlinePos) {
		return property(FormElementProperty.INLINE, inlinePos);
	}
	
	/**
	 * Sets width of input in count of columns.
	 * @param width
	 * @return
	 */
	public FieldProps<T> colInputWidth(Integer width) {
		return property(FormElementProperty.COL_INPUT_WIDTH, width);
	}
	
	/**
	 * Sets width of input in count of columns.
	 * @param width
	 * @return
	 */
	public FieldProps<T> colInputWidth(int width) {
		return property(FormElementProperty.COL_INPUT_WIDTH, Integer.valueOf(width));
	}
	
	// only for internal usage
	FieldProps<T> parent(FormMapping<?> parent) {
		this.parent = parent;
		return this;
	}
	
	// only for internal usage
	FieldProps<T> propertyName(String propertyName) {
		this.propertyName = propertyName;
		return this;
	}
	
	// only for internal usage
	FieldProps<T> order(int order) {
		this.order = order;
		return this;
	}
	
	// only for internal usage
	FieldProps<T> value(String value) {
		this.strValue = value;
		return this;
	}
	
	// only for internal usage
	FieldProps<T> filledObjects(List<T> filledObjects) {
		if (filledObjects == null) {
			filledObjects = new ArrayList<T>();
		}
		this.filledObjects = filledObjects;
		return this;
	}
	
	// for internal usage
	<U> FieldProps<T> properties(FormFieldProperties properties) {
		this.formProperties = properties;
		return this;
	}
	
	public FormMapping<?> getParent() {
		return this.parent;
	}

	/**
	 * Name of mapped propertyName of edited object.
	 * @return
	 */
	public String getPropertyName() {
		return this.propertyName;
	}

	/**
	 * Type of form field, for e.g.: text, checkbox, textarea, select-multiple, date-picker ..., 
	 * or {@code null} if not specified.
	 * @return
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Type of HTML input(s) that is used to render this form field.
	 * @return
	 */
	public String getInputType() {
		return inputType;
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
	public ChoiceProvider<T> getChoices() {
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
	 * Field form properties (flags like required, ... - see {@link FormElementProperty}).
	 * @return
	 */
	public FormFieldProperties getFormProperties() {
		return this.formProperties;
	}
	
	/**
	 * Returns ordinal index of this form element.
	 * @return
	 */
	public int getOrder() {
		return this.order;
	}
	
	public List<Validator<T>> getValidators() {
		return this.validators;
	}
	
	/**
	 * Constructs new immutable form field.
	 * @return
	 */
	public FormField<T> build() {
		return build(this.order);
	}
	
	/**
	 * Constructs new immutable form field.
	 * @return
	 */
	FormField<T> build(int order) {
		if (this.choiceRenderer == null && this.type != null && !this.type.isEmpty()) {
			Field formComponent = Field.findByType(this.type);
			if (formComponent != null && formComponent.isChoice()) {
				// This default locale will be replaced by the desired one when the form field is filled
				this.choiceRenderer = new DefaultChoiceRenderer<T>(Locale.getDefault());
			}
		}
		return new FormFieldImpl<T>(this, order);
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
		this.propertyName = field.getPropertyName();
		this.parent = field.getParent();
		this.type = field.getType();
		this.inputType = field.getInputType();
		this.pattern = field.getPattern();
		this.formatter = field.getFormatter();
		this.choiceProvider = field.getChoices();
		this.choiceRenderer = field.getChoiceRenderer();
		this.formProperties = field.getProperties();
		this.filledObjects = field.getFilledObjects();
		this.strValue = field.getValue();
		this.order = field.getOrder();
		this.validators = new ArrayList<Validator<T>>(field.getValidators());
		return this;
	}
}
