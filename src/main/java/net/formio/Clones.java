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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.formio.internal.FormUtils;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.ValidationResult;

/**
 * Auxiliary cloning methods.
 * @author Radek Beran
 */
final class Clones {

	/** Creates new instance of map with validation messages. */
	static Map<String, List<ConstraintViolationMessage>> cloneFieldMessages(Map<String, List<ConstraintViolationMessage>> fieldMsgs) {
		Map<String, List<ConstraintViolationMessage>> fieldMsgCopy = new LinkedHashMap<String, List<ConstraintViolationMessage>>();
		for (Map.Entry<String, List<ConstraintViolationMessage>> entry : fieldMsgs.entrySet()) {
			fieldMsgCopy.put(entry.getKey(), new ArrayList<ConstraintViolationMessage>(entry.getValue()));	
		}
		return fieldMsgCopy;
	}
	
	/**
	 * Returns merged validation results.
	 * @param validationResults
	 * @return
	 */
	static ValidationResult mergedValidationResults(Collection<ValidationResult> validationResults) {
		// gather validation messages from nested mappings
		final Map<String, List<ConstraintViolationMessage>> fieldMsgs = new LinkedHashMap<String, List<ConstraintViolationMessage>>();
		final List<ConstraintViolationMessage> globalMsgs = new ArrayList<ConstraintViolationMessage>();
		for (ValidationResult res : validationResults) {
			fieldMsgs.putAll(res.getFieldMessages());
			globalMsgs.addAll(res.getGlobalMessages());
		}
		ValidationResult validationResCopy = new ValidationResult(fieldMsgs, globalMsgs);
		return validationResCopy;
	}
	
	static Map<String, FormField<?>> fieldsWithPrependedPathPrefix(
		Map<String, FormField<?>> fields, String pathPrefix) {
		Map<String, FormField<?>> newFields = new LinkedHashMap<String, FormField<?>>();
		for (Map.Entry<String, FormField<?>> e : fields.entrySet()) {
			// copy of field with given prefix prepended
			FormField<?> field = createFormField(pathPrefix, e.getValue()); // copy constructor
			if (!field.getName().startsWith(pathPrefix + Forms.PATH_SEP))
				throw new IllegalStateException("Field name '" + field.getName() + "' must start with prefix '" + pathPrefix + ".'");
			newFields.put(e.getKey(), field); // key must be a simple property name (it is not changing)
		}
		return Collections.unmodifiableMap(newFields);
	}
	
	static Map<String, FormField<?>> fieldsWithIndexAfterPathPrefix(
		Map<String, FormField<?>> fields, int index, String pathPrefix) {
		Map<String, FormField<?>> newFields = new LinkedHashMap<String, FormField<?>>();
		for (Map.Entry<String, FormField<?>> e : fields.entrySet()) {
			FormField<?> field = createFormField(index, pathPrefix, e.getValue()); // copy constructor
			newFields.put(e.getKey(), field); // key must be a simple property name (it is not changing)
		}
		return Collections.unmodifiableMap(newFields);
	}
	
	static Map<String, FormMapping<?>> mappingsWithIndexAfterPathPrefix(
		Map<String, FormMapping<?>> mappings, int index, String pathPrefix) {
		Map<String, FormMapping<?>> newMappings = new LinkedHashMap<String, FormMapping<?>>();
		for (Map.Entry<String, FormMapping<?>> e : mappings.entrySet()) {
			newMappings.put(e.getKey(), e.getValue().withIndexAfterPathPrefix(index, pathPrefix));
		}
		return Collections.unmodifiableMap(newMappings);
	}
	
	static Map<String, FormMapping<?>> mappingsWithPrependedPathPrefix(
		Map<String, FormMapping<?>> mappings, String pathPrefix) {
		final Map<String, FormMapping<?>> newMappings = new LinkedHashMap<String, FormMapping<?>>();
		for (Map.Entry<String, FormMapping<?>> e : mappings.entrySet()) {
			newMappings.put(e.getKey(), e.getValue().withPathPrefix(pathPrefix, e.getValue().getOrder()));
		}
		return Collections.unmodifiableMap(newMappings);
	}
	
