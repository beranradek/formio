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

import net.formio.binding.Instantiator;
import net.formio.binding.StaticFactoryMethod;

/**
 * API for form definition and processing.
 * @author Radek Beran
 */
public final class Forms {
	
	public static final String AUTH_TOKEN_FIELD_NAME = "formAuthToken";
	
	/**
	 * Separator of parts in the path (used in fully qualified field name).
	 */
	public static final String PATH_SEP = "-";

	/**
	 * Starts building basic mapping for which all the fields and nested mappings 
	 * must be explicitly specified.
	 * @param editedObjectClass class of form mapping data
	 * @param formName name of the form/property with nested data
	 * @return form mapping builder
	 */
	public static <T> BasicFormMappingBuilder<T> basic(Class<T> editedObjectClass, String formName) {
		return basic(editedObjectClass, formName, Forms.<T>getDefaultInstantiator(), DEFAULT_MAPPING_TYPE);
	}
	
	/**
	 * Starts building basic mapping for which all the fields and nested mappings 
	 * must be explicitly specified.
	 * @param editedObjectClass class of form mapping data
	 * @param formName name of the form/property with nested data
	 * @param instantiator instantiator of form data
	 * @return form mapping builder
	 */
	public static <T> BasicFormMappingBuilder<T> basic(Class<T> editedObjectClass, String formName, Instantiator<T> instantiator) {
		return basic(editedObjectClass, formName, instantiator, DEFAULT_MAPPING_TYPE);
	}
	
	/**
	 * Starts building basic mapping for which all the fields and nested mappings 
	 * must be explicitly specified.
	 * @param editedObjectClass class of form mapping data
	 * @param formName name of the form/property with nested data
	 * @param mappingType type of nested mapping
	 * @return form mapping builder
	 */
	public static <T> BasicFormMappingBuilder<T> basic(Class<T> editedObjectClass, String formName, MappingType mappingType) {
		return basic(editedObjectClass, formName, Forms.<T>getDefaultInstantiator(), mappingType);
	}
	
	/**
	 * Starts building basic mapping for which all the fields and nested mappings 
	 * must be explicitly specified.
	 * @param editedObjectClass
	 * @param formName name of the form/property with nested data
	 * @param instantiator instantiator of form data
	 * @param mappingType type of nested mapping
	 * @return form mapping builder
	 */
	public static <T> BasicFormMappingBuilder<T> basic(Class<T> editedObjectClass, String formName, Instantiator<T> instantiator, MappingType mappingType) {
		return mappingInternal(editedObjectClass, formName, instantiator, false, mappingType, false);
	}
	
	/**
	 * Like corresponding basic mapping, including CSRF protection.
	 * @param editedObjectClass
	 * @param formName
	 * @return
	 */
	public static <T> BasicFormMappingBuilder<T> basicSecured(Class<T> editedObjectClass, String formName) {
		return basicSecured(editedObjectClass, formName, Forms.<T>getDefaultInstantiator(), DEFAULT_MAPPING_TYPE);
	}
	
	/**
	 * Like corresponding basic mapping, including CSRF protection.
	 * @param editedObjectClass
	 * @param formName
	 * @param instantiator
	 * @return
	 */
	public static <T> BasicFormMappingBuilder<T> basicSecured(Class<T> editedObjectClass, String formName, Instantiator<T> instantiator) {
		return basicSecured(editedObjectClass, formName, instantiator, DEFAULT_MAPPING_TYPE);
	}
	
	/**
	 * Like corresponding basic mapping, including CSRF protection.
	 * @param editedObjectClass
	 * @param formName
	 * @param mappingType
	 * @return
	 */
	public static <T> BasicFormMappingBuilder<T> basicSecured(Class<T> editedObjectClass, String formName, MappingType mappingType) {
		return basicSecured(editedObjectClass, formName, Forms.<T>getDefaultInstantiator(), mappingType);
	}
	
	/**
	 * Like corresponding basic mapping, including CSRF protection.
	 * @param editedObjectClass
	 * @param formName
	 * @param instantiator
	 * @param mappingType
	 * @return
	 */
	public static <T> BasicFormMappingBuilder<T> basicSecured(Class<T> editedObjectClass, String formName, Instantiator<T> instantiator, MappingType mappingType) {
		return mappingInternal(editedObjectClass, formName, instantiator, false, mappingType, true);
	}
	
	/**
	 * Starts building mapping that is automatically specified by introspection 
	 * of given data class.
	 * @return
	 */
	public static <T> BasicFormMappingBuilder<T> automatic(Class<T> editedObjectClass, String formName) {
		return automatic(editedObjectClass, formName, Forms.<T>getDefaultInstantiator(), DEFAULT_MAPPING_TYPE);
	}
	
