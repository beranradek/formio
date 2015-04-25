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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.formio.ajax.action.HandledJsEvent;
import net.formio.binding.BeanExtractor;
import net.formio.binding.BindingReflectionUtils;
import net.formio.binding.ConstructionDescription;
import net.formio.binding.ConstructorInstantiator;
import net.formio.binding.Instantiator;
import net.formio.binding.PrimitiveType;
import net.formio.binding.PropertyMethodRegex;
import net.formio.binding.collection.CollectionSpec;
import net.formio.binding.collection.ItemsOrder;
import net.formio.common.heterog.HeterogCollections;
import net.formio.common.heterog.HeterogMap;
import net.formio.props.FormElementProperty;
import net.formio.upload.UploadedFile;
import net.formio.validation.ValidationResult;
import net.formio.validation.Validator;
import net.formio.validation.validators.RequiredValidator;

/**
 * Basic builder of {@link FormMapping}.
 * @author Radek Beran
 */
public class BasicFormMappingBuilder<T> {

	FormMapping<?> parent;
	String propertyName;
	Class<T> dataClass;
	Instantiator<T> instantiator;
	/** Mapping simple property names to fields. */
	Map<String, FormField<?>> fields = new LinkedHashMap<String, FormField<?>>();
	/** Mapping simple property names to nested mappings. Property name is a part of full path of nested mapping. */
	Map<String, FormMapping<?>> nested = new LinkedHashMap<String, FormMapping<?>>();
	List<FormMapping<T>> listOfMappings = new ArrayList<FormMapping<T>>();
	Config config;
	List<Validator<T>> validators;
	ValidationResult validationResult;
	MappingType mappingType;
	T filledObject;
	boolean automatic;
	boolean secured;
	HeterogMap<String> properties;
	int order;
	private int nextNestedElementOrder;
	Integer index;

	/**
	 * Should be constructed only via {@link Forms} entry point of API.
	 */
	BasicFormMappingBuilder(Class<T> dataClass, String propertyName, Instantiator<T> inst, boolean automatic, MappingType mappingType) {
		if (dataClass == null) throw new IllegalArgumentException("dataClass must be filled");
		if (propertyName == null || propertyName.isEmpty()) throw new IllegalArgumentException("propertyName must be filled");
		if (mappingType == null) throw new IllegalArgumentException("mappingType must be filled");
		this.dataClass = dataClass;
		this.propertyName = propertyName;
		Instantiator<T> instantiator = inst;
		if (instantiator == null) {
			// using some public constructor as default instantiation strategy
			instantiator = new ConstructorInstantiator<T>(dataClass);
		}
		this.instantiator = instantiator;
		this.mappingType = mappingType;
		this.automatic = automatic;
		this.properties = FormElementProperty.createDefaultFieldProperties();
		this.validators = new ArrayList<Validator<T>>();
	}
	
	BasicFormMappingBuilder(Class<T> objectClass, String propertyName, Instantiator<T> inst, boolean automatic) {
		this(objectClass, propertyName, inst, automatic, MappingType.SINGLE);
	}
	
	BasicFormMappingBuilder(BasicFormMapping<T> src, Map<String, FormField<?>> fields, Map<String, FormMapping<?>> nested) {
		// src already contains composed/created fields -> automatic = false for this case
		this(src.dataClass, src.propertyName, src.instantiator, false, 
			(src instanceof BasicListFormMapping) ? MappingType.LIST : MappingType.SINGLE);
		this.parent = src.parent;
		this.config = src.config;
		this.filledObject = src.filledObject;
		this.fields = fields;
		this.nested = Collections.unmodifiableMap(nested);
		this.secured = src.secured;
		this.validationResult = src.validationResult;
		final HeterogMap<String> properties = HeterogCollections.<String>newLinkedMap();
		properties.putAllFromSource(src.formProperties.getPropertiesMap());
		this.properties = properties;
		this.order = src.order;
		this.index = src.index;
		this.validators = new ArrayList<Validator<T>>(src.validators);
	}
	
