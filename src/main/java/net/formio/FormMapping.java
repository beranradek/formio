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

import java.util.List;
import java.util.Map;

import javax.validation.groups.Default;

import net.formio.binding.Instantiator;
import net.formio.validation.ValidationResult;

/**
 * A form for editing object (form data) of type T.
 * All implementations are immutable, so the instance of this form definition
 * can be freely shared and cached.
 * 
 * @author Radek Beran
 */
public interface FormMapping<T> {
	
	/**
	 * Name of this mapping (full path from the most outer mapping to this potentially nested mapping).
	 * @return
	 */
	String getName();
	
	/**
	 * Key for the label for filled object (or list of objects).
	 * Derived from path. Does not contain any brackets with indexes.
	 * @return
	 */
	String getLabelKey();
	
	/**
	 * Object filled in this mapping, or {@code null} if this mapping is not filled yet.
	 * @return
	 */
	Object getFilledObject();
	
	/**
	 * Returns true if this is mapping for object that is required to fill.
	 * @return
	 */
	boolean isRequired();
	
	/**
	 * Class of edited object (form data).
	 * @return
	 */
	Class<T> getDataClass();
	
	/**
	 * Instantiator of class of type T.
	 * @return
	 */
	Instantiator<T> getInstantiator();
	
	/**
	 * Fills form with values from given object and returns new filled form
	 * that can be populated to the template. 
	 * @param formData object that holds data for the form and initial validation messages/report
	 * @return
	 */
	FormMapping<T> fill(FormData<T> formData);
	
	/**
	 * Binds and validates data from the form.
	 * @param paramsProvider provider of request parameters
	 * @param validationGroups the group or list of groups targeted for validation (defaults to implicit 
	 * {@link Default} group, but other groups (interfaces) can be created - extended or not extended
	 * from {@link Default} - and their classes used in attribute "groups" of validation annotations)
	 * @return
	 */
	FormData<T> bind(ParamsProvider paramsProvider, Class<?>... validationGroups);
	
	/**
	 * Binds and validates data from the form.
	 * @param paramsProvider provider of request parameters
	 * @param instance instance to which data from the request parameter provider should be bound
	 * @param validationGroups the group or list of groups targeted for validation (defaults to implicit 
	 * {@link Default} group, but other groups (interfaces) can be created - extended or not extended
	 * from {@link Default} - and their classes used in attribute "groups" of validation annotations)
	 * @return
	 */
	FormData<T> bind(ParamsProvider paramsProvider, T instance, Class<?>... validationGroups);
	
	/**
	 * Returns result with validation messages, {@code null} if form data was not validated yet.
	 * @return
	 */
	ValidationResult getValidationResult();
	
	/**
	 * Fields in this mapping by their names.
	 * @return
	 */
	Map<String, FormField> getFields();
	
	/**
	 * Nested mappings of this mapping.
	 * @return
	 */
	Map<String, FormMapping<?>> getNested();
	
	/**
	 * List of mappings for individual filled data items if this is list mapping,
	 * empty list otherwise.
	 * @return
	 */
	List<FormMapping<T>> getList();
	
	
	/**
	 * Configuration of this mapping.
	 * @return
	 */
	Config getConfig();
	
	/**
	 * Returns true if this mapping has non-default user defined configuration.
	 * @return
	 */
	boolean isUserDefinedConfig();
	
	/**
	 * Returns copy of this mapping with new path that has given prefix prepended.
	 * Given prefix is applied to all nested mappings recursively.
	 * @param pathPrefix
	 * @return
	 */
	FormMapping<T> withPathPrefix(String pathPrefix);
	
	/**
	 * Returns copy of this mapping with new path that contains index after given path prefix.
	 * Given index is applied to all nested mappings recursively.
	 * @param index
	 * @param prefix
	 * @return
	 */
	FormMapping<T> withIndexAfterPathPrefix(int index, String prefix);
	
	/**
	 * Returns copy of this mapping that uses given configuration
	 * and required flag.
	 * @param config
	 * @param required
	 * @return
	 */
	FormMapping<T> withConfig(Config config, boolean required);
	
	/**
	 * Returns string representation of this mapping.
	 * @param indent indentation characters
	 * @return
	 */
	String toString(String indent);
	
}
