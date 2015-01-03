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

import java.util.List;

import net.formio.BasicListFormMapping;
import net.formio.FormElement;
import net.formio.FormField;
import net.formio.FormFieldType;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.choice.ChoiceRenderer;
import net.formio.common.MessageTranslator;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.Severity;
import net.formio.validation.ValidationResult;

/**
 * Convenience basic implementation of {@link FormRenderer}.
 * @author Radek Beran
 */
public class BasicFormRenderer {
	
	public <T> String renderHtmlPage(RenderContext<T> ctx) {
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

		sb.append(renderForm(ctx));
		
		sb.append("</body>" + newLine());
		sb.append("</html>" + newLine());
		return sb.toString();
	}

	public <T> String renderForm(RenderContext<T> ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append(renderFormBegin(ctx));
		sb.append(renderGlobalMessages(ctx));
		sb.append(renderMapping(ctx, ctx.getFilledForm()));
		if (!containsSubmitButton(ctx.getFilledForm())) {
			sb.append(renderDefaultSubmitButton(ctx));
		}
		sb.append(renderFormEnd(ctx));
		return sb.toString();
	}
	
	public <T> String renderGlobalMessages(RenderContext<T> ctx) {
		StringBuilder sb = new StringBuilder();
		ValidationResult validationResult = ctx.getFilledForm().getValidationResult();
		if (!validationResult.isEmpty() && !validationResult.isSuccess()) {
			sb.append("<div class=\"alert alert-danger\">" + newLine());
			sb.append("<div>Form contains validation errors.</div>" + newLine());
			for (ConstraintViolationMessage msg : validationResult.getGlobalMessages()) {
				sb.append(renderMessage(ctx, msg));	
			}
			sb.append("</div>" + newLine());
		}
		return sb.toString();
	}
	
	public <T> String renderMapping(RenderContext<?> ctx, FormMapping<T> mapping) {
		StringBuilder sb = new StringBuilder();
		if (mapping.isVisible()) {
			sb.append(renderVisibleMapping(ctx, mapping));
		} else {
			sb.append(renderInvisibleElement(ctx, mapping));
		}
		return sb.toString();
	}

	public <T> String renderVisibleMapping(RenderContext<?> ctx, FormMapping<T> mapping) {
		StringBuilder sb = new StringBuilder();
		sb.append(newLine());
		sb.append(renderMappingBoxBegin(ctx, mapping));
		if (mapping instanceof BasicListFormMapping) {
			for (FormMapping<?> m : ((BasicListFormMapping<?>)mapping).getList()) {
				sb.append(renderMapping(ctx, m));
			}
		} else {
			for (FormElement el : mapping.getElements()) {
				if (el instanceof FormField) {
					FormField<?> field = (FormField<?>)el;
					sb.append(renderField(ctx, field));
				} else if (el instanceof FormMapping) {
					sb.append(renderMapping(ctx, (FormMapping<?>)el));
				} else {
					throw new UnsupportedOperationException("Unsupported form element " + el.getClass().getName());
				}
			}
		}
		sb.append(renderMappingBoxEnd(ctx, mapping));
		return sb.toString();
	}

	public <T> String renderField(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		String type = getFieldType(field);
		if (type != null && type.equals(FormFieldType.HIDDEN_FIELD.getType())) {
			sb.append(renderHiddenInput(ctx, field));
		} else if (field.isVisible()) {
			sb.append(renderVisibleField(ctx, field));
		} else {
			// Placeholder hidden div so the field can be made visible later and placed to this reserved position
			sb.append(renderInvisibleElement(ctx, field));
		}
		return sb.toString();
	}
	
	public <T> String renderHiddenInput(RenderContext<?> ctx, FormField<T> field) {
		return renderInput(ctx, field) + newLine();
	}
	
