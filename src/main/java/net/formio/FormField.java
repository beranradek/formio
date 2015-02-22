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

import net.formio.choice.ChoiceProvider;
import net.formio.choice.ChoiceRenderer;
import net.formio.format.Formatter;

/**
 * Form field representation. Implementations must be immutable.
 * @param T type of data filled into this form field; field can be filled with one or more values of type T
 * @author Radek Beran
 */
public interface FormField<T> extends FormElement<T>, FormFieldProperties {
	
	/**
	 * Type of form field, for e.g.: text, checkbox, textarea, ..., or {@code null} if not specified.
	 * @return
	 */
	String getType();
	
	/**
	 * Single/first object which this field is filled with.
	 * @return
	 */
	T getFilledObject();
	
	/**
	 * Objects which this field is filled with. There are more objects if this
	 * field represents multivalue field (group of checkboxes, multiselect).
	 * @return
	 */
	List<T> getFilledObjects();
	
	/**
	 * Single/first value of this field in text form.
	 * @return
	 */
	String getValue();

	/**
	 * Formatting pattern for conversion of value to/from string.
	 * @return
	 */
	String getPattern();
	
	/**
	 * Formatter that formats object to String and vice versa.
	 * @return
	 */
	Formatter<T> getFormatter();
	
	/**
	 * Returns provider of all possible codebook items used when rendering this form field;
	 * or {@code null}.
	 * @return
	 */
	ChoiceProvider<T> getChoices();
	
	/**
	 * Returns renderer that prepares ids and titles of items when this
	 * form field represents a choice from the codebook item(s); or {@code null} if this
	 * form field does not represent such a choice.
	 * @return
	 */
	ChoiceRenderer<T> getChoiceRenderer();
	
	/**
	 * Returns view with properties of this form element.
	 * @return
	 */
	@Override
	FormFieldProperties getFormProperties();
}
