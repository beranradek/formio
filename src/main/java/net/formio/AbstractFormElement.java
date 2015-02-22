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

import java.util.ArrayList;
import java.util.List;

import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.Validator;

/**
 * Common implementations of {@link FormElement}'s methods.
 * @author Radek Beran
 */
abstract class AbstractFormElement<T> implements FormElement<T> {
	
	final FormMapping<?> parent;
	final String propertyName;
	final List<Validator<T>> validators;
	
	AbstractFormElement(FormMapping<?> parent, String propertyName, List<Validator<T>> validators) {
		this.parent = parent;
		this.propertyName = propertyName;
		if (validators == null) {
			throw new IllegalArgumentException("validators cannot be null");
		}
		this.validators = validators; 
	}
	
	/**
	 * Returns validation messages of form element.
	 * @return
	 */
	@Override
	public List<ConstraintViolationMessage> getValidationMessages() {
		List<ConstraintViolationMessage> msgs = null;
		if (getValidationResult() != null) {
			msgs = getValidationResult().getFieldMessages().get(getName());
		}
		if (msgs == null) {
			msgs = new ArrayList<ConstraintViolationMessage>();
		}
		return msgs;
	}
	
	/**
	 * Returns true if given form element is visible.
	 * @return
	 */
	@Override
	public boolean isVisible() {
		boolean visible = getFormProperties().isVisible();
		if (getParent() != null && !getParent().isVisible()) {
			visible = false;
		}
		return visible;
	}
	
	/**
	 * Returns true if given form element is enabled.
	 * @return
	 */
	@Override
	public boolean isEnabled() {
		boolean enabled = getFormProperties().isEnabled();
		if (getParent() != null && !getParent().isEnabled()) {
			enabled = false;
		}
		return enabled;
	}
	
	/**
	 * Returns true if given form element is readonly.
	 * @return
	 */
	@Override
	public boolean isReadonly() {
		boolean readonly = getFormProperties().isReadonly();
		if (getParent() != null && getParent().isReadonly()) {
			readonly = true;
		}
		return readonly;
	}
	
	@Override
	public List<Validator<T>> getValidators() {
		return validators;
	}
}
