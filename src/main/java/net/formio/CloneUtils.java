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
final class CloneUtils {

	/** Creates new instance of map with validation messages. */
	static Map<String, List<ConstraintViolationMessage>> cloneFieldMessages(Map<String, List<ConstraintViolationMessage>> fieldMsgs) {
		Map<String, List<ConstraintViolationMessage>> fieldMsgCopy = new LinkedHashMap<String, List<ConstraintViolationMessage>>();
		for (Map.Entry<String, List<ConstraintViolationMessage>> entry : fieldMsgs.entrySet()) {
			fieldMsgCopy.put(entry.getKey(), new ArrayList<ConstraintViolationMessage>(entry.getValue()));	
		}
		return fieldMsgCopy;
	}
	
	static ValidationResult mergedValidationResults(
		ValidationResult validationRes,
		Map<String, FormData<?>> nestedFormData) {
		
		final Map<String, List<ConstraintViolationMessage>> fieldMsgsCopy = cloneFieldMessages(validationRes.getFieldMessages());
		
		// gather validation messages from nested mappings
		List<ConstraintViolationMessage> globalMsgsCopy = new ArrayList<ConstraintViolationMessage>(validationRes.getGlobalMessages());
		for (FormData<?> formData : nestedFormData.values()) {
			fieldMsgsCopy.putAll(formData.getValidationResult().getFieldMessages());
			globalMsgsCopy.addAll(formData.getValidationResult().getGlobalMessages());
		}
		ValidationResult validationResCopy = new ValidationResult(fieldMsgsCopy, globalMsgsCopy);
		return validationResCopy;
	}
	
	static Map<String, FormField<?>> fieldsWithPrependedPathPrefix(
		Map<String, FormField<?>> fields, String pathPrefix, String mappingPath) {
		Map<String, FormField<?>> newFields = new LinkedHashMap<String, FormField<?>>();
		for (Map.Entry<String, FormField<?>> e : fields.entrySet()) {
			// copy of field with given prefix prepended
			FormField<?> field = createFormField(pathPrefix, e.getValue()); // copy constructor
			if (!field.getName().startsWith(mappingPath + Forms.PATH_SEP))
				throw new IllegalStateException("Field name '" + field.getName() + "' must start with prefix '" + mappingPath + ".'");
			newFields.put(e.getKey(), field); // key must be a simple property name (it is not changing)
		}
		return Collections.unmodifiableMap(newFields);
	}
	
	static Map<String, FormMapping<?>> mappingsWithPrependedPathPrefix(
		Map<String, FormMapping<?>> mappings, String pathPrefix) {
		final Map<String, FormMapping<?>> newMappings = new LinkedHashMap<String, FormMapping<?>>();
		for (Map.Entry<String, FormMapping<?>> e : mappings.entrySet()) {
			newMappings.put(e.getKey(), e.getValue().withPathPrefix(pathPrefix));
		}
		return Collections.unmodifiableMap(newMappings);
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
	
	private static <U> FormField<U> createFormField(int index, String pathPrefix, FormField<U> fld) {
		return new FormFieldImpl<U>(fld, index, pathPrefix);
	}
	
	private static <U> FormField<U> createFormField(String pathPrefix, FormField<U> fld) {
		return new FormFieldImpl<U>(fld, pathPrefix);
	}
	
	private CloneUtils() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
