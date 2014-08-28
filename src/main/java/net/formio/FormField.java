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

import net.formio.common.heterog.HeterogMap;
import net.formio.format.Formatter;

/**
 * Form field representation. All implementations are immutable.
 * @author Radek Beran
 */
public interface FormField<T> {

	/**
	 * Name of this field (full path from outer object to potentially nested property).
	 * @return
	 */
	String getName();
	
	/**
	 * Type of form field, for e.g.: text, checkbox, textarea, ..., or {@code null} if not specified.
	 * @return
	 */
	String getType();
	
	/**
	 * Key for the label (derived from name).
	 * Does not contain any brackets with indexes as the name does. 
	 * Useful especially for repeated fields that are part of list mapping
	 * and should have the same labels, but different (unique) indexed names.
	 * @return
	 */
	String getLabelKey();
	
	/**
	 * Single/first object which this field is filled with.
	 * @return
	 */
	T getFilledObject();
	
	/**
	 * Returns true if this field is required.
	 * @return
	 */
	boolean isRequired();
	
	/**
	 * Returns properties (flags) of this form field.
	 * @return
	 */
	HeterogMap<String> getProperties();
	
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

}
