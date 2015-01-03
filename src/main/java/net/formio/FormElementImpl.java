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

/**
 * Common implementations of {@link FormElement}'s methods.
 * @author Radek Beran
 */
class FormElementImpl {
	/**
	 * Returns validation messages of form element.
	 * @param fieldName
	 * @return
	 */
	static List<ConstraintViolationMessage> getValidationMessages(FormElement el) {
		List<ConstraintViolationMessage> msgs = null;
		if (el.getValidationResult() != null) {
			msgs = el.getValidationResult().getFieldMessages().get(el.getName());
		}
		if (msgs == null) {
			msgs = new ArrayList<ConstraintViolationMessage>();
		}
		return msgs;
	}
	
	/**
	 * Returns true if given form element is visible.
	 * @param el
	 * @return
	 */
	static boolean isVisible(FormElement el) {
		boolean visible = el.getFormProperties().isVisible();
		if (el.getParent() != null && !el.getParent().isVisible()) {
			visible = false;
		}
		return visible;
	}
	
	/**
	 * Returns true if given form element is enabled.
	 * @param el
	 * @return
	 */
	static boolean isEnabled(FormElement el) {
		boolean enabled = el.getFormProperties().isEnabled();
		if (el.getParent() != null && !el.getParent().isEnabled()) {
			enabled = false;
		}
		return enabled;
	}
	
	/**
	 * Returns true if given form element is readonly.
	 * @param el
	 * @return
	 */
	static boolean isReadonly(FormElement el) {
		boolean readonly = el.getFormProperties().isReadonly();
		if (el.getParent() != null && el.getParent().isReadonly()) {
			readonly = true;
		}
		return readonly;
	}
	
	private FormElementImpl() {
	}
}
