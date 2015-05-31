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
package net.formio.props;

import net.formio.props.types.InlinePosition;

/**
 * Definable properties of form field.
 * These properties contain typical accessibility flags like required, enabled, readonly, visible
 * in addition to custom user-defined formProperties.
 * @author Radek Beran
 */
public interface FormFieldProperties extends FormProperties {
	
	/**
	 * Returns true if first "choose option" item should be displayed. 
	 * @return
	 */
	boolean isChooseOptionDisplayed();
	
	/**
	 * Returns text of first "choose option" item's title.
	 * @return
	 */
	String getChooseOptionTitle();
	
	/**
	 * Returns placeholder text shown in the input when the input is empty.
	 * @return
	 */
	String getPlaceholder();
	
	/**
	 * Returns position of inline form field, {@code null} if this is not the inline form field.
	 * @return
	 */
	InlinePosition getInline();
	
	/**
	 * Returns width of input in count of columns.
	 * @return
	 */
	Integer getColInputWidth();
	
	/**
	 * Returns message for confirmation of form field activation (e.g. a button confirm message).
	 * @return
	 */
	String getConfirmMessage();
}