	/**
	 * True if this form should be secured with authorization token.
	 * @param secured
	 * @return
	 */
	public BasicFormMappingBuilder<T> secured(boolean secured) {
		this.secured = secured;
		if (secured) {
			fieldForAuthToken();
		}
		return this;
	}
	
	/** Only for internal usage. */
	BasicFormMappingBuilder<T> parent(FormMapping<?> parent) {
		this.parent = parent;
		return this;
	}
	
	/** Only for internal usage. */
	BasicFormMappingBuilder<T> order(int order) {
		this.order = order;
		return this;
	}
	
	/** Only for internal usage. */
	BasicFormMappingBuilder<T> index(Integer index) {
		this.index = index;
		return this;
	}
	
	/** Only for internal usage. */
	BasicFormMappingBuilder<T> dataClass(Class<T> dataClass) {
		this.dataClass = dataClass;
		return this;
	}
	
	/** Only for internal usage. */
	BasicFormMappingBuilder<T> instantiator(Instantiator<T> instantiator) {
		this.instantiator = instantiator;
		return this;
	}
	
	/** Only for internal usage. */
	BasicFormMappingBuilder<T> config(Config config) {
		this.config = config;
		return this;
	}
	
	/** Only for internal usage. */
	BasicFormMappingBuilder<T> filledObject(T filledObject) {
		this.filledObject = filledObject;
		return this;
	}
	
	/** Only for internal usage. */
	BasicFormMappingBuilder<T> validationResult(ValidationResult validationResult) {
		this.validationResult = validationResult;
		return this;
	}
	
	/**
	 * Adds validator.
	 * @param validator
	 * @return
	 */
	public BasicFormMappingBuilder<T> validator(Validator<T> validator) {
		this.validators.add(validator);
		return this;
	}
	
	/**
	 * Adds form field specification.
	 * @param fieldProps
	 * @return
	 */
	public <U> BasicFormMappingBuilder<T> field(FieldProps<U> fieldProps) {
		fields.put(fieldProps.getPropertyName(), fieldProps.build(nextNestedElementOrder++));
		return this;
	}
	
	/**
	 * Adds form field specification.
	 * @param form field with specified property name
	 * @return
	 */
	public <U> BasicFormMappingBuilder<T> field(FormField<U> formField) {
		if (formField.getName().contains(Forms.PATH_SEP)) {
			throw new IllegalStateException("Name of specified form field should contain only name "
				+ "of bound property (without full path). "
				+ "Name of outer mapping is automatically prepended to it.");
		}
		fields.put(formField.getName(), new FormFieldImpl<U>(formField, (FormMapping<?>)null, this.nextNestedElementOrder++));
		return this;
	}
	
	/**
	 * Adds form field specification.
	 * @param propertyName name of mapped property
	 * @param type type of form field, for e.g.: text, checkbox, textarea, ...
	 * @return
	 */
	public <U> BasicFormMappingBuilder<T> field(String propertyName, String type) {
		return field(Forms.field(propertyName, type));
	}
	
	/**
	 * Adds form field specification.
	 * @param propertyName name of mapped property
	 * @param type type of form field
	 * @return
	 */
	public <U> BasicFormMappingBuilder<T> field(String propertyName, Field type) {
		return field(propertyName, type.getType());
	}
	
	/**
	 * Adds form field specification.
	 * @param propertyName name of mapped property
	 * @return
	 */
	public <U> BasicFormMappingBuilder<T> field(String propertyName) {
		return field(propertyName, (String)null);
	}
	
	/**
	 * Adds specifications of form fields.
	 * @param fields
	 * @return
	 */
	public BasicFormMappingBuilder<T> fields(FieldProps<?> ... fields) {
		for (int i = 0; i < fields.length; i++) {
			field(fields[i]);
		}
		return this;
	}
	
	/**
	 * Adds specifications of form fields.
	 * @param fieldNames
	 * @return
	 */
	public BasicFormMappingBuilder<T> fields(String ... fieldNames) {
		for (int i = 0; i < fieldNames.length; i++) {
			field(fieldNames[i]);
		}
		return this;
	}
	
