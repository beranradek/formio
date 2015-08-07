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
import net.formio.data.RequestContext;
import net.formio.format.Location;
import net.formio.props.FormMappingProperties;

/**
 * A form for editing object (form data) of type T.
 * All implementations are immutable, so the instance of this form definition
 * can be freely shared and cached.
 * 
 * @author Radek Beran
 */
public interface FormMapping<T> extends FormElement<T> {
	
	/**
	 * Object filled in this mapping, or {@code null} if this mapping is not filled yet.
	 * @return
	 */
	T getFilledObject();
	
	/**
	 * Class of edited object (form data).
	 * @return
	 */
	Class<T> getDataClass();
	
	/**
	 * Instantiator of classes.
	 * @return
	 */
	Instantiator getInstantiator();
	
	/**
	 * Fills form with values from given object and returns new filled form
	 * that can be populated to the template. 
	 * @param formData object that holds data for the form and initial validation messages/report
	 * @param loc local settings for formatting values from/to strings
	 * @param ctx context of the request required for security features
	 * @return
	 */
	FormMapping<T> fill(FormData<T> formData, Location loc, RequestContext ctx);
	
	/**
	 * Fills form with values from given object and returns new filled form
	 * that can be populated to the template. 
	 * @param formData object that holds data for the form and initial validation messages/report
	 * @param loc local settings for formatting values from/to strings 
	 * @return
	 */
	FormMapping<T> fill(FormData<T> formData, Location loc);
	
	/**
	 * Fills form with values from given object and returns new filled form
	 * that can be populated to the template. 
	 * @param formData object that holds data for the form and initial validation messages/report
	 * @param ctx context of the request required for security features
	 * @return
	 */
	FormMapping<T> fill(FormData<T> formData, RequestContext ctx);
	
	/**
	 * Fills form with values from given object and returns new filled form
	 * that can be populated to the template. Configured or default locale and time zone is used.
	 * @param formData object that holds data for the form and initial validation messages/report
	 * @return
	 */
	FormMapping<T> fill(FormData<T> formData);
	
	/**
	 * Fills form with values from given object, validates the object and returns new filled form
	 * that can be populated to the template. Actual validation messages are present for rendering.
	 * @param formData object that holds data for the form and initial validation messages
	 * @param loc local settings for formatting values from/to strings
	 * @param ctx
	 * @param validationGroups
	 * @return
	 */
	FormMapping<T> fillAndValidate(FormData<T> formData, Location loc, RequestContext ctx, Class<?> ... validationGroups);
	
	/**
	 * Fills form with values from given object, validates the object and returns new filled form
	 * that can be populated to the template. Actual validation messages are present for rendering.
	 * @param formData object that holds data for the form and initial validation messages
	 * @param loc local settings for formatting values from/to strings
	 * @param validationGroups
	 * @return
	 */
	FormMapping<T> fillAndValidate(FormData<T> formData, Location loc, Class<?> ... validationGroups);
	
	/**
	 * Fills form with values from given object, validates the object and returns new filled form
	 * that can be populated to the template. Actual validation messages are present for rendering.
	 * Configured or default locale and time zone is used.
	 * @param formData object that holds data for the form and initial validation messages
	 * @param validationGroups
	 * @return
	 */
	FormMapping<T> fillAndValidate(FormData<T> formData, Class<?>... validationGroups);
	
	/**
	 * Returns form element that invoked the TDI AJAX request, filled with request data (and validation messages);
	 * or {@code null} of no such element exists (e.g. given request is not an TDI AJAX request).
	 * @param requestParams
	 * @param loc local settings for formatting values from/to strings
	 * @param validationGroups
	 * @return filled source element
	 */
	FormElement<?> fillTdiAjaxSrcElement(RequestParams requestParams, Location loc, Class<?>... validationGroups);
	
	/**
	 * Returns form element that invoked the TDI AJAX request, filled with request data (and validation messages);
	 * or {@code null} of no such element exists (e.g. given request is not an TDI AJAX request).
	 * Configured or default locale and time zone is used.
	 * @param requestParams
	 * @param validationGroups
	 * @return filled source element
	 */
	FormElement<?> fillTdiAjaxSrcElement(RequestParams requestParams, Class<?>... validationGroups);
	
	FormData<T> bind(RequestParams paramsProvider, Location loc, RequestContext ctx, Class<?>... validationGroups);
	FormData<T> bind(RequestParams paramsProvider, RequestContext ctx, Class<?>... validationGroups);
	
	/**
	 * Binds data from request to provided instance and validates data of the form.
	 * This variant of bind method is not recommended because the provided instance is filled from request even when the form 
	 * is not valid (first it is filled and then validated). You are encouraged to use the default "functional" variant od bind method 
	 * without providing your instance and copy/store resulting data only 
	 * when the form is in valid state (use FormData.isValid to check this).
	 * @param paramsProvider
	 * @param instance
	 * @param ctx
	 * @param validationGroups
	 * @return
	 */
	FormData<T> bind(RequestParams paramsProvider, T instance, RequestContext ctx, Class<?>... validationGroups);
	
