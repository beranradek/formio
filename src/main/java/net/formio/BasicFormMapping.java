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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.formio.binding.BoundValuesInfo;
import net.formio.binding.FilledData;
import net.formio.binding.InstanceHoldingInstantiator;
import net.formio.binding.Instantiator;
import net.formio.binding.ParseError;
import net.formio.choice.ChoiceProvider;
import net.formio.common.heterog.HeterogMap;
import net.formio.data.RequestContext;
import net.formio.format.Formatter;
import net.formio.internal.FormUtils;
import net.formio.servlet.ServletRequestParams;
import net.formio.upload.MaxSizeExceededError;
import net.formio.upload.RequestProcessingError;
import net.formio.upload.UploadedFile;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.ValidationResult;

/**
 * Default implementation of {@link FormMapping}. Immutable when not filled.
 * After the filling, new instance of mapping is created and its immutability 
 * depends on the character of filled data.
 * 
 * @author Radek Beran
 */
public class BasicFormMapping<T> implements FormMapping<T> {
	// public because of introspection required by some template frameworks, constructors are not public

	final FormMapping<?> parent;
	final String propertyName;
	final Class<T> dataClass;
	final Instantiator<T> instantiator;
	final Config config;
	final T filledObject;
	
	/** Mapping simple property names to fields. */
	final Map<String, FormField<?>> fields;
	/** Mapping simple property names to nested mappings. Property name is a part of full path of nested mapping. */
	final Map<String, FormMapping<?>> nested;
	final ValidationResult validationResult;
	final FormProperties formProperties;
	final boolean secured;
	final int order;
	final Integer index;
	
	/**
	 * Constructs a mapping from the given builder.
	 * @param builder
	 * @param simpleCopy true if simple copy of builder's data should be constructed, otherwise propagation
	 * of parent mapping into fields and nested mappings is processed
	 */
	BasicFormMapping(BasicFormMappingBuilder<T> builder, boolean simpleCopy) {
		this.parent = builder.parent;
		this.propertyName = builder.propertyName;
		this.config = builder.config;
		this.dataClass = assertNotNullArg(builder.dataClass, "data class must be filled before configuring fields");
		this.instantiator = builder.instantiator;
		this.filledObject = builder.filledObject;
		this.secured = builder.secured;
		this.validationResult = builder.validationResult;
		this.formProperties = new FormPropertiesImpl(builder.properties);
		this.order = builder.order;
		this.index = builder.index;
		this.fields = simpleCopy ? Collections.unmodifiableMap(builder.fields) : 
			Clones.fieldsWithParent(this, builder.fields, getConfig(), builder.dataClass);
		this.nested = simpleCopy ? Collections.unmodifiableMap(builder.nested) : 
			Clones.mappingsWithParent(this, builder.nested, builder.dataClass, getConfig());
	}
	
	/**
	 * Returns copy with given order (called when appending this nested mapping to outer builder).
	 * @param src
	 * @param order
	 */
	BasicFormMapping(BasicFormMapping<T> src, int order) {
		this(new BasicFormMappingBuilder<T>(src, 
			src.fields,
			src.nested)
			.order(order), 
			true); // true = simple copy of builder's data
	}
	
	/**
	 * Returns copy with given config.
	 * @param src
	 * @param config
	 * @param required
	 */
	BasicFormMapping(BasicFormMapping<T> src, FormMapping<?> parent, boolean required) {
		this(new BasicFormMappingBuilder<T>(src, 
			src.fields, 
			src.nested)
			.parent(parent)
			.required(required), 
			false); // false = parent will be propagated to nested elements
	}
	
	@Override
	public FormMapping<?> getParent() {
		return this.parent;
	}