	/**
	 * Registers form mapping for nested object in form data.
	 * Path of this mapping is added as a prefix to given nested mapping.
	 * @param mapping nested mapping
	 * @return
	 */
	public <U> BasicFormMappingBuilder<T> nested(FormMapping<U> mapping) {
		return nestedInternal(mapping);
	}
	
	/**
	 * Registers form mappings for nested objects in form data.
	 * @param mappings nested mappings
	 * @return
	 */
	public BasicFormMappingBuilder<T> nested(FormMapping<?> ... mappings) {
		if (mappings != null) {
			for (FormMapping<?> b : mappings) {
				nested(b);
			}
		}
		return this;
	}
	
	public <U> BasicFormMappingBuilder<T> property(FormElementProperty<U> fieldProperty, U value) {
		this.properties.putTyped(fieldProperty, value);
		return this;
	}
	
	public BasicFormMappingBuilder<T> visible(boolean visible) {
		return property(FormElementProperty.VISIBLE, Boolean.valueOf(visible));
	}
	
	public BasicFormMappingBuilder<T> enabled(boolean enabled) {
		return property(FormElementProperty.ENABLED, Boolean.valueOf(enabled));
	}
	
	public BasicFormMappingBuilder<T> readonly(boolean readonly) {
		return property(FormElementProperty.READ_ONLY, Boolean.valueOf(readonly));
	}

	public BasicFormMappingBuilder<T> required(boolean required) {
		if (required) {
			Validator<T> validator = RequiredValidator.getInstance();
			if (!validators.contains(validator)) {
				validators.add(validator);
			}
		}
		return this;
	}
	
	public BasicFormMappingBuilder<T> help(String help) {
		return property(FormElementProperty.HELP, help);
	}
	
	public BasicFormMappingBuilder<T> labelVisible(boolean visible) {
		return property(FormElementProperty.LABEL_VISIBLE, Boolean.valueOf(visible));
	}
	
	public BasicFormMappingBuilder<T> dataAjaxActions(HandledJsEvent action) {
		return dataAjaxActions(new HandledJsEvent[] { action });
	}
	
	public BasicFormMappingBuilder<T> dataAjaxActions(HandledJsEvent[] actions) {
		return property(FormElementProperty.DATA_AJAX_ACTIONS, actions);
	}
	
	public BasicFormMappingBuilder<T> dataAjaxActions(List<? extends HandledJsEvent> actions) {
		return property(FormElementProperty.DATA_AJAX_ACTIONS, actions.toArray(new HandledJsEvent[0]));
	}

	public FormMapping<T> build() {
		boolean plainCopy = false;
		return buildInternal(this.config, plainCopy);
	}
	
	/**
	 * Builds form mapping with given user-defined configuration.
	 * @param config
	 * @return
	 */
	public BasicFormMapping<T> build(Config config) {
		boolean plainCopy = false;
		return buildInternal(config, plainCopy);
	}
	
	<U> BasicFormMappingBuilder<T> nestedInternal(FormMapping<U> nestedMapping) {
		if (nestedMapping.getName().contains(Forms.PATH_SEP)) {
			throw new IllegalStateException("Nested mapping should be defined with path that is one simple name " + 
				"that corresponds to the name of the property");
		}
		this.nested.put(nestedMapping.getPropertyName(), nestedMapping.withOrder(nextNestedElementOrder++));
		
		// should return BasicFormMappingBuilder, not ConfigurableBasicFormMappingBuilder
		// all configurable dependencies are taken from outer mapping
		return this;
	}
	
	/** Adding multiple form fields. Operation for internal use only. */
	BasicFormMappingBuilder<T> fields(Map<String, FormField<?>> fields) {
		if (fields == null) throw new IllegalArgumentException("fields cannot be null");
		Map<String, FormField<?>> flds = new LinkedHashMap<String, FormField<?>>();
		for (Map.Entry<String, FormField<?>> e : fields.entrySet()) {
			flds.put(e.getKey(), e.getValue());
		}
		this.fields.putAll(flds);
		return this;
	}
	
