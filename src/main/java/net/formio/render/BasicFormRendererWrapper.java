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
package net.formio.render;

import net.formio.FormElement;
import net.formio.FormField;
import net.formio.FormMapping;
import net.formio.validation.ConstraintViolationMessage;

/**
 * Wrapper of {@link BasicFormRenderer} that allows convenient implementations that are extending
 * {@link BasicFormRenderer}'s functionality. 
 * 
 * @author Radek Beran
 */
public class BasicFormRendererWrapper extends BasicFormRenderer {
	private final BasicFormRenderer inner;
	
	public BasicFormRendererWrapper(BasicFormRenderer wrapped) {
		super(wrapped.getRenderContext());
		this.inner = wrapped;
	}
	
	/**
	 * <p>Renders given form element - form mapping or form field:</p>
	 * <ul>
	 * 	<li>Surrounding placeholder tag (even if the element is invisible).</li>
	 * 	<li>Form mapping or form field if it is visible (element markup).</li>
	 * </ul>
	 * <p>Visible mapping consists of mapping box with label and nested elements, 
	 * visible form field consists of field box with label and field envelope (with nested form input).</p>
	 * 
	 * @param element
	 * @return
	 */
	@Override
	public <T> String renderElement(FormElement<T> element) {
		return getInnerRenderer().renderElement(element);
	}

	/**
	 * Renders the element itself without the surrounding placeholder tag.
	 * If the given element is invisible, returns empty string.
	 * @param element
	 * @return
	 */
	@Override
	public <T> String renderElementMarkup(FormElement<T> element) {
		return getInnerRenderer().renderElementMarkup(element);
	}
		
	/**
	 * Renders element in visible state (assuming the element is visible).
	 * @param element
	 * @return
	 */
	@Override
	public <T> String renderVisibleElement(FormElement<T> element) {
		return getInnerRenderer().renderVisibleElement(element);
	}

	/**
	 * Renders form mapping in visible state (assuming the mapping is visible).
	 * @param mapping
	 * @return
	 */
	@Override
	public <T> String renderVisibleMapping(FormMapping<T> mapping) {
		return getInnerRenderer().renderVisibleMapping(mapping);
	}

	/**
	 * Renders form field in visible state (assuming the field is visible).
	 * @param field
	 * @return
	 */
	@Override
	public <T> String renderVisibleField(FormField<T> field) {
		return getInnerRenderer().renderVisibleField(field);
	}

	/**
	 * Creates builder of AJAX response.
	 * @return
	 */
	@Override
	public TdiResponseBuilder ajaxResponse() {
		return getInnerRenderer().ajaxResponse();
	}
	
	/**
	 * Returns value of id attribute for given element.
	 * @param element
	 * @return
	 */
	@Override
	public <T> String getElementId(FormElement<T> element) {
		return getInnerRenderer().getElementId(element);
	}
	
	/**
	 * Returns value of id attribute for given form field name.
	 * @param name
	 * @return
	 */
	@Override
	public String getIdForName(String name) {
		return getInnerRenderer().getIdForName(name);
	}
	
	/**
	 * Returns value of id attribute for given form field with given index (order).
	 * @param field
	 * @param index
	 * @return
	 */
	@Override
	protected <T> String getElementIdWithIndex(FormField<T> field, int index) {
		return getInnerRenderer().getElementIdWithIndex(field, index);
	}
	
	/**
	 * Returns id of placeholder element for given form element.
	 * @param element
	 * @return
	 */
	@Override
	protected <T> String getElementPlaceholderId(FormElement<T> element) {
		return getInnerRenderer().getElementPlaceholderId(element);
	}
	
	/**
	 * Returns id of placeholder element for given name of form field/form mapping.
	 * @param name
	 * @return
	 */
	@Override
	protected String getElementPlaceholderId(String name) {
		return getInnerRenderer().getElementPlaceholderId(name);
	}
	
	@Override
	protected <T> String renderMarkupElementPlaceholder(FormElement<T> element, String innerMarkup) {
		return getInnerRenderer().renderMarkupElementPlaceholder(element, innerMarkup);
	}