	/**
	 * <p>Binds data from request to provided instance and validates data of the form.</p>
	 * <p>This variant of bind method is not recommended because the provided instance is filled from request even when the form 
	 * is not valid (first it is filled and then validated). You are encouraged to use the default "functional" variant od bind method 
	 * without providing your instance and copy/store resulting data only 
	 * when the form is in valid state (use FormData.isValid to check this).</p>
	 * @param paramsProvider
	 * @param loc local settings for formatting values from/to strings
	 * @param instance
	 * @param ctx
	 * @param validationGroups
	 * @return
	 */
	FormData<T> bind(RequestParams paramsProvider, Location loc, T instance, RequestContext ctx, Class<?>... validationGroups);
	
	/**
	 * Binds and validates data from the form.
	 * @param paramsProvider provider of request parameters
	 * @param loc local settings for formatting values from/to strings
	 * @param validationGroups the group or list of groups targeted for validation (defaults to implicit 
	 * {@link Default} group, but other groups (interfaces) can be created - extended or not extended
	 * from {@link Default} - and their classes used in attribute "groups" of validation annotations)
	 * @return
	 */
	FormData<T> bind(RequestParams paramsProvider, Location loc, Class<?>... validationGroups);
	
	/**
	 * Binds and validates data from the form. Configured or default locale and time zone is used.
	 * @param paramsProvider provider of request parameters
	 * @param validationGroups the group or list of groups targeted for validation (defaults to implicit 
	 * {@link Default} group, but other groups (interfaces) can be created - extended or not extended
	 * from {@link Default} - and their classes used in attribute "groups" of validation annotations)
	 * @return
	 */
	FormData<T> bind(RequestParams paramsProvider, Class<?>... validationGroups);
	
	/**
	 * Binds data from request to provided instance and validates data of the form.
	 * This variant of bind method is not recommended because the provided instance is filled from request even when the form 
	 * is not valid (first it is filled and then validated). You are encouraged to use the default "functional" variant od bind method 
	 * without providing your instance and copy/store resulting data only 
	 * when the form is in valid state (use FormData.isValid to check this).
	 * @param paramsProvider provider of request parameters
	 * @param loc local settings for formatting values from/to strings
	 * @param instance instance to which data from the request parameter provider should be bound
	 * @param validationGroups the group or list of groups targeted for validation (defaults to implicit 
	 * {@link Default} group, but other groups (interfaces) can be created - extended or not extended
	 * from {@link Default} - and their classes used in attribute "groups" of validation annotations)
	 * @return
	 */
	FormData<T> bind(RequestParams paramsProvider, Location loc, T instance, Class<?>... validationGroups);
	
	/**
	 * Binds and validates data from the form. Configured or default locale and time zone is used.
	 * @param paramsProvider provider of request parameters
	 * @param instance instance to which data from the request parameter provider should be bound
	 * @param validationGroups the group or list of groups targeted for validation (defaults to implicit 
	 * {@link Default} group, but other groups (interfaces) can be created - extended or not extended
	 * from {@link Default} - and their classes used in attribute "groups" of validation annotations)
	 * @return
	 */
	FormData<T> bind(RequestParams paramsProvider, T instance, Class<?>... validationGroups);
	
	/**
	 * Returns form elements (both fields and mappings) nested in this mapping in correct order
	 * (in the same order as the elements were declared in form definition).
	 * @return
	 */
	List<FormElement<?>> getElements();
	
	/**
	 * Fields in this mapping by their property names.
	 * Can be used in template to construct markup of form fields.
	 * @return
	 */
	Map<String, FormField<?>> getFields();
	
	/**
	 * Returns form field with given property name, or {@code null} if such field
	 * does not exist.
	 * @param dataClass
	 * @param propertyName
	 * @return
	 * @throws IllegalStateException if property value cannot be converted to requested class
	 */
	<U> FormField<U> getField(Class<U> dataClass, String propertyName);
	
	/**
	 * Nested mappings of this mapping.
	 * @return
	 */
	Map<String, FormMapping<?>> getNested();
	
	/**
	 * Returns nested mapping with given property name, or {@code null} if such mapping
	 * does not exist.
	 * @param dataClass class of data object for nested mapping
	 * @param propertyName name of property that is managed by nested mapping
	 * @return nested mapping or {@code null}
	 * @throws IllegalStateException if nested mapping's object type cannot be converted to requested class
	 */
	<U> FormMapping<U> getMapping(Class<U> dataClass, String propertyName);
	
	/**
	 * List of mappings for individual filled data items if this is list mapping,
	 * empty list otherwise.
	 * @return
	 */
	List<FormMapping<T>> getList();
	
	/**
	 * Returns copy of this mapping with given order.
	 * @param order
	 * @return
	 */
	FormMapping<T> withOrder(int order);
	
	/**
	 * Returns copy of this mapping that is attached to given parent.
	 * @param parent
	 * @param config
	 * @return
	 */
	FormMapping<T> withParent(FormMapping<?> parent);
	
	/**
	 * Returns true if this mapping is root mapping (without parent).
	 * @return
	 */
	boolean isRootMapping();
	
	/**
	 * Returns string representation of this mapping.
	 * @param indent indentation characters
	 * @return
	 */
	String toString(String indent);
	
	/**
	 * Returns an index if this is filled indexed mapping with an index; {@code null} otherwise.
	 * @return
	 */
	Integer getIndex();
	
	@Override
	FormMappingProperties getProperties();
}
