/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import net.formio.props.FormProperties;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.ValidationResult;
import net.formio.validation.Validator;

/**
 * Element in a form - form field, group of fields (form mapping), ...
 * @author Radek Beran
 */
public interface FormElement<T> {
	
	/**
	 * Parent of this form element.
	 * @return parent of this form element
	 */
	FormMapping<?> getParent();
	
	/**
	 * Root mapping of the form.
	 * @return root mapping
	 */
	FormMapping<?> getRoot();
	
	/**
	 * Name of this form element (full path from outer object to potentially nested property).
	 * It represents an identifier of this element in the form and should be used as value 
	 * for the name attribute in the markup.
	 * @return name of this form element
	 */
	String getName();
	
	/**
	 * Id of this form element (value for id attribute in the markup).
	 * @return id of this form element
	 */
	String getElementId();
	
	/**
	 * Id of placeholder element that is used to reserve place in markup for
	 * this element when the element is not visible etc.
	 * @return id of placeholder element that is generated in markup
	 */
	String getElementPlaceholderId();
	
	/**
	 * Returns id of this form element with given index (order) appended.
	 * @param index
	 * @return id of this form element including index
	 */
	String getElementIdWithIndex(int index);
	
	/**
	 * Name of property to which this form element is bound to.
	 * @return property name
	 */
	String getPropertyName();
	
	/**
	 * Key for the label (derived from name).
	 * Does not contain any brackets with indexes as the name does. 
	 * Useful especially for repeated fields that are part of list mapping
	 * and should have the same labels, but different (unique) indexed names.
	 * @return translation key for label
	 */
	String getLabelKey();
	
	/**
	 * Configuration of this form element.
	 * @return
	 */
	Config getConfig();
	
	/**
	 * Returns properties of this form element.
	 * @return additional properties of this form element 
	 */
	FormProperties getProperties();
	
	/**
	 * Returns ordinal index of this form element.
	 * @return order of this form element 
	 */
	int getOrder();
	
	/**
	 * Returns result with validation messages, {@code null} if form data was not validated yet.
	 * @return validation messages
	 */
	ValidationResult getValidationResult();
	
	/**
	 * Returns validation messages of form element.
	 * @return validation messages from validation result
	 */
	List<ConstraintViolationMessage> getValidationMessages();
	
	/**
	 * Returns the validators used in the validation in addition to bean validation constraints defined
	 * by annotations.
	 * @return validators specified for this form element
	 */
	List<Validator<T>> getValidators();
	
	/**
	 * Returns true if filling this form element is required.
	 * @return true if filling this form element is required
	 */
	boolean isRequired();
	
	/**
	 * Returns true if this field/mapping is visible.
	 * @return visible flag
	 */
	boolean isVisible();
	
	/**
	 * Returns true if this field/mapping is enabled.
	 * @return enabled flag
	 */
	boolean isEnabled();
	
	/**
	 * Returns true if this field/mapping is read-only.
	 * @return readonly flag
	 */
	boolean isReadonly();
	
	/**
	 * Returns CSS class of maximum severity of validation messages
	 * of this form element. 
	 * @return CSS class of maximum severity of validation messages
	 */
	String getMaxSeverityClass();
	
	/**
	 * Returns element with given name, or {@code null}.
	 * @param cls
	 * @param name
	 * @return found element or {@code null}
	 */
	<U> FormElement<U> findElement(Class<U> cls, String name);
	
	/**
	 * Returns element with given name, or {@code null}.
	 * @param name
	 * @return found element or {@code null}
	 */
	FormElement<Object> findElement(String name);
}
