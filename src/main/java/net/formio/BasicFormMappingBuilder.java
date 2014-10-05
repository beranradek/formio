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

import net.formio.binding.BeanExtractor;
import net.formio.binding.BindingReflectionUtils;
import net.formio.binding.ConstructionDescription;
import net.formio.binding.ConstructorInstantiator;
import net.formio.binding.Instantiator;
import net.formio.binding.PrimitiveType;
import net.formio.binding.PropertyMethodRegex;
import net.formio.binding.collection.CollectionSpec;
import net.formio.binding.collection.ItemsOrder;
import net.formio.upload.UploadedFile;
import net.formio.validation.ValidationResult;

/**
 * Basic builder of {@link FormMapping}.
 * @author Radek Beran
 */
public class BasicFormMappingBuilder<T> {

	/** 
	 * Name prefixing all names of the fields in this mapping; 
	 * it contains complete path to this nested mapping, if this is a nested mapping.
	 */
	String path;
	Class<T> dataClass;
	Instantiator<T> instantiator;
	/** Mapping simple property names to fields. */
	Map<String, FormField<?>> fields = new LinkedHashMap<String, FormField<?>>();
	/** Mapping simple property names to nested mappings. Property name is a part of full path of nested mapping. */
	Map<String, FormMapping<?>> nested = new LinkedHashMap<String, FormMapping<?>>();
	List<FormMapping<T>> listOfMappings = new ArrayList<FormMapping<T>>();
	Config config;
	ValidationResult validationResult;
	boolean userDefinedConfig;
	MappingType mappingType;
	T filledObject;
	boolean automatic;
	boolean secured;

	/**
	 * Should be constructed only via {@link Forms} entry point of API.
	 */
	BasicFormMappingBuilder(Class<T> dataClass, String formName, Instantiator<T> inst, boolean automatic, MappingType mappingType) {
		if (dataClass == null) throw new IllegalArgumentException("dataClass must be filled");
		if (formName == null || formName.isEmpty()) throw new IllegalArgumentException("form name must be filled");
		if (mappingType == null) throw new IllegalArgumentException("mappingType must be filled");
		this.dataClass = dataClass;
		this.path = formName;
		this.instantiator = inst; // can be null
		this.mappingType = mappingType;
		this.automatic = automatic;
	}
	
	BasicFormMappingBuilder(Class<T> objectClass, String formName, Instantiator<T> inst, boolean automatic) {
		this(objectClass, formName, inst, automatic, MappingType.SINGLE);
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
	
	/**
	 * Adds form field specification.
	 * @param fieldProperties
	 * @return
	 */
	public <U> BasicFormMappingBuilder<T> field(FieldProps<U> fieldProperties) {
		String frmPrefixedName = formPrefixedName(fieldProperties.getPropertyName());
		fields.put(fieldProperties.getPropertyName(), FormFieldImpl.getInstance(
			frmPrefixedName, 
			fieldProperties.getType(), 
			fieldProperties.getPattern(), 
			fieldProperties.getFormatter(), 
			fieldProperties.getProperties()));
		return this;
	}
	
	/**
	 * Adds form field specification.
	 * @param propertyName name of mapped property
	 * @param type type of form field, for e.g.: text, checkbox, textarea, ...
	 * @return
	 */
	public <U> BasicFormMappingBuilder<T> field(String propertyName, String type) {
		return field(Forms.field(propertyName, type).build());
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
	 * @param mapping nested mapping - with class of nested object and form name that is
	 * equal to name of property in outer mapped object
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

	public FormMapping<T> build() {
		return buildInternal(
			Forms.config()
				.messageBundleName(dataClass.getName().replace(".", "/"))
				.build(), false
		); // default config
	}
	
	public BasicFormMapping<T> build(Config config) {
		return buildInternal(config, true);
	}
	
	<U> BasicFormMappingBuilder<T> nestedInternal(FormMapping<U> nestedMapping) {
		// nested mapping is defined with path that is one simple name that corresponds to the name of the property
		String propertyName = getFirstPathName(nestedMapping.getName());
		
		// Name of nested mapping and names of its fields must be prefixed by path of this mapping
		// (and recursively - nested mapping can have its own nested mappings).
		this.nested.put(propertyName, nestedMapping.withPathPrefix(this.path));
		
		// should return BasicFormMappingBuilder, not ConfigurableBasicFormMappingBuilder
		// all configurable dependencies are taken from outer mapping
		return this;
	}
	
	/** Adding multiple form fields. Operation for internal use only. */
	BasicFormMappingBuilder<T> fields(Map<String, FormField<?>> fields) {
		if (fields == null) throw new IllegalArgumentException("fields cannot be null");
		Map<String, FormField<?>> flds = new LinkedHashMap<String, FormField<?>>();
		for (Map.Entry<String, FormField<?>> e : fields.entrySet()) {
			FormField<?> f = e.getValue();
			if (!f.getName().startsWith(path + Forms.PATH_SEP)) {
				throw new IllegalStateException("Field name '" + f.getName() + "' is not prefixed with form name '" + path + "'!");
			}
			flds.put(e.getKey(), f);
		}
		this.fields.putAll(flds);
		return this;
	}
	
	BasicFormMapping<T> buildInternal(Config config, boolean userDefinedConfig) {
		if (config == null) throw new IllegalArgumentException("config cannot be null");
		this.userDefinedConfig = userDefinedConfig;
		this.config = config;
		
		if (this.automatic) {
			buildFieldsAndNestedMappings(config);
		}
		
		BasicFormMapping<T> mapping = null;
		if (this.mappingType == MappingType.LIST) {
			mapping = new BasicListFormMapping<T>(this);
		} else {
			mapping = new BasicFormMapping<T>(this);
		}
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
	
	void buildFieldsAndNestedMappings(Config config) {
		if (config == null) throw new IllegalArgumentException("config cannot be null");
		Map<String, Method> propertiesByNames = getClassProperties(this.dataClass, config.getBeanExtractor(), config.getAccessorRegex());
		
		Instantiator<T> inst = null;
		if (this.instantiator == null) {
			// using some public constructor as default instantiation strategy
			inst = new ConstructorInstantiator<T>(this.dataClass);
		} else {
			// user defined instantiator
			inst = this.instantiator;
		}
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
		field(Forms.AUTH_TOKEN_FIELD_NAME, "hidden");
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
	
	private String getFirstPathName(String path) {
		String name = null;
		int indexOfSep = path.indexOf(Forms.PATH_SEP);
		if (indexOfSep < 0) {
			name = path;
		} else {
			name = path.substring(0, indexOfSep);
		}
		return name;
	}
	
	private String formPrefixedName(String name) {
		if (name == null) return null;
		return path + Forms.PATH_SEP + name;
	}
}