	/**
	 * Starts building mapping that is automatically specified by introspection 
	 * of given data class.
	 * @param editedObjectClass
	 * @param formName
	 * @param instantiator
	 * @return
	 */
	public static <T> BasicFormMappingBuilder<T> automatic(Class<T> editedObjectClass, String formName, Instantiator<T> instantiator) {
		return automatic(editedObjectClass, formName, instantiator, DEFAULT_MAPPING_TYPE);
	}
	
	/**
	 * Starts building mapping that is automatically specified by introspection 
	 * of given data class.
	 * @param editedObjectClass
	 * @param formName
	 * @param mappingType
	 * @return
	 */
	public static <T> BasicFormMappingBuilder<T> automatic(Class<T> editedObjectClass, String formName, MappingType mappingType) {
		return automatic(editedObjectClass, formName, Forms.<T>getDefaultInstantiator(), mappingType);
	}
	
	/**
	 * Starts building mapping that is automatically specified by introspection 
	 * of given data class.
	 * @param editedObjectClass
	 * @param formName
	 * @param instantiator
	 * @param mappingType
	 * @return
	 */
	public static <T> BasicFormMappingBuilder<T> automatic(Class<T> editedObjectClass, String formName, Instantiator<T> instantiator, MappingType mappingType) {
		return mappingInternal(editedObjectClass, formName, instantiator, true, mappingType, false);
	}
	
	/**
	 * Like corresponding automatic mapping, including CSRF protection.
	 * @param editedObjectClass
	 * @param formName
	 * @return
	 */
	public static <T> BasicFormMappingBuilder<T> automaticSecured(Class<T> editedObjectClass, String formName) {
		return automaticSecured(editedObjectClass, formName, Forms.<T>getDefaultInstantiator(), DEFAULT_MAPPING_TYPE);
	}
	
	/**
	 * Like corresponding automatic mapping, including CSRF protection.
	 * @param editedObjectClass
	 * @param formName
	 * @param instantiator
	 * @return
	 */
	public static <T> BasicFormMappingBuilder<T> automaticSecured(Class<T> editedObjectClass, String formName, Instantiator<T> instantiator) {
		return automaticSecured(editedObjectClass, formName, instantiator, DEFAULT_MAPPING_TYPE);
	}
	
	/**
	 * Like corresponding automatic mapping, including CSRF protection.
	 * @param editedObjectClass
	 * @param formName
	 * @param mappingType
	 * @return
	 */
	public static <T> BasicFormMappingBuilder<T> automaticSecured(Class<T> editedObjectClass, String formName, MappingType mappingType) {
		return automaticSecured(editedObjectClass, formName, Forms.<T>getDefaultInstantiator(), mappingType);
	}
	
	/**
	 * Like corresponding automatic mapping, including CSRF protection.
	 * @param editedObjectClass
	 * @param formName
	 * @param instantiator
	 * @param mappingType
	 * @return
	 */
	public static <T> BasicFormMappingBuilder<T> automaticSecured(Class<T> editedObjectClass, String formName, Instantiator<T> instantiator, MappingType mappingType) {
		return mappingInternal(editedObjectClass, formName, instantiator, true, mappingType, true);
	}
	
	/**
	 * Instantiator that uses static factory method to construct object of type T.
	 * @param constructedClass
	 * @param methodName
	 * @param factoryClass
	 * @return
	 */
	public static <T, U> Instantiator<T> factoryMethod(Class<T> constructedClass, String methodName, Class<U> factoryClass) {
		return new StaticFactoryMethod<T>(factoryClass, methodName);
	}
	
	/**
	 * Instantiator that uses static factory method to construct object of type T.
	 * @param constructedClass
	 * @param methodName
	 * @return
	 */
	public static <T, U> Instantiator<T> factoryMethod(Class<T> constructedClass, String methodName) {
		return factoryMethod(constructedClass, methodName, constructedClass);
	}
	
	/**
	 * Creates configuration for form processing.
	 * @return
	 */
	public static Config.Builder config() {
		return new Config.Builder();
	}
	
	/**
	 * Creates specification of form field.
	 * @param propertyName
	 * @param type
	 * @return
	 */
	public static <T> FieldProps.Builder<T> field(String propertyName, String type) {
		return new FieldProps.Builder<T>(propertyName, type);
	}
	
	/**
	 * Creates specification of form field.
	 * @param propertyName
	 * @return
	 */
	public static <T> FieldProps.Builder<T> field(String propertyName) {
		return field(propertyName, (String)null);
	}
	
	private static <T> BasicFormMappingBuilder<T> mappingInternal(Class<T> dataClass, String formName, Instantiator<T> instantiator, boolean automatic, MappingType mappingType, boolean secured) {
		return new BasicFormMappingBuilder<T>(dataClass, formName, instantiator, automatic, mappingType).secured(secured);
	}
	
	private Forms() {
		throw new AssertionError("Not instantiable, use static members.");
	}
	
	private static final MappingType DEFAULT_MAPPING_TYPE = MappingType.SINGLE;
	private static <T> Instantiator<T> getDefaultInstantiator() {
		return null;
	}
}
