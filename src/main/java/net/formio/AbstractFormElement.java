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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.formio.internal.FormUtils;
import net.formio.render.RenderUtils;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.Severity;
import net.formio.validation.Validator;
import net.formio.validation.constraints.NotEmpty;
import net.formio.validation.validators.RequiredValidator;

/**
 * Common implementations of {@link FormElement}'s methods.
 * @author Radek Beran
 */
public abstract class AbstractFormElement<T> implements FormElement<T> {
	// public because of introspection required by some template frameworks, constructors are not public
	
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
		boolean visible = getProperties().isVisible();
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
		boolean enabled = getProperties().isEnabled();
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
		boolean readonly = getProperties().isReadonly();
		if (getParent() != null && getParent().isReadonly()) {
			readonly = true;
		}
		return readonly;
	}
	
	@Override
	public List<Validator<T>> getValidators() {
		return validators;
	}
	
	@Override
	public boolean isRequired() {
		Class<?> parentDataClass = null;
		if (parent != null) {
			parentDataClass = parent.getDataClass();
		}
		return isRequired(parentDataClass);
	}
	
	protected boolean isRequired(Class<?> parentDataClass) {
		if (getPropertyName().equals(Forms.AUTH_TOKEN_FIELD_NAME)) {
			return false; // handled specially
		}
		boolean required = false;
		if (parentDataClass != null) {
			try {
				final Field fld = parentDataClass.getDeclaredField(getPropertyName());
				if (fld != null && isRequiredByAnnotations(fld.getAnnotations(), 0)) {
					// isRequiredByAnnotations is intentionally checked first because this
					// also checks if the field exists and throws exception in time of form definition
					// building if not.
					required = true;
				}
			} catch (NoSuchFieldException ex) {
				throw new ReflectionException("Error while checking if property " + getPropertyName() + 
					" of class " + parentDataClass.getName() + 
					" is required, the corresponding field does not exist: " + ex.getMessage(), ex);
			}
		}
		if (validators != null && validators.contains(RequiredValidator.getInstance())) {
			required = true;
		}
		return required;
	}
	
	private boolean isRequiredByAnnotations(Annotation[] annots, int level) {
		boolean required = false;
		if (level < 2) {
			if (annots != null) {
				for (Annotation ann : annots) {
					if (ann instanceof Size) {
						Size s = (Size) ann;
						if (s.min() > 0) {
							required = true;
							break;
						}
					} else if (ann instanceof NotNull) {
						required = true;
						break;
					} else if (ann instanceof NotEmpty) {
						required = true;
						break;
					} else {
						if (isRequiredByAnnotations(ann.annotationType().getAnnotations(), level + 1)) {
							required = true;
							break;
						}
					}
				}
			}
		}
		return required;
	}
	
	@Override
	public <U> FormElement<U> findElement(Class<U> cls, String name) {
		FormElement<U> foundEl = null;
		if (name != null && !name.isEmpty()) {
			if (this.getName().equals(name)) {
				foundEl = (FormElement<U>)this;
			}
			if (foundEl == null) {
				FormMapping<?> root = getRoot();
				if (root != null) {
					foundEl = FormUtils.findElementRecursive(cls, name, root);
				}
			}
		}
		return foundEl;
	}
	
	@Override
	public FormElement<Object> findElement(String name) {
		return findElement(Object.class, name);
	}

	@Override
	public FormMapping<?> getRoot() {
		FormMapping<?> root = null;
		if (this instanceof FormMapping<?>) {
			root = (FormMapping<?>)this;
		} else {
			root = this.getParent();
		}
		if (root != null) {
			while (root.getParent() != null) {
				root = root.getParent();
			}
		}
		return root;
	}
	
	@Override
	public String getElementId() {
		return RenderUtils.getElementIdForName(getName());
	}
	
	@Override
	public String getElementPlaceholderId() {
		return getElementPlaceholderId(getName());
	}
	
	public static String getElementPlaceholderId(String elementName) {
		return "placeholder" + Forms.PATH_SEP + elementName;
	}
	
	@Override
	public String getElementIdWithIndex(int index) {
		return getElementId() + Forms.PATH_SEP + index;
	}
	
	@Override
	public String getMaxSeverityClass() {
		Severity maxSeverity = Severity.max(getValidationMessages());
		return maxSeverity != null ? maxSeverity.getStyleClass() : "";
	}
}