	/**
	 * Returns copies of nested mappings that are attached to parent.
	 * @param nestedMappings
	 * @param outerClass
	 * @param outerConfig
	 * @return
	 */
	static <T> Map<String, FormMapping<?>> mappingsWithParent(FormMapping<?> parent, Map<String, FormMapping<?>> nestedMappings, Class<T> outerClass, Config outerConfig) {
		Map<String, FormMapping<?>> newNestedMappings = new LinkedHashMap<String, FormMapping<?>>();
		for (Map.Entry<String, FormMapping<?>> e : nestedMappings.entrySet()) {
			final String propertyName = e.getKey();
			final FormMapping<?> nestedMapping = e.getValue();
			final boolean requiredProp = outerConfig.getBeanValidator().isRequired(outerClass, propertyName);
			// put copy of nested form mapping that is newly attached to the parent mapping
			newNestedMappings.put(propertyName, nestedMapping.withParent(parent, requiredProp));
		}
		return Collections.unmodifiableMap(newNestedMappings);
	}
	
	/**
	 * Returns copy of form fields that are updated with static information from configuration
	 * (like required flags). 
	 * @param parent
	 * @param srcFields
	 * @param cfg
	 * @param dataClass
	 * @return
	 */
	static <T> Map<String, FormField<?>> fieldsWithParent(FormMapping<?> parent, Map<String, FormField<?>> srcFields, Config cfg, Class<T> dataClass) {
		if (dataClass == null) throw new IllegalStateException("data class cannot be null");
		
		Map<String, FormField<?>> fields = new LinkedHashMap<String, FormField<?>>();
		if (srcFields != null) {
			for (Map.Entry<String, FormField<?>> e : srcFields.entrySet()) {
				FormField<?> f = fieldWithParent(parent, cfg, e.getKey(), e.getValue(), dataClass);
				fields.put(e.getKey(), f);
			}
		}
		return Collections.unmodifiableMap(fields);
	}
	
	private static <T, U> FormField<U> fieldWithParent(FormMapping<?> parent, Config cfg, String propertyName, FormField<U> field, Class<T> dataClass) {
		Boolean required = null; // not specified
		if (cfg.getBeanValidator().isRequired(dataClass, propertyName)) {
			required = Boolean.TRUE;
		} // else not specified, required remains null
		return new FormFieldImpl<U>(field, parent, required);
	}
	
	private static <U> FormField<U> createFormField(int index, String pathPrefix, FormField<U> fld) {
		return new FormFieldImpl<U>(fld, fld.getParent(), index, pathPrefix);
	}
	
	private static <U> FormField<U> createFormField(String pathPrefix, FormField<U> fld) {
		if (pathPrefix == null || pathPrefix.isEmpty()) {
			throw new IllegalArgumentException("pathPrefix cannot be empty");
		}
		if (fld.getName().startsWith(pathPrefix + Forms.PATH_SEP) || fld.getName().equals(pathPrefix)) {
			throw new IllegalStateException("Field's name '" + fld.getName() + "' already starts with prefix '" + pathPrefix + "'");
		}
		String lastName = FormUtils.fieldNameToLastPropertyName(pathPrefix);
		if (lastName != null && !lastName.isEmpty()) {
			if (fld.getName().startsWith(lastName + Forms.PATH_SEP) || fld.getName().equals(lastName)) {
				throw new IllegalStateException("Field's name '" + fld.getName() + "' already starts with property name '" + lastName + "'");
			}
		}
		return new FormFieldImpl<U>(fld, fld.getParent(), pathPrefix, fld.getOrder());
	}
	
	private Clones() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
