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

import net.formio.Field;
import net.formio.FormElement;
import net.formio.FormField;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.validation.ConstraintViolationMessage;

/**
 * {@link BasicFormRenderer} that renders also the whole HTML page or whole form tag
 * for debug purposes.
 * 
 * @author Radek Beran
 */
public class WholeFormRenderer extends BasicFormRenderer {
	
	private final BasicFormRenderer inner;
	
	public WholeFormRenderer(BasicFormRenderer wrapped) {
		// TODO: Extract BasicFormRendererWrapper to the separate class and extend it
		super(wrapped.getRenderContext());
		this.inner = wrapped;
	}
	
	public <T> String renderHtmlFormPage(FormMapping<T> formMapping) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>" + newLine());
		sb.append("<html lang=\"en\">" + newLine());
		sb.append("<head>" + newLine());
		sb.append("<meta charset=\"utf-8\">" + newLine());
		sb.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" + newLine());
		sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" + newLine());
		sb.append("<title>Form rendering test</title>" + newLine());

		// Bootstrap CSS and JavaScript
		sb.append("<!-- Latest compiled and minified CSS -->" + newLine());
		sb.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css\">" + newLine());

		sb.append("<!-- JQuery UI for datepicker -->" + newLine());
		sb.append("<link rel=\"stylesheet\" href=\"http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/ui-lightness/jquery-ui.min.css\">");
		sb.append("<!-- Optional theme -->" + newLine());
		sb.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css\">" + newLine());

		sb.append("<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->" + newLine());
		sb.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js\"></script>" + newLine());
		sb.append("<!-- jQuery UI -->" + newLine());
		sb.append("<script src=\"http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js\"></script>" + newLine());
		sb.append("<!-- Latest compiled and minified JavaScript -->" + newLine());
		sb.append("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js\"></script>" + newLine());

		sb.append("<script>" + newLine());
		sb.append("$(function(){" + newLine());
		sb.append("	$.datepicker.setDefaults(" + newLine());
		sb.append("	  $.extend($.datepicker.regional[''])" + newLine());
		sb.append(");" + newLine());
		sb.append("});" + newLine());
		sb.append("</script>" + newLine());

		sb.append("</head>" + newLine());
		sb.append("<body style=\"margin:1em\">" + newLine());

		sb.append(renderHtmlForm(formMapping));

		sb.append("</body>" + newLine());
		sb.append("</html>" + newLine());
		return sb.toString();
	}

	public <T> String renderHtmlForm(FormMapping<T> formMapping) {
		StringBuilder sb = new StringBuilder();
		sb.append("<form action=\"" + getRenderContext().getActionUrl() + 
			"\" method=\"" + getRenderContext().getMethod().name() + 
			"\" class=\"form-horizontal\" role=\"form\">" + newLine());
		if (formMapping.isVisible()) {
			sb.append(renderMarkupGlobalMessages(formMapping));
			sb.append(renderElement(formMapping));
			if (!containsSubmitButton(formMapping)) {
				sb.append(renderDefaultSubmitButton());
			}
		}
		sb.append("</form>" + newLine());
		return sb.toString();
	}
	
	protected <T> boolean containsSubmitButton(FormMapping<T> mapping) {
		// searching only on top level
		for (FormField<?> field : mapping.getFields().values()) {
			if (Field.SUBMIT_BUTTON.getType().equals(field.getType())) {
				return true;
			}
		}
		return false;
	}
	
	protected String renderDefaultSubmitButton() {
		return renderFieldSubmitButton(Forms.<String> field(
			PROPERTY_DEFAULT_SUBMIT,
			Field.SUBMIT_BUTTON.getType()).build());
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
		return inner.renderElement(element);
	}

	/**
	 * Renders the element itself without the surrounding placeholder tag.
	 * If the given element is invisible, returns empty string.
	 * @param element
	 * @return
	 */
	@Override
	public <T> String renderElementMarkup(FormElement<T> element) {
		return inner.renderElementMarkup(element);
	}
		
	/**
	 * Renders element in visible state (assuming the element is visible).
	 * @param element
	 * @return
	 */
	@Override
	public <T> String renderVisibleElement(FormElement<T> element) {
		return inner.renderVisibleElement(element);
	}

	/**
	 * Renders form mapping in visible state (assuming the mapping is visible).
	 * @param mapping
	 * @return
	 */
	@Override
	public <T> String renderVisibleMapping(FormMapping<T> mapping) {
		return inner.renderVisibleMapping(mapping);
	}

	/**
	 * Renders form field in visible state (assuming the field is visible).
	 * @param field
	 * @return
	 */
	@Override
	public <T> String renderVisibleField(FormField<T> field) {
		return inner.renderVisibleField(field);
	}

	/**
	 * Creates builder of AJAX response.
	 * @return
	 */
	@Override
	public TdiResponseBuilder ajaxResponse() {
		return inner.ajaxResponse();
	}
	
	/**
	 * Returns value of id attribute for given element.
	 * @param element
	 * @return
	 */
	@Override
	public <T> String getElementId(FormElement<T> element) {
		return inner.getElementId(element);
	}
	
	/**
	 * Returns value of id attribute for given form field name.
	 * @param name
	 * @return
	 */
	@Override
	public String getIdForName(String name) {
		return inner.getIdForName(name);
	}
	
	/**
	 * Returns value of id attribute for given form field with given index (order).
	 * @param field
	 * @param index
	 * @return
	 */
	@Override
	protected <T> String getElementIdWithIndex(FormField<T> field, int index) {
		return inner.getElementIdWithIndex(field, index);
	}
	
	/**
	 * Returns id of placeholder element for given form element.
	 * @param element
	 * @return
	 */
	@Override
	protected <T> String getElementPlaceholderId(FormElement<T> element) {
		return inner.getElementPlaceholderId(element);
	}
	
	/**
	 * Returns id of placeholder element for given name of form field/form mapping.
	 * @param name
	 * @return
	 */
	@Override
	protected String getElementPlaceholderId(String name) {
		return inner.getElementPlaceholderId(name);
	}
	
	@Override
	protected <T> String renderMarkupElementPlaceholder(FormElement<T> element, String innerMarkup) {
		return inner.renderMarkupElementPlaceholder(element, innerMarkup);
	}

	@Override
	protected <T> String renderMarkupMappingBox(FormMapping<T> mapping, String innerMarkup) {
		return inner.renderMarkupMappingBox(mapping, innerMarkup);
	}
	
	@Override
	protected <T> String renderMarkupFieldBox(FormField<T> field, String innerMarkup) {
		return inner.renderMarkupFieldBox(field, innerMarkup);
	}

	@Override
	protected <T> String renderMarkupInputEnvelope(FormField<T> field, String innerMarkup) {
		return inner.renderMarkupInputEnvelope(field, innerMarkup);
	}
	
	@Override
	public <T> String renderMarkupGlobalMessages(FormMapping<T> formMapping) {
		return inner.renderMarkupGlobalMessages(formMapping);
	}

	@Override
	protected <T> String renderMarkupMessageList(FormElement<T> element) {
		return inner.renderMarkupMessageList(element);
	}

	@Override
	protected String renderMarkupMessage(ConstraintViolationMessage msg) {
		return inner.renderMarkupMessage(msg);
	}

	@Override
	protected <T> String renderMarkupMappingLabel(FormMapping<T> mapping) {
		return inner.renderMarkupMappingLabel(mapping);
	}

	@Override
	protected <T> String renderMarkupFieldLabel(FormField<T> field) {
		return inner.renderMarkupFieldLabel(field);
	}
	
	@Override
	protected <T> String renderMarkupTextArea(FormField<T> field) {
		return inner.renderMarkupTextArea(field);
	}

	@Override
	protected <T> String renderMarkupInput(FormField<T> field) {
		return inner.renderMarkupInput(field);
	}

	@Override
	protected <T> String renderMarkupCheckBox(FormField<T> field) {
		return inner.renderMarkupCheckBox(field);
	}

	@Override
	protected <T> String renderMarkupSelect(FormField<T> field, boolean multiple, Integer size) {
		return inner.renderMarkupSelect(field, multiple, size);
	}

	@Override
	protected <T> String renderMarkupChecks(FormField<T> field) {
		return inner.renderMarkupChecks(field);
	}
	
	@Override
	protected String renderMarkupOption(String value, String title, boolean selected) {
		return inner.renderMarkupOption(value, title, selected);
	}
	
	@Override
	protected <T> String renderMarkupButton(FormField<T> field) {
		return inner.renderMarkupButton(field);
	}
	
	/**
	 * Returns string will all HTML attributes of given element. 
	 * @param element
	 * @return
	 */
	@Override
	protected <T> String getElementAttributes(FormElement<T> element) {
		return inner.getElementAttributes(element);
	}

	@Override
	protected <T> String getAccessibilityAttributes(FormElement<T> element) {
		return inner.getAccessibilityAttributes(element);
	}

	/**
	 * Returns AJAX attributes of TDI library.
	 * @param element
	 * @return
	 */
	@Override
	protected <T> String getAjaxAttributes(FormElement<T> element) {
		return inner.getAjaxAttributes(element);
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
		return inner.getInputPlaceholderAttribute(field);
	}

	@Override
	protected <T> String renderDatePickerScript(FormField<T> field) {
		return inner.renderDatePickerScript(field);
	}

	@Override
	protected <T> String renderTextFieldInternal(FormField<T> field) {
		return inner.renderTextFieldInternal(field);
	}

	// --- Various field types - begin ---

	@Override
	protected <T> String renderFieldSubmitButton(FormField<T> field) {
		return inner.renderFieldSubmitButton(field);
	}

	@Override
	protected <T> String renderFieldHidden(FormField<T> field) {
		return inner.renderFieldHidden(field);
	}

	@Override
	protected <T> String renderFieldText(FormField<T> field) {
		return inner.renderFieldText(field);
	}

	@Override
	protected <T> String renderFieldColor(FormField<T> field) {
		return inner.renderFieldColor(field);
	}

	@Override
	protected <T> String renderFieldDate(FormField<T> field) {
		return inner.renderFieldDate(field);
	}

	@Override
	protected <T> String renderFieldDateTime(FormField<T> field) {
		return inner.renderFieldDateTime(field);
	}

	@Override
	protected <T> String renderFieldDateTimeLocal(FormField<T> field) {
		return inner.renderFieldDateTimeLocal(field);
	}

	@Override
	protected <T> String renderFieldTime(FormField<T> field) {
		return inner.renderFieldTime(field);
	}

	@Override
	protected <T> String renderFieldEmail(FormField<T> field) {
		return inner.renderFieldEmail(field);
	}

	@Override
	protected <T> String renderFieldMonth(FormField<T> field) {
		return inner.renderFieldMonth(field);
	}

	@Override
	protected <T> String renderFieldNumber(FormField<T> field) {
		return inner.renderFieldNumber(field);
	}

	@Override
	protected <T> String renderFieldRange(FormField<T> field) {
		return inner.renderFieldRange(field);
	}

	@Override
	protected <T> String renderFieldSearch(FormField<T> field) {
		return inner.renderFieldSearch(field);
	}

	@Override
	protected <T> String renderFieldTel(FormField<T> field) {
		return inner.renderFieldTel(field);
	}

	@Override
	protected <T> String renderFieldUrl(FormField<T> field) {
		return inner.renderFieldUrl(field);
	}

	@Override
	protected <T> String renderFieldWeek(FormField<T> field) {
		return inner.renderFieldWeek(field);
	}

	@Override
	protected <T> String renderFieldTextArea(FormField<T> field) {
		return inner.renderFieldTextArea(field);
	}

	@Override
	protected <T> String renderFieldCheckBox(FormField<T> field) {
		return inner.renderFieldCheckBox(field);
	}

	@Override
	protected <T> String renderFieldPassword(FormField<T> field) {
		return inner.renderFieldPassword(field);
	}

	@Override
	protected <T> String renderFieldFileUpload(FormField<T> field) {
		return inner.renderFieldFileUpload(field);
	}

	@Override
	protected <T> String renderFieldDatePicker(FormField<T> field) {
		return inner.renderFieldDatePicker(field);
	}

	@Override
	protected <T> String renderFieldDropDownChoice(FormField<T> field) {
		return inner.renderFieldDropDownChoice(field);
	}

	@Override
	protected <T> String renderFieldMultipleChoice(FormField<T> field) {
		return inner.renderFieldMultipleChoice(field);
	}

	@Override
	protected <T> String renderFieldMultipleCheckbox(FormField<T> field) {
		return inner.renderFieldMultipleCheckbox(field);
	}

	@Override
	protected <T> String renderFieldRadioChoice(FormField<T> field) {
		return inner.renderFieldRadioChoice(field);
	}

	// --- /Various field types - end ---

	@Override
	protected <T> String getLabelText(FormElement<T> element) {
		return inner.getLabelText(element);
	}
	
	@Override
	protected <T> String getRequiredMark(FormElement<T> element) {
		return inner.getRequiredMark(element);
	}
	
	private String newLine() {
		return System.getProperty("line.separator");
	}
	
	private final String PROPERTY_DEFAULT_SUBMIT = "_defaultSubmitButton";
}