	public <T> String renderVisibleField(RenderContext<?> ctx, FormField<T> field) {
		String type = getFieldType(field);
		StringBuilder sb = new StringBuilder();
		FormFieldType formComponent = FormFieldType.findByType(type);
		if (formComponent != null) {
			switch (formComponent) {
				case TEXT_FIELD:
					sb.append(renderTextField(ctx, field));
					break;
				case TEXT_AREA:
					sb.append(renderTextArea(ctx, field));
					break;
				case PASSWORD:
					sb.append(renderPassword(ctx, field));
					break;
				case CHECK_BOX:
					sb.append(renderCheckBox(ctx, field));
					break;
				case DATE_PICKER:
					sb.append(renderDatePicker(ctx, field));
					break;
				case DROP_DOWN_CHOICE:
					sb.append(renderDropDownChoice(ctx, field));
					break;
				case FILE_UPLOAD:
					sb.append(renderFileUpload(ctx, field));
					break;
				case MULTIPLE_CHECK_BOX:
					sb.append(renderMultipleCheckbox(ctx, field));
					break;
				case MULTIPLE_CHOICE:
					sb.append(renderMultipleChoice(ctx, field));
					break;
				case RADIO_CHOICE:
					sb.append(renderRadioChoice(ctx, field));
					break;
				case COLOR:
					sb.append(renderColor(ctx, field));
					break;
				case DATE:
					sb.append(renderDate(ctx, field));
					break;
				case DATE_TIME:
					sb.append(renderDateTime(ctx, field));
					break;
				case DATE_TIME_LOCAL:
					sb.append(renderDateTimeLocal(ctx, field));
					break;
				case TIME:
					sb.append(renderTime(ctx, field));
					break;
				case EMAIL:
					sb.append(renderEmail(ctx, field));
					break;
				case MONTH:
					sb.append(renderMonth(ctx, field));
					break;
				case NUMBER:
					sb.append(renderNumber(ctx, field));
					break;
				case RANGE:
					sb.append(renderRange(ctx, field));
					break;
				case SEARCH:
					sb.append(renderSearch(ctx, field));
					break;
				case TEL:
					sb.append(renderTel(ctx, field));
					break;
				case URL: 
					sb.append(renderUrl(ctx, field));
					break;
				case WEEK:
					sb.append(renderWeek(ctx, field));
					break;
				case SUBMIT_BUTTON:
					sb.append(renderSubmitButton(ctx, field));
					break;
				default:
					throw new UnsupportedOperationException("Cannot render component with type " + type);
			}
		} else {
			throw new UnsupportedOperationException("Cannot render component with type " + type);
		}
		return sb.toString();
	}
	
	public String renderInvisibleElement(RenderContext<?> ctx, FormElement element) {
		return "<div id=\"" + renderElementBoxId(ctx, element) + "\" class=\"hidden\"></div>" + newLine();
	}
	
	public String renderDefaultSubmitButton(RenderContext<?> ctx) {
		return renderSubmitButton(ctx, Forms.<String>field(PROPERTY_DEFAULT_SUBMIT, FormFieldType.SUBMIT_BUTTON.getType()).build());
	}
	