	@Override
	protected <T> String renderMarkupMappingBox(FormMapping<T> mapping, String innerMarkup) {
		return getInnerRenderer().renderMarkupMappingBox(mapping, innerMarkup);
	}
	
	@Override
	protected <T> String renderMarkupFieldBox(FormField<T> field, String innerMarkup) {
		return getInnerRenderer().renderMarkupFieldBox(field, innerMarkup);
	}

	@Override
	protected <T> String renderMarkupInputEnvelope(FormField<T> field, String innerMarkup) {
		return getInnerRenderer().renderMarkupInputEnvelope(field, innerMarkup);
	}
	
	@Override
	public <T> String renderMarkupGlobalMessages(FormMapping<T> formMapping) {
		return getInnerRenderer().renderMarkupGlobalMessages(formMapping);
	}

	@Override
	protected <T> String renderMarkupMessageList(FormElement<T> element) {
		return getInnerRenderer().renderMarkupMessageList(element);
	}

	@Override
	protected String renderMarkupMessage(ConstraintViolationMessage msg) {
		return getInnerRenderer().renderMarkupMessage(msg);
	}

	@Override
	protected <T> String renderMarkupMappingLabel(FormMapping<T> mapping) {
		return getInnerRenderer().renderMarkupMappingLabel(mapping);
	}

	@Override
	protected <T> String renderMarkupFieldLabel(FormField<T> field) {
		return getInnerRenderer().renderMarkupFieldLabel(field);
	}
	
	@Override
	protected <T> String renderMarkupTextArea(FormField<T> field) {
		return getInnerRenderer().renderMarkupTextArea(field);
	}

	@Override
	protected <T> String renderMarkupInput(FormField<T> field) {
		return getInnerRenderer().renderMarkupInput(field);
	}

	@Override
	protected <T> String renderMarkupCheckBox(FormField<T> field) {
		return getInnerRenderer().renderMarkupCheckBox(field);
	}

	@Override
	protected <T> String renderMarkupSelect(FormField<T> field, boolean multiple, Integer size) {
		return getInnerRenderer().renderMarkupSelect(field, multiple, size);
	}

	@Override
	protected <T> String renderMarkupChecks(FormField<T> field) {
		return getInnerRenderer().renderMarkupChecks(field);
	}
	
	@Override
	protected String renderMarkupOption(String value, String title, boolean selected) {
		return getInnerRenderer().renderMarkupOption(value, title, selected);
	}
	
	@Override
	protected <T> String renderMarkupButton(FormField<T> field) {
		return getInnerRenderer().renderMarkupButton(field);
	}
	
	/**
	 * Returns string will all HTML attributes of given element. 
	 * @param element
	 * @return
	 */
	@Override
	protected <T> String getElementAttributes(FormElement<T> element) {
		return getInnerRenderer().getElementAttributes(element);
	}

	@Override
	protected <T> String getAccessibilityAttributes(FormElement<T> element) {
		return getInnerRenderer().getAccessibilityAttributes(element);
	}

	/**
	 * Returns AJAX attributes of TDI library.
	 * @param element
	 * @return
	 */
	@Override
	protected <T> String getAjaxAttributes(FormElement<T> element) {
		return getInnerRenderer().getAjaxAttributes(element);
	}
	
	/**
	 * Returns placeholder attribute for the input of given form field.
	 * This attribute shows help (placeholder value) inside the form input
	 * before the user fills in his own value. 
	 * @param field
	 * @return
	 */
	@Override
	protected <T> String getInputPlaceholderAttribute(FormField<T> field) {
		return getInnerRenderer().getInputPlaceholderAttribute(field);
	}

	@Override
	protected <T> String renderDatePickerScript(FormField<T> field) {
		return getInnerRenderer().renderDatePickerScript(field);
	}

	@Override
	protected <T> String renderTextFieldInternal(FormField<T> field) {
		return getInnerRenderer().renderTextFieldInternal(field);
	}

	// --- Various field types - begin ---

