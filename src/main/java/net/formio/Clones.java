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
	
	private Clones() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