	/**
	 * Only for internal usage.
	 * @param fields
	 * @return
	 */
	BasicFormMappingBuilder<T> fieldsReplaceAll(Map<String, FormField<?>> fields) {
		this.fields = Collections.unmodifiableMap(fields);
		return this;
	}
	
	/**
	 * Builds form mapping.
	 * @param config
	 * @param simpleCopy true if simple copy of builder's data should be constructed, otherwise propagation
	 * of parent mapping into fields and nested mappings is processed
	 * @return
	 */
	BasicFormMapping<T> buildInternal(Config config, boolean simpleCopy) {
		this.config = config;
		
		if (this.automatic) {
			Config c = config;
			if (c == null) {
				c = Forms.defaultConfig(this.dataClass);
			}
			buildFieldsAndNestedMappingsAutomatically(c);
		}
		
		BasicFormMapping<T> mapping = null;
		if (this.mappingType == MappingType.LIST) {
			mapping = new BasicListFormMapping<T>(this, simpleCopy);
		} else {
			mapping = new BasicFormMapping<T>(this, simpleCopy);
		}
		checkValidFormMapping(mapping);
		return mapping;
	}

	boolean isAccessor(Method method, PropertyMethodRegex accessorRegex) {
		return accessorRegex.matchesMethod(method.getName()) && method.getParameterTypes().length == 0;
	}
	
	Map<String, Method> getClassProperties(Class<?> beanClass, BeanExtractor extractor, PropertyMethodRegex accessorRegex) {
		final Map<String, Method> properties = new LinkedHashMap<String, Method>();
        final Method[] objMethods = beanClass.getMethods();
        for (Method objMethod : objMethods) {
        	if (extractor.isIgnored(objMethod)) continue;
        	if (objMethod.getName().equals("getClass")) continue;
            if (isAccessor(objMethod, accessorRegex)) {
            	String propName = accessorRegex.getPropertyName(objMethod.getName());
            	if (propName != null) {
            		properties.put(propName, objMethod);
            	}
            }
        }
		return Collections.unmodifiableMap(properties);
	}
	
	void buildFieldsAndNestedMappingsAutomatically(Config config) {
		if (config == null) throw new IllegalArgumentException("config cannot be null");
		Map<String, Method> propertiesByNames = getClassProperties(this.dataClass, config.getBeanExtractor(), config.getAccessorRegex());
		
		final Instantiator<T> inst = this.instantiator;
		ConstructionDescription constrDesc = inst.getDescription(config.getArgumentNameResolver());
		
		Method[] methods = this.dataClass.getMethods();
		for (Map.Entry<String, Method> e : propertiesByNames.entrySet()) {
			String propertyName = e.getKey();
			if (!this.fields.containsKey(propertyName) && !this.nested.containsKey(propertyName)) {
				// Not in fields or nested mappings which are defined explicitly.
				// Check if also setter (or construction method argument) for this property exists, otherwise getter
				// can serve as an auxiliary method only.
				if (isSettable(constrDesc, methods, config.getSetterRegex(), propertyName)) {
					Class<?> propertyType = e.getValue().getReturnType();
					if (propertyType.getName().equals(Class.class.getName()))
						throw new IllegalStateException("Cannot map property " + 
							propertyName + " of type " + propertyType.getName() + " in class " + this.dataClass.getName());
					if (isDataClassForField(propertyType, config)) {
						this.field(propertyName); // single value field
					} else {
						if (isCollection(propertyType, config)) {
							Class<?> itemClass = BindingReflectionUtils.itemTypeFromGenericCollType(e.getValue().getGenericReturnType());
							if (itemClass != null && isDataClassForField(itemClass, config)) {
								this.field(propertyName); // multiple value field
							} else {
								// nested collection of complex types or unknown types
								if (itemClass == null) 
									throw new IllegalStateException("Cannot resolve item type of collection type of property " + 
										propertyName + " in class " + this.dataClass.getName());
								BasicFormMapping<?> mapping = null;
								if (this.secured) {
									mapping = Forms.automaticSecured(itemClass, propertyName, null, MappingType.LIST).build(config);
								} else {
									mapping = Forms.automatic(itemClass, propertyName, null, MappingType.LIST).build(config);
								}
								this.nested(mapping);
							}
						} else {
							// some complex or unknown type
							assertValidComplexTypeProperty(propertyType, propertyName);
							BasicFormMapping<?> mapping = null;
							if (this.secured) {
								mapping = Forms.automaticSecured(propertyType, propertyName).build(config);
							} else {
								mapping = Forms.automatic(propertyType, propertyName).build(config);
							}
							this.nested(mapping);
						}
					}
				}
			}
		}
	}
	