	public <T> String renderSubmitButton(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"" + getFormBlockClass() + "\">" + newLine());
		sb.append("<div class=\"" + getInputIndentClass() + "\">" + newLine());
		sb.append("<button type=\"submit\" value=\"" + renderValue(ctx, field.getValue()) + "\" class=\"btn btn-default\">");
		String text = null;
		if (field.getLabelKey() != null && !field.getLabelKey().equals(PROPERTY_DEFAULT_SUBMIT)) {
			MessageTranslator tr = createMessageTranslator(ctx, field);
			text = escapeHtml(tr.getMessage(field.getLabelKey()));
		} else {
			text = "Submit";
		}
		sb.append(text);
		sb.append("</button>" + newLine());
		sb.append("</div>" + newLine());
		sb.append("</div>" + newLine());
		return sb.toString();
	}
	
	public <T> String renderFormBegin(RenderContext<T> ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append("<form action=\"");
		sb.append(renderUrl(ctx, ctx.getActionUrl()));
		sb.append("\" method=\"");
		sb.append(ctx.getMethod().name());
		sb.append("\" class=\"form-horizontal\" role=\"form\">" + newLine());
		return sb.toString();
	}
	
	public <T> String renderFormEnd(RenderContext<T> ctx) {
		return "</form>" + newLine();
	}
	
	protected <T> String renderMappingBoxBegin(RenderContext<?> ctx, FormMapping<T> mapping) {
		StringBuilder sb = new StringBuilder();
		// Form group
		sb.append(renderMappingBoxBeginTag(ctx, mapping));
		
		// Label
		sb.append(renderMappingLabelElement(ctx, mapping));
		
		// Mapping messages
		sb.append(renderFieldMessages(ctx, mapping.getValidationMessages()));
		return sb.toString();
	}
	
	protected <T> String renderMappingBoxEnd(RenderContext<?> ctx, FormMapping<T> mapping) {
		return renderMappingBoxEndTag(ctx, mapping);
	}

	protected <T> String renderMappingBoxBeginTag(RenderContext<?> ctx, FormMapping<T> mapping) {
		String maxSeverityClass = getMaxSeverityClass(mapping.getValidationMessages());
		StringBuilder sb = new StringBuilder();
		sb.append("<div id=\"" + renderElementBoxId(ctx, mapping) + "\" class=\"" + maxSeverityClass + "\">" + newLine());
		return sb.toString();
	}
	
	protected <T> String renderMappingBoxEndTag(RenderContext<?> ctx, FormMapping<T> mapping) {
		return "</div>" + newLine();
	}
	
	protected <T> String renderUrl(RenderContext<T> ctx, String url) {
		return url;
	}

	protected String renderFieldMessages(RenderContext<?> ctx, List<ConstraintViolationMessage> fieldMessages) {
		StringBuilder sb = new StringBuilder();
		if (fieldMessages != null && !fieldMessages.isEmpty()) {
			for (ConstraintViolationMessage msg : fieldMessages) {
				sb.append(renderMessage(ctx, msg));
			}
		}
		return sb.toString();
	}
	
	protected String renderMessage(RenderContext<?> ctx, ConstraintViolationMessage msg) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"" + msg.getSeverity().getStyleClass() + "\">" + escapeHtml(msg.getText()) + "</div>" + newLine());
		return sb.toString();
	}
	
	protected <T> String renderMappingLabelElement(RenderContext<?> ctx, FormMapping<T> mapping) {
		StringBuilder sb = new StringBuilder("");
		if (!mapping.isRootMapping()) {
			sb.append("<div class=\"" + getFormBlockClass() + "\">" + newLine());
			sb.append("<div class=\"" + getLabelIndentClass() + "\">" + newLine());
			sb.append("<label>" + newLine());
			sb.append(renderLabelText(ctx, mapping));
			sb.append(":");
			sb.append("</label>" + newLine());
			sb.append("</div>" + newLine());
			sb.append("</div>" + newLine());
		}
		return sb.toString();
	}
	
	protected <T> String renderFieldLabelElement(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<label for=\"id-" + field.getName() + "\" class=\"" + getLabelIndentClass() + "\">");
		sb.append(renderLabelText(ctx, field));
		sb.append(":");
		sb.append("</label>" + newLine());
		return sb.toString();
	}

	protected String renderValue(RenderContext<?> ctx, String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}
		return escapeHtml(value);
	}
	
	/**
	 * Escapes HTML (converts HTML text to XML entities).
	 * @param s
	 * @return
	 */
	protected String escapeHtml(String s) {
		if (s == null) return null;
		if (s.isEmpty()) return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '&': sb.append("&amp;"); break;
				case '<': sb.append("&lt;"); break;
				case '>': sb.append("&gt;"); break;
				case '"': sb.append("&quot;"); break;
				default:  sb.append(c); break;
			}
		}
		return sb.toString();
	}
	
	protected String renderElementBoxId(RenderContext<?> ctx, FormElement element) {
		return "box-" + element.getName();
	}
	
	protected String renderAccessibilityAttributes(RenderContext<?> ctx, FormElement element) {
		StringBuilder sb = new StringBuilder();
		if (!element.isEnabled()) {
			sb.append(" disabled=\"disabled\"");
		}
		if (element.isReadonly()) {
			sb.append(" readonly=\"readonly\"");
		}
		return sb.toString();
	}
	
	protected <T> String renderTextarea(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<textarea name=\"" + field.getName() + "\" id=\"id-" + field.getName() + "\" class=\"input-sm form-control\"");
		sb.append(renderAccessibilityAttributes(ctx, field) + ">");
		sb.append(renderValue(ctx, field.getValue()));
		sb.append("</textarea>" + newLine());
		return sb.toString();
	}	
	
	protected <T> String renderInput(RenderContext<?> ctx, FormField<T> field) {
		return renderInput(ctx, field, null);
	}
	
	protected <T> String renderInput(RenderContext<?> ctx, FormField<T> field, String typeOverriden) {
		String type = getFieldType(field);
		if (typeOverriden != null && !typeOverriden.isEmpty()) {
			type = typeOverriden;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<input type=\"" + type + "\" name=\"" + field.getName() + "\" id=\"id-" + field.getName() + "\"");
		if (type != null && !FormFieldType.FILE_UPLOAD.getType().equals(type)) {
			String value = renderValue(ctx, field.getValue());
			sb.append(" value=\"" + value + "\"");
		}
		if (type != null && !type.equals(FormFieldType.HIDDEN_FIELD.getType())) {
			sb.append(renderAccessibilityAttributes(ctx, field));
		}
		if (isInputClassIncluded(type)) {
			sb.append(" class=\"input-sm form-control\"");
		}
		sb.append("/>" + newLine());
		return sb.toString();
	}
	
	protected <T> String renderCheckBoxInput(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<input type=\"checkbox\" name=\"" + field.getName() + "\" id=\"id-" + field.getName() + "\" value=\"" + renderValue(ctx, "1") + "\"");
		if (field.getValue() != null && !field.getValue().isEmpty()) { 
			String lc = field.getValue().toLowerCase();
			if (Boolean.valueOf(lc.equals("t") || lc.equals("y") || lc.equals("true") || lc.equals("1")).booleanValue()) {
				sb.append(" checked=\"checked\" ");
			}
		}
		sb.append(renderAccessibilityAttributes(ctx, field));
		sb.append("/>" + newLine());
		return sb.toString();
	}
	
	protected <T> String renderSelect(RenderContext<?> ctx, FormField<T> field, boolean multiple, Integer size) {
		StringBuilder sb = new StringBuilder();
		sb.append("<select name=\"" + field.getName() + "\" id=\"id-" + field.getName() + "\"");
		if (multiple) {
			sb.append(" multiple=\"multiple\"");
		}
		if (size != null) {
			sb.append(" size=\"" + size + "\"");
		}
		sb.append(" class=\"input-sm form-control\"");
		sb.append(renderAccessibilityAttributes(ctx, field));
		sb.append(">" + newLine());
		if (field.getChoiceProvider() != null && field.getChoiceRenderer() != null) {
			List<?> items = field.getChoiceProvider().getItems();
			if (items != null) {
				ChoiceRenderer<Object> choiceRenderer = (ChoiceRenderer<Object>)field.getChoiceRenderer();
				int itemIndex = 0;
				for (Object item : items) {
					String value = renderValue(ctx, choiceRenderer.getId(item, itemIndex));
					String title = escapeHtml(choiceRenderer.getTitle(item, itemIndex));
					sb.append("<option value=\"" + value + "\"");
					if (field.getFilledObjects().contains(item)) {
						sb.append(" selected=\"selected\"");
					}
					sb.append(">" + title + "</option>" + newLine());
					itemIndex++;
				}
			}
		}
		sb.append("</select>" + newLine());
		return sb.toString();
	}
	
	protected <T> String renderChecks(RenderContext<?> ctx, FormField<T> field) {
		String type = field.getType() != null && field.getType().equals(FormFieldType.RADIO_CHOICE.getType()) ? "radio" : "checkbox";
		StringBuilder sb = new StringBuilder();
		if (field.getChoiceProvider() != null && field.getChoiceRenderer() != null) {
			List<?> items = field.getChoiceProvider().getItems();
			if (items != null) {
				ChoiceRenderer<Object> choiceRenderer = (ChoiceRenderer<Object>)field.getChoiceRenderer();
				int itemIndex = 0;
				for (Object item : items) {
					String value = renderValue(ctx, choiceRenderer.getId(item, itemIndex));
					String title = escapeHtml(choiceRenderer.getTitle(item, itemIndex));
					
					sb.append("<div class=\"" + type + "\">" + newLine());
					sb.append("<label><input type=\"" + type + "\" name=\"" + field.getName() + "\" value=\"" + value + "\"");
					if (field.getFilledObjects().contains(item)) {
						sb.append(" checked=\"checked\"");
					}
					sb.append(renderAccessibilityAttributes(ctx, field));
					sb.append("/> " + title + "</label>" + newLine());
					sb.append("</div>" + newLine());
					itemIndex++;
				}
			}
		}
		return sb.toString();
	}
	
	protected <T> String renderFieldBegin(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"col-sm-4\">" + newLine());
		return sb.toString();
	}
	
	protected <T> String renderFieldEnd(RenderContext<?> ctx, FormField<T> field) {
		return "</div>" + newLine();
	}
	
	protected <T> String renderDatePickerJavaScript(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<script>" + newLine());
		sb.append("$(function(){" + newLine());
		sb.append("	$('#id-" + field.getName() + "').datepicker({ dateFormat: \"" + getDatePickerPattern(ctx, field) + "\" });" + newLine());
		sb.append("});" + newLine());
		sb.append("</script>" + newLine());
		return sb.toString();
	}
	
	protected <T> String getDatePickerPattern(RenderContext<?> ctx, FormField<T> field) {
		return "d.m.yy";
	}
	
	protected <T> String renderTextField(RenderContext<?> ctx, FormField<T> field) {
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderColor(RenderContext<?> ctx, FormField<T> field) {
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderDate(RenderContext<?> ctx, FormField<T> field) {
		// TODO: Support for min, max attributes
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderDateTime(RenderContext<?> ctx, FormField<T> field) {
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderDateTimeLocal(RenderContext<?> ctx, FormField<T> field) {
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderTime(RenderContext<?> ctx, FormField<T> field) {
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderEmail(RenderContext<?> ctx, FormField<T> field) {
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderMonth(RenderContext<?> ctx, FormField<T> field) {
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderNumber(RenderContext<?> ctx, FormField<T> field) {
		// TODO: Support for min, max, step attributes
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderRange(RenderContext<?> ctx, FormField<T> field) {
		// TODO: Support for min, max attributes
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderSearch(RenderContext<?> ctx, FormField<T> field) {
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderTel(RenderContext<?> ctx, FormField<T> field) {
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderUrl(RenderContext<?> ctx, FormField<T> field) {
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderWeek(RenderContext<?> ctx, FormField<T> field) {
		return renderTextFieldInternal(ctx, field);
	}
	
	protected <T> String renderTextArea(RenderContext<?> ctx, FormField<T> field) {
		return renderFieldBoxBegin(ctx, field) +
			renderFieldBegin(ctx, field) +
			renderTextarea(ctx, field) +
			renderFieldMessages(ctx, field.getValidationMessages()) + 
			renderFieldEnd(ctx, field) +
			renderFieldBoxEnd(ctx, field);
	}
	
	protected <T> String renderCheckBox(RenderContext<?> ctx, FormField<T> field) {
		return renderCheckBoxBegin(ctx, field) +
			renderCheckBoxInput(ctx, field) + 
			renderFieldMessages(ctx, field.getValidationMessages()) + 
			renderCheckBoxEnd(ctx, field);
	}
	
	protected <T> String renderPassword(RenderContext<?> ctx, FormField<T> field) {
		return renderFieldBoxBegin(ctx, field) +
			renderFieldBegin(ctx, field) +
			renderFieldInput(ctx, field) + 
			renderFieldMessages(ctx, field.getValidationMessages()) + 
			renderFieldEnd(ctx, field) +
			renderFieldBoxEnd(ctx, field);
	}
	
	protected <T> String renderFileUpload(RenderContext<?> ctx, FormField<T> field) {
		return renderFieldBoxBegin(ctx, field) +
			renderFieldBegin(ctx, field) +
			renderFieldInput(ctx, field) + 
			renderFieldMessages(ctx, field.getValidationMessages()) + 
			renderFieldEnd(ctx, field) +
			renderFieldBoxEnd(ctx, field);
	}
	
	protected <T> String renderDatePicker(RenderContext<?> ctx, FormField<T> field) {
		return renderFieldBoxBegin(ctx, field) +
			renderFieldBegin(ctx, field) +
			renderFieldInput(ctx, field, "text") +
			renderDatePickerJavaScript(ctx, field) +
			renderFieldMessages(ctx, field.getValidationMessages()) + 
			renderFieldEnd(ctx, field) +
			renderFieldBoxEnd(ctx, field);
	}
	
	protected <T> String renderDropDownChoice(RenderContext<?> ctx, FormField<T> field) {
		return renderFieldBoxBegin(ctx, field) +
			renderFieldBegin(ctx, field) +
			renderSelect(ctx, field, false, null) + 
			renderFieldMessages(ctx, field.getValidationMessages()) + 
			renderFieldEnd(ctx, field) +
			renderFieldBoxEnd(ctx, field);
	}
	
	protected <T> String renderMultipleChoice(RenderContext<?> ctx, FormField<T> field) {
		return renderFieldBoxBegin(ctx, field) +
			renderFieldBegin(ctx, field) +
			renderSelect(ctx, field, true, null) + 
			renderFieldMessages(ctx, field.getValidationMessages()) + 
			renderFieldEnd(ctx, field) +
			renderFieldBoxEnd(ctx, field);
	}
	
	protected <T> String renderMultipleCheckbox(RenderContext<?> ctx, FormField<T> field) {
		return renderFieldBoxBegin(ctx, field) +
			renderFieldBegin(ctx, field) +
			renderChecks(ctx, field) + 
			renderFieldMessages(ctx, field.getValidationMessages()) + 
			renderFieldEnd(ctx, field) +
			renderFieldBoxEnd(ctx, field);
	}
	
	protected <T> String renderRadioChoice(RenderContext<?> ctx, FormField<T> field) {
		return renderFieldBoxBegin(ctx, field) +
			renderFieldBegin(ctx, field) +
			renderChecks(ctx, field) + 
			renderFieldMessages(ctx, field.getValidationMessages()) + 
			renderFieldEnd(ctx, field) +
			renderFieldBoxEnd(ctx, field);
	}

	protected <T> String renderFieldBoxBegin(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		// Form group begin
		sb.append(renderFieldBoxBeginTag(ctx, field));

		// Label
		sb.append(renderFieldLabelElement(ctx, field));
		return sb.toString();
	}
	
	protected <T> String renderFieldBoxEnd(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append(renderFieldBoxEndTag(ctx, field));
		return sb.toString();
	}
	
	protected <T> String renderCheckBoxBegin(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		// Form group begin
		sb.append(renderFieldBoxBeginTag(ctx, field));
		sb.append(renderLabelBeginTag(ctx, field));
		return sb.toString();
	}
	
	protected <T> String renderCheckBoxEnd(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append(renderLabelText(ctx, field));
		sb.append(renderLabelEndTag(ctx, field));
		sb.append(renderFieldBoxEndTag(ctx, field));
		return sb.toString();
	}
	
	protected <T> String renderLabelBeginTag(RenderContext<?> ctx, FormField<T> field) {
		return "<label>" + newLine();
	}
	
	protected <T> String renderLabelEndTag(RenderContext<?> ctx, FormField<T> field) {
		return "</label>" + newLine();
	}
	
	protected <T> String renderFieldBoxBeginTag(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		String maxSeverityClass = getMaxSeverityClass(field.getValidationMessages());
		sb.append("<div id=\"" + renderElementBoxId(ctx, field) + "\" class=\"" + getFormBlockClass() + " " + maxSeverityClass + "\">" + newLine());
		if (field.getType() != null && field.getType().equals(FormFieldType.CHECK_BOX.getType())) {
			sb.append("<div class=\"" + getInputIndentClass() + "\">" + newLine());
			sb.append("<div class=\"checkbox\">" + newLine());
		}
		return sb.toString();
	}
	
	protected <T> String renderFieldBoxEndTag(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		if (field.getType() != null && field.getType().equals(FormFieldType.CHECK_BOX.getType())) {
			sb.append("</div>" + newLine());
			sb.append("</div>" + newLine());
		}
		sb.append("</div>" + newLine() + newLine());
		return sb.toString();
	}
	
	protected <T> String renderFieldInput(RenderContext<?> ctx, FormField<T> field) {
		return renderFieldInput(ctx, field, null);
	}
	
	protected <T> String renderFieldInput(RenderContext<?> ctx, FormField<T> field, String typeOverriden) {
		StringBuilder sb = new StringBuilder();
		sb.append(renderInput(ctx, field, typeOverriden));
		return sb.toString();
	}
	
	protected <T> String renderLabelText(RenderContext<?> ctx, FormElement formElement) {
		StringBuilder sb = new StringBuilder();
		MessageTranslator tr = createMessageTranslator(ctx, formElement);
		String msgKey = formElement.getLabelKey();
		if (formElement instanceof FormMapping) {
			FormMapping<?> m = (FormMapping<?>)formElement;
			if (m.getIndex() != null) {
				msgKey = msgKey + Forms.PATH_SEP + "singular"; 
			}
		}
		sb.append(escapeHtml(tr.getMessage(msgKey, ctx.getLocale())));
		if (formElement instanceof BasicListFormMapping) {
			FormMapping<?> listMapping = (FormMapping<?>)formElement;
			sb.append(" (" + listMapping.getList().size() + ")");
		}
		if (formElement.isRequired()) {
			sb.append(renderRequiredMark(ctx, formElement));
		}
		return sb.toString();
	}
	
	protected <T> String renderRequiredMark(RenderContext<?> ctx, FormElement formElement) {
		return "&nbsp;*";
	}
	
	protected String getFormBlockClass() {
		return "form-group";
	}
	
	protected String getLabelIndentClass() {
		return "control-label col-sm-2";
	}
	
	protected String getInputIndentClass() {
		return "col-sm-offset-2 col-sm-10";
	}
	
	protected boolean containsSubmitButton(FormMapping<?> mapping) {
		// searching only on top level
		for (FormField<?> field : mapping.getFields().values()) {
			if (field.getType() != null && field.getType().equals(FormFieldType.SUBMIT_BUTTON.getType())) {
				return true;
			}
		}
		return false;
	}
	
	private <T> MessageTranslator createMessageTranslator(RenderContext<?> ctx, FormElement formElement) {
		return new MessageTranslator(
			formElement.getParent().getDataClass(), ctx.getLocale(), 
			ctx.getFilledForm().getDataClass());
	}
	
	private String newLine() {
		return System.getProperty("line.separator");
	}
	
	private boolean isInputClassIncluded(String type) {
		return type != null 
			&& !type.equals(FormFieldType.FILE_UPLOAD.getType()) 
			&& !type.equals(FormFieldType.HIDDEN_FIELD.getType())
			&& !type.equals(FormFieldType.CHECK_BOX.getType());
	}
	
	private <T> String getFieldType(FormField<T> field) {
		String type = field.getType();
		if (type == null) {
			type = "text";
		}
		return type.toLowerCase();
	}
	
	private String getMaxSeverityClass(List<ConstraintViolationMessage> fieldMessages) {
		Severity maxSeverity = Severity.max(fieldMessages);
		String maxSeverityClass = maxSeverity != null ? ("has-" + maxSeverity.getStyleClass()) : "";
		return maxSeverityClass;
	}
	
	private <T> String renderTextFieldInternal(RenderContext<?> ctx, FormField<T> field) {
		return renderFieldBoxBegin(ctx, field) +
			renderFieldBegin(ctx, field) +
			renderFieldInput(ctx, field) + 
			renderFieldMessages(ctx, field.getValidationMessages()) + 
			renderFieldEnd(ctx, field) +
			renderFieldBoxEnd(ctx, field);
	}
	
	private final String PROPERTY_DEFAULT_SUBMIT = "_defaultSubmitButton";
}