	@Override
	protected <T> String renderFieldSubmitButton(FormField<T> field) {
		return getInnerRenderer().renderFieldSubmitButton(field);
	}

	@Override
	protected <T> String renderFieldHidden(FormField<T> field) {
		return getInnerRenderer().renderFieldHidden(field);
	}

	@Override
	protected <T> String renderFieldText(FormField<T> field) {
		return getInnerRenderer().renderFieldText(field);
	}

	@Override
	protected <T> String renderFieldColor(FormField<T> field) {
		return getInnerRenderer().renderFieldColor(field);
	}

	@Override
	protected <T> String renderFieldDate(FormField<T> field) {
		return getInnerRenderer().renderFieldDate(field);
	}

	@Override
	protected <T> String renderFieldDateTime(FormField<T> field) {
		return getInnerRenderer().renderFieldDateTime(field);
	}

	@Override
	protected <T> String renderFieldDateTimeLocal(FormField<T> field) {
		return getInnerRenderer().renderFieldDateTimeLocal(field);
	}

	@Override
	protected <T> String renderFieldTime(FormField<T> field) {
		return getInnerRenderer().renderFieldTime(field);
	}

	@Override
	protected <T> String renderFieldEmail(FormField<T> field) {
		return getInnerRenderer().renderFieldEmail(field);
	}

	@Override
	protected <T> String renderFieldMonth(FormField<T> field) {
		return getInnerRenderer().renderFieldMonth(field);
	}

	@Override
	protected <T> String renderFieldNumber(FormField<T> field) {
		return getInnerRenderer().renderFieldNumber(field);
	}

	@Override
	protected <T> String renderFieldRange(FormField<T> field) {
		return getInnerRenderer().renderFieldRange(field);
	}

	@Override
	protected <T> String renderFieldSearch(FormField<T> field) {
		return getInnerRenderer().renderFieldSearch(field);
	}

	@Override
	protected <T> String renderFieldTel(FormField<T> field) {
		return getInnerRenderer().renderFieldTel(field);
	}

	@Override
	protected <T> String renderFieldUrl(FormField<T> field) {
		return getInnerRenderer().renderFieldUrl(field);
	}

	@Override
	protected <T> String renderFieldWeek(FormField<T> field) {
		return getInnerRenderer().renderFieldWeek(field);
	}

	@Override
	protected <T> String renderFieldTextArea(FormField<T> field) {
		return getInnerRenderer().renderFieldTextArea(field);
	}

	@Override
	protected <T> String renderFieldCheckBox(FormField<T> field) {
		return getInnerRenderer().renderFieldCheckBox(field);
	}

	@Override
	protected <T> String renderFieldPassword(FormField<T> field) {
		return getInnerRenderer().renderFieldPassword(field);
	}

	@Override
	protected <T> String renderFieldFileUpload(FormField<T> field) {
		return getInnerRenderer().renderFieldFileUpload(field);
	}

	@Override
	protected <T> String renderFieldDatePicker(FormField<T> field) {
		return getInnerRenderer().renderFieldDatePicker(field);
	}

	@Override
	protected <T> String renderFieldDropDownChoice(FormField<T> field) {
		return getInnerRenderer().renderFieldDropDownChoice(field);
	}

	@Override
	protected <T> String renderFieldMultipleChoice(FormField<T> field) {
		return getInnerRenderer().renderFieldMultipleChoice(field);
	}

	@Override
	protected <T> String renderFieldMultipleCheckbox(FormField<T> field) {
		return getInnerRenderer().renderFieldMultipleCheckbox(field);
	}

	@Override
	protected <T> String renderFieldRadioChoice(FormField<T> field) {
		return getInnerRenderer().renderFieldRadioChoice(field);
	}

	// --- /Various field types - end ---

	@Override
	protected <T> String getLabelText(FormElement<T> element) {
		return getInnerRenderer().getLabelText(element);
	}
	
	@Override
	protected <T> String getRequiredMark(FormElement<T> element) {
		return getInnerRenderer().getRequiredMark(element);
	}
	
	protected BasicFormRenderer getInnerRenderer() {
		return inner;
	}
}