	void fieldForAuthToken() {
		if (!fields.containsKey(Forms.AUTH_TOKEN_FIELD_NAME)) {
			field(Forms.AUTH_TOKEN_FIELD_NAME, "hidden");
		}
	}
	
	private void checkValidFormMapping(BasicFormMapping<T> mapping) {
		if (mapping.propertyName == null || mapping.propertyName.isEmpty()) {
			throw new IllegalStateException("propertyName must not be empty");
		}
		// All fields must have names prefixed with mapping path
		for (FormField<?> field : mapping.fields.values()) {
			if (field.getName() == null || field.getName().isEmpty()) {
				throw new IllegalStateException("Field name must not be empty");
			}
			if (!field.getName().contains(Forms.PATH_SEP)) {
				throw new IllegalStateException("Full path (name) of field '" + field.getName() + "' must contain at least one path separator that separates mapping path '" + 
					mapping.getName() + "' from property name (or more complex path) mapped to field");
			}
			if (!field.getName().startsWith(mapping.getName())) {
				throw new IllegalStateException("Field name '" + field.getName() + "' does not start with mapping name '" + mapping.getName() + "'");
			}
			
			checkRequired(field);
		}
		checkRequired(mapping);
	}

	private <U> void checkRequired(FormElement<U> element) {
		// This is just to check if the property represented by the element exists (by introspection):
		boolean required = element.isRequired();
		if (required) {
			// nothing
		}
	}

	private void assertValidComplexTypeProperty(Class<?> propertyType, String propertyName) {
		if (String.class.isAssignableFrom(propertyType))
			throw new IllegalStateException("Cannot map property " + 
				propertyName + " of type " + propertyType.getName() + " in class " + this.dataClass.getName());
		if (propertyType.isEnum())
			throw new IllegalStateException("Cannot map property " + 
				propertyName + " of type " + propertyType.getName() + " in class " + this.dataClass.getName());
	}

	private boolean isSettable(ConstructionDescription constrDesc, 
		Method[] methods, PropertyMethodRegex setterRegex, String propertyName) {
		if (setterRegex == null) throw new IllegalArgumentException("setterRegex cannot be null");
		boolean settable = false;
		for (Method method : methods) {
			if (isPropertySetter(setterRegex, method, propertyName)) {
				settable = true;
				break;
			}
		}
		if (!settable) {
			List<String> argNames = constrDesc.getArgNames();
			if (argNames != null) {
				for (String argName : argNames) {
					if (argName != null && argName.equals(propertyName)) {
						settable = true;
						break;
					}
				}
			}
		}
		return settable;
	}
	
	private boolean isPropertySetter(PropertyMethodRegex setterRegex, Method method, String propertyName) {
		return setterRegex.matchesPropertyMethod(method.getName(), propertyName) && method.getParameterTypes().length == 1;
	}

	private boolean isCollection(Class<?> type, Config config) {
		return config.getCollectionBuilders().canHandle(CollectionSpec.getInstance(type, ItemsOrder.LINEAR)) 
		  || config.getCollectionBuilders().canHandle(CollectionSpec.getInstance(type, ItemsOrder.HASH))
		  || config.getCollectionBuilders().canHandle(CollectionSpec.getInstance(type, ItemsOrder.SORTED));
	}

	private boolean isDataClassForField(Class<?> retType, Config config) {
		return PrimitiveType.byPrimitiveClass(retType) != null 
			|| PrimitiveType.byWrapperClass(retType) != null 
			|| config.getFormatters().canHandle(retType)
			|| UploadedFile.class.isAssignableFrom(retType);
	}
}