	@Override
	public String getName() {
		String name = null;
		if (getParent() != null) {
			if (index != null) {
				name = getParent().getName() + Forms.PATH_SEP + propertyName + "[" + index + "]";
			} else {
				name = getParent().getName() + Forms.PATH_SEP + propertyName;
			}
		} else {
			if (index != null) {
				name = propertyName + "[" + index + "]";
			} else {
				name = propertyName;
			}
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
	public Class<T> getDataClass() {
		return dataClass;
	}
	
	@Override
	public Instantiator<T> getInstantiator() {
		return instantiator;
	}

	@Override
	public ValidationResult getValidationResult() {
		return validationResult;
	}
	
	@Override
	public List<ConstraintViolationMessage> getValidationMessages() {
		return FormElementImpl.getValidationMessages(this);
	}
	
	@Override
	public List<FormElement> getElements() {
		List<FormElement> elems = new ArrayList<FormElement>();
		elems.addAll(this.nested.values());
		elems.addAll(this.fields.values());
		Collections.sort(elems, new FormElementOrderAscComparator());
		return Collections.unmodifiableList(elems);
	}
	
	@Override
	public Map<String, FormField<?>> getFields() {
		return fields;
	}
	
	@Override
	public <U> FormField<U> getField(Class<U> dataClass, String propertyName) {
		FormField<?> field = getFields().get(propertyName);
		if (field != null) {
			Object filledObject = field.getFilledObject();
			if (filledObject != null && !dataClass.isAssignableFrom(filledObject.getClass())) {
				throw new IllegalStateException("Type of value in field '" + propertyName + 
					"' is not compatible with requested type " + dataClass.getName());
			}
		}
		return (FormField<U>)field;
	}
	
	@Override
	public Map<String, FormMapping<?>> getNested() {
		return Collections.unmodifiableMap(nested);
	}
	
	@Override
	public <U> FormMapping<U> getMapping(Class<U> dataClass, String propertyName) {
		Map<String, FormMapping<?>> nestedMappings = getNested();
		FormMapping<?> mapping = nestedMappings.get(propertyName);
		if (mapping != null) {
			if (!dataClass.isAssignableFrom(mapping.getDataClass())) {
				throw new IllegalStateException("Type of object in nested mapping '" + propertyName + 
					"' is not compatible with requested type " + dataClass.getName());
			}
		}
		return (FormMapping<U>)mapping;
	}
	
	@Override
	public List<FormMapping<T>> getList() {
		return Collections.<FormMapping<T>>emptyList();
	}
	
	@Override
	public BasicFormMapping<T> fill(FormData<T> editedObj, Locale locale, RequestContext ctx) {
		return fillInternal(editedObj, locale, ctx).build(getConfig());
	}
	
	@Override
	public BasicFormMapping<T> fill(FormData<T> editedObj, Locale locale) {
		return fill(editedObj, locale, (RequestContext)null);
	}
	
	@Override
	public BasicFormMapping<T> fill(FormData<T> editedObj, RequestContext ctx) {
		return fill(editedObj, getConfigLocale(), ctx);
	}
	
	@Override
	public BasicFormMapping<T> fill(FormData<T> editedObj) {
		return fill(editedObj, getConfigLocale());
	}
	
	@Override
	public BasicFormMapping<T> fillAndValidate(FormData<T> formData, Locale locale, RequestContext ctx, Class<?> ... validationGroups) {
		BasicFormMapping<T> mapping = fill(formData, locale, ctx);
		FormData<T> validatedFormData = new FormData<T>(formData.getData(), mapping.validate(locale, validationGroups));
		return fill(validatedFormData, locale, ctx);
	}
	
	@Override
	public FormMapping<T> fillAndValidate(FormData<T> formData, Locale locale, Class<?>... validationGroups) {
		return fillAndValidate(formData, locale, (RequestContext)null, validationGroups);
	}
	
	@Override
	public FormData<T> bind(RequestParams paramsProvider, Locale locale, Class<?>... validationGroups) {
		return bind(paramsProvider, locale, (RequestContext)null, validationGroups);
	}
	
	@Override
	public FormData<T> bind(RequestParams paramsProvider, Locale locale, RequestContext ctx, Class<?>... validationGroups) {
		return bind(paramsProvider, locale, (T)null, ctx, validationGroups);
	}
	
	@Override
	public FormData<T> bind(RequestParams paramsProvider, Class<?>... validationGroups) {
		return bind(paramsProvider, (RequestContext)null, validationGroups);
	}
	
	@Override
	public FormData<T> bind(RequestParams paramsProvider, RequestContext ctx, Class<?>... validationGroups) {
		return bind(paramsProvider, getConfigLocale(), ctx, validationGroups);
	}
	
	@Override
	public FormData<T> bind(RequestParams paramsProvider, T instance, Class<?>... validationGroups) {
		return bind(paramsProvider, instance, (RequestContext)null, validationGroups);
	}
	
	@Override
	public FormData<T> bind(RequestParams paramsProvider, T instance, RequestContext ctx, Class<?>... validationGroups) {
		return bind(paramsProvider, getConfigLocale(), instance, ctx, validationGroups);
	}
	
	@Override
	public FormData<T> bind(RequestParams paramsProvider, Locale locale, T instance, Class<?>... validationGroups) {
		return bind(paramsProvider, locale, instance, (RequestContext)null, validationGroups);
	}

	@Override
	public FormData<T> bind(final RequestParams paramsProvider, final Locale locale, final T instance, final RequestContext context, final Class<?>... validationGroups) {
		if (paramsProvider == null) throw new IllegalArgumentException("paramsProvider cannot be null");
		RequestContext ctx = context;
		if (ctx == null && paramsProvider instanceof ServletRequestParams) {
			// fallback to ctx retrieved from ServletRequestParams, 
			// so the user need not to specify ctx explicitly for bind method
			ctx = ((ServletRequestParams)paramsProvider).getRequestContext();
		}
		
		final RequestProcessingError error = paramsProvider.getRequestError();
		Map<String, BoundValuesInfo> values = prepareValuesToBindForFields(paramsProvider, locale);
		
		// binding (and validating) data from paramsProvider to objects for nested mappings
		// and adding it to available values to bind
		Map<String, FormData<?>> nestedFormData = loadDataForMappings(nested, paramsProvider, locale, instance, ctx, validationGroups);
		for (Map.Entry<String, FormData<?>> e : nestedFormData.entrySet()) {
			values.put(e.getKey(), BoundValuesInfo.getInstance(
				new Object[] { e.getValue().getData() }, 
				(String)null, 
				(Formatter<Object>)null,
				locale));
		}
		
		if (!(error instanceof MaxSizeExceededError) && this.secured) {
			// Must be executed after processing of nested mappings
			AuthTokens.verifyAuthToken(ctx, getConfig().getTokenAuthorizer(), getRootMappingPath(), paramsProvider, isRootMapping());
		}
		
		// binding data from "values" to resulting object for this mapping
		Instantiator<T> instantiator = this.instantiator;
		if (instance != null) {
			// use instance already prepared by client which the client wish to fill
			instantiator = new InstanceHoldingInstantiator<T>(instance);
		}
		final FilledData<T> filledData = getConfig().getBinder().bindToNewInstance(this.dataClass, instantiator, values);
		
		// validation of resulting object for this mapping
		ValidationResult validationRes = validateInternal(
			filledData.getData(),
			error, 
			FormUtils.flatten(filledData.getPropertyBindErrors().values()), 
			locale, 
			validationGroups); 
		
		Collection<ValidationResult> validationResults = new ArrayList<ValidationResult>();
		validationResults.add(validationRes);
		for (FormData<?> fd : nestedFormData.values()) {
			validationResults.add(fd.getValidationResult());
		}
		
		return new FormData<T>(filledData.getData(), Clones.mergedValidationResults(validationResults));
	}

	@Override
	public String getLabelKey() {
		return FormUtils.labelKeyForName(getName());
	}
	
	/**
	 * Object filled in this mapping.
	 * @return
	 */
	@Override
	public T getFilledObject() {
		return this.filledObject;
	}
	
	@Override
	public Config getConfig() {
		Config cfg = this.config;
		if (cfg == null && this.parent != null) {
			cfg = this.parent.getConfig();
		}
		if (cfg == null) {
			// fallback to default config
			cfg = Forms.defaultConfig(this.dataClass);
		}
		return cfg;
	}
	
	@Override
	public String toString() {
		return toString("");
	}
	
	@Override
	public BasicFormMapping<T> withOrder(int order) {
		return new BasicFormMapping<T>(this, order);
	}
	
	@Override
	public BasicFormMapping<T> withParent(FormMapping<?> parent, boolean required) {
		return new BasicFormMapping<T>(this, parent, required);
	}
	
	@Override
	public String toString(String indent) {
		return new MappingStringBuilder<T>(
			getDataClass(), 
			getName(),
			order,
			fields,
			nested, 
			getList()).build(indent);
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
	public Integer getIndex() {
		return this.index;
	}
	
	@Override
	public boolean isRootMapping() {
		return this.parent == null;
	}
	
	ValidationResult validate(Locale locale, Class<?> ... validationGroups) {
		Collection<ValidationResult> validationResults = new ArrayList<ValidationResult>();
		if (getFilledObject() != null) {
			validationResults.add(validateInternal(getFilledObject(), (RequestProcessingError)null, new ArrayList<ParseError>(), locale, validationGroups));
		}
		for (FormMapping<?> mapping : nested.values()) {
			validationResults.add(((BasicFormMapping<?>)mapping).validate(locale, validationGroups));
		}
		return Clones.mergedValidationResults(validationResults);
	}
	
	ValidationResult validateInternal(T object, RequestProcessingError error, List<ParseError> parseErrors, Locale locale, Class<?> ... validationGroups) {
		List<RequestProcessingError> requestErrors = new ArrayList<RequestProcessingError>();
		if (error != null) {
			requestErrors.add(error);
		}
		return getConfig().getBeanValidator().validate(
			object,
			getName(), 
			requestErrors, 
			parseErrors,
			locale,
			validationGroups);
	}
	
	Map<String, FormData<?>> loadDataForMappings(
		Map<String, FormMapping<?>> mappings, 
		RequestParams paramsProvider,
		Locale locale,
		T instance,
		RequestContext ctx,
		Class<?> ... validationGroups) {
		final Map<String, FormData<?>> dataMap = new LinkedHashMap<String, FormData<?>>();
		// Transformation from ? to Object (to satisfy generics)
		final Map<String, FormMapping<?>> inputMappings = new LinkedHashMap<String, FormMapping<?>>();
		for (Map.Entry<String, FormMapping<?>> e : mappings.entrySet()) {
			inputMappings.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<String, FormMapping<?>> e : inputMappings.entrySet()) {
			Object nestedInstance = null;
			if (instance != null) {
				nestedInstance = nestedData(e.getKey(), instance); 
			}
			FormMapping<Object> mapping = (FormMapping<Object>)e.getValue();
			FormData<Object> formData = mapping.bind(paramsProvider, locale, nestedInstance, ctx, validationGroups);
			dataMap.put(e.getKey(), formData);
		}
		// Transformation from Object to ? (to satisfy generics)
		final Map<String, FormData<?>> outputData = new LinkedHashMap<String, FormData<?>>();
		for (Map.Entry<String, FormData<?>> e : dataMap.entrySet()) {
			outputData.put(e.getKey(), e.getValue());
		}
		return outputData;
	}
	
	BasicFormMappingBuilder<T> fillInternal(FormData<T> editedObj, Locale locale, RequestContext ctx) {
		Map<String, FormMapping<?>> filledNestedMappings = fillNestedMappings(editedObj, locale, ctx);
		
		// Preparing values for this mapping
		Map<String, Object> propValues = gatherPropertyValues(editedObj.getData(), FormUtils.getPropertiesFromFields(fields), ctx);
		
		// Fill the definitions of fields of this mapping with prepared values
		Map<String, FormField<?>> filledFields = fillFields(
			propValues, 
			editedObj.getValidationResult().getFieldMessages(),
			-1, 
			locale);

		// Returning copy of this form that is filled with form data
		BasicFormMappingBuilder<T> builder = new BasicFormMappingBuilder<T>(this, 
			filledFields, 
			Collections.unmodifiableMap(filledNestedMappings))
			.filledObject(editedObj.getData())
			.validationResult(editedObj.getValidationResult());
		return builder;
	}
	
	/**
	 * Gather values of object's formProperties.
	 * @param object
	 * @param allowedProperties set of allowed formProperties, does not influence order of returned entries
	 * @param ctx request context
	 * @return
	 */
	Map<String, Object> gatherPropertyValues(T object, Set<String> allowedProperties, RequestContext ctx) {
		Map<String, Object> beanValues = getConfig().getBeanExtractor().extractBean(object, allowedProperties);
		Map<String, Object> propValues = new LinkedHashMap<String, Object>(beanValues);
		if (isRootMapping() && secured) {
			propValues.put(Forms.AUTH_TOKEN_FIELD_NAME, 
				AuthTokens.generateAuthToken(ctx, getConfig().getTokenAuthorizer(), getRootMappingPath()));
		}
		return Collections.unmodifiableMap(propValues);
	}
	
	String getRootMappingPath() {
		FormMapping<?> rootMapping = this;
		while (rootMapping.getParent() != null) {
			rootMapping = rootMapping.getParent();
		}
		return rootMapping.getName();
	}

	Map<String, FormField<?>> fillFields(
		Map<String, Object> propValues, 
		Map<String, List<ConstraintViolationMessage>> fieldMsgs, 
		int indexInList, 
		Locale locale) {
		Map<String, FormField<?>> filledFields = new LinkedHashMap<String, FormField<?>>();
		// For each field from form definition, let's fill this field with value -> filled form field
		for (Map.Entry<String, FormField<?>> fieldDefEntry : this.fields.entrySet()) {
			final String propertyName = fieldDefEntry.getKey();
			if (indexInList >= 0 && Forms.AUTH_TOKEN_FIELD_NAME.equals(propertyName)) {
				if (isRootMapping() && this.secured) {
					throw new UnsupportedOperationException("Verification of authorization token is not supported "
						+ "in root list mapping. Please create SINGLE root mapping with nested list mapping.");
				}
			}
			
			final FormField<?> field = fieldDefEntry.getValue();
			Object value = propValues.get(propertyName);
			List<ConstraintViolationMessage> fieldMessages = fieldMsgs.get(field.getName());
			String preferedStringValue = null;
			if (fieldMessages != null && !fieldMessages.isEmpty()) {
				preferedStringValue = getOriginalStringValueFromParseError(fieldMessages);
			}
			final FormField<?> filledField = createFilledFormField((FormField<Object>)field, value, locale, preferedStringValue);
			filledFields.put(propertyName, filledField);
		}
		filledFields = Collections.unmodifiableMap(filledFields);
		return filledFields;
	}

	Map<String, FormMapping<?>> fillNestedMappings(FormData<T> editedObj, Locale locale, RequestContext ctx) {
		Map<String, FormMapping<?>> newNestedMappings = new LinkedHashMap<String, FormMapping<?>>();
		// For each definition of nested mapping, fill this mapping with edited data -> filled mapping
		for (Map.Entry<String, FormMapping<?>> e : this.nested.entrySet()) {
			// nested data - nested object or list of nested objects in case of mapping to list
			Object data = nestedData(e.getKey(), editedObj.getData());
			// the outer report is propagated to nested
			FormData<Object> formData = new FormData<Object>(data, editedObj.getValidationResult());
			FormMapping<Object> mapping = (FormMapping<Object>)e.getValue();
			newNestedMappings.put(e.getKey(), mapping.fill(formData, locale, ctx));
		}
		return newNestedMappings;
	}
	
	/**
	 * Returns nested object extracted as value of given property of given data.
	 * @param propName
	 * @param data
	 * @return
	 */
	<U> U nestedData(String propName, T data) {
		Map<String, Object> props = getConfig().getBeanExtractor().extractBean(data, Collections.singleton(propName));
		return (U)props.get(propName); // can be null if nested object is not required
	}

	/**
	 * Converts parameters from request (RequestParams) using field definitions and given locale
	 * to descriptions of values for individual properties, ready to bind to form data object
	 * via binder.
	 * @param paramsProvider
	 * @param locale
	 * @return
	 */
	private Map<String, BoundValuesInfo> prepareValuesToBindForFields(RequestParams paramsProvider, Locale locale) {
		Map<String, BoundValuesInfo> values = new LinkedHashMap<String, BoundValuesInfo>();
		// Get values for each defined field
		for (Map.Entry<String, FormField<?>> e : fields.entrySet()) {
			FormField<?> field = e.getValue();
			String formPrefixedName = field.getName(); // already prefixed with form name
			Object[] paramValues = null;
			UploadedFile[] files = paramsProvider.getUploadedFiles(formPrefixedName);
			if (files == null || files.length == 0) { 
				files = paramsProvider.getUploadedFiles(formPrefixedName + "[]");
			}
			if (files != null && files.length > 0) {
				// non-empty files array returned
				paramValues = files;
			} else {
				String[] strValues = paramsProvider.getParamValues(formPrefixedName);
				if (strValues == null) strValues = paramsProvider.getParamValues(formPrefixedName + "[]");
				if (getConfig().isInputTrimmed()) {
					strValues = FormUtils.trimValues(strValues);
				}
				paramValues = strValues;
				if (strValues != null && field.getChoices() != null && field.getChoiceRenderer() != null) {
					// There is a codebook with choices to select from
					paramValues = ChoiceItems.convertParamsToChoiceItems(field, strValues);
				}
			}
			String propertyName = e.getKey();
			values.put(propertyName, BoundValuesInfo.getInstance(
			  paramValues, field.getPattern(), field.getFormatter(), locale));
		}
		return values;
	}

	private <U> FormField<U> createFilledFormField(final FormField<U> field, U value, Locale locale, String preferedStringValue) {
		ChoiceProvider<U> choiceProvider = field.getChoices();
		if (choiceProvider == null && field.getType() != null && !field.getType().isEmpty()) {
			Field formComponent = Field.findByType(field.getType());
			if (formComponent != null && formComponent.isChoice()) {
				// TODO: choice provider can be initialized here to some default but class of value must be
				// propagated here
			}
		}
		return new FieldProps<U>(field, 
			FormUtils.<U>convertObjectToList(value), 
			locale, 
			getConfig().getFormatters(),
			preferedStringValue).choices(choiceProvider).build();
	}
	
	private Locale getConfigLocale() {
		return getConfig().getLocale();
	}
	
	private String getOriginalStringValueFromParseError(List<ConstraintViolationMessage> fieldMessages) {
		String value = null;
		if (fieldMessages != null) {
			for (ConstraintViolationMessage msg : fieldMessages) {
				if (value == null && msg.getMsgArgs() != null) {
					value = (String)msg.getMsgArgs().get(ParseError.MSG_ARG_VALUE_AS_STRING);
				}
			}
		}
		return value;
	}
	
	private static <U> U assertNotNullArg(U arg, String message) {
		if (arg == null) throw new IllegalArgumentException(message);
		return arg;
	}

}
