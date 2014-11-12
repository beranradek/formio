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
package net.formio.debug;

import java.util.List;
import java.util.Locale;

import net.formio.FormField;
import net.formio.FormMapping;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.ValidationResult;

/**
 * Renders form and its parts. 
 * @author Radek Beran
 */
public interface FormRenderer {
	<T> String renderForm(FormMapping<T> filledForm, FormMethod method, String actionUrl, Locale locale);
	
	String renderGlobalMessages(ValidationResult validationResult);
	
	<T> String renderMapping(FormMapping<T> mapping, ParentMappings parentMappings, Locale locale);
	
	<T> String renderField(FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings, Locale locale);
	
	String renderSubmit();
}
