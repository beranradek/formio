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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.formio.FormComponent;
import net.formio.FormElement;
import net.formio.FormField;
import net.formio.FormMapping;
import net.formio.choice.ChoiceRenderer;
import net.formio.common.MessageTranslator;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.Severity;
import net.formio.validation.ValidationResult;

/**
 * Convenience basic implementation of {@link FormRenderer}.
 * @author Radek Beran
 */
class BasicFormRenderer implements FormRenderer {

	@Override
	public <T> String renderForm(RenderContext<T> ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append(renderFormTag(ctx));
		sb.append(renderGlobalMessages(ctx, ctx.getFilledForm().getValidationResult()));
		sb.append(renderMapping(ctx, ctx.getFilledForm(), new ParentMappings(ctx.getFilledForm(), null)));
		sb.append(renderSubmit(ctx));
		sb.append("</form>" + newLine());
		return sb.toString();
	}
	
	@Override
	public String renderGlobalMessages(RenderContext<?> ctx, ValidationResult validationResult) {
		StringBuilder sb = new StringBuilder();
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
	
	@Override
	public <T> String renderMapping(RenderContext<?> ctx, FormMapping<T> mapping, ParentMappings parentMappings) {
		StringBuilder sb = new StringBuilder();
		if (mapping.isVisible()) {
			sb.append(newLine());
			sb.append("<div id=\"" + renderElementBoxId(ctx, mapping) + "\">" + newLine());
			ParentMappings nestedParents = new ParentMappings(parentMappings.getRootMapping(), mapping);
			for (FormElement el : mapping.getElements()) {
				if (el instanceof FormField) {
					FormField<?> field = (FormField<?>)el;
					sb.append(renderField(ctx, field, mapping.getValidationResult().getFieldMessages().get(field.getName()), nestedParents));
				} else if (el instanceof FormMapping) {
					FormMapping<?> nestedMapping = (FormMapping<?>)el;
					sb.append(renderMapping(ctx, nestedMapping, nestedParents));
				} else {
					throw new UnsupportedOperationException("Unsupported form element " + el.getClass().getName());
				}
			}
			sb.append("</div>" + newLine());
		} else {
			sb.append(renderHiddenDivElement(ctx, mapping));
		}
		return sb.toString();
	}

	@Override
	public <T> String renderField(RenderContext<?> ctx, FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings) {
		StringBuilder sb = new StringBuilder();
		String type = getFieldType(field);
		if (type.equals(FormComponent.HIDDEN_FIELD.getType())) {
			sb.append(renderHiddenField(ctx, field));
		} else if (field.isVisible()) {
			FormComponent formComponent = FormComponent.findByType(type);
			if (formComponent != null) {
				switch (formComponent) {
					case TEXT_FIELD:
						sb.append(renderTextField(ctx, field, fieldMessages, parentMappings));
						break;
					case TEXT_AREA:
						sb.append(renderTextArea(ctx, field, fieldMessages, parentMappings));
						break;
					case PASSWORD:
						sb.append(renderPassword(ctx, field, fieldMessages, parentMappings));
						break;
					case BUTTON:
						throw new UnsupportedOperationException("Not implemented yet");
						// break;
					case CHECK_BOX:
						sb.append(renderCheckBox(ctx, field, fieldMessages, parentMappings));
						break;
					case DATE_PICKER:
						// throw new UnsupportedOperationException("Not implemented yet");
						break;
					case DROP_DOWN_CHOICE:
						sb.append(renderDropDownChoice(ctx, field, fieldMessages, parentMappings));
						break;
					case FILE_UPLOAD:
						sb.append(renderFileUpload(ctx, field, fieldMessages, parentMappings));
						break;
					case LABEL:
						break;
					case LINK:
						break;
					case MULTIPLE_CHECK_BOX:
						sb.append(renderMultipleCheckbox(ctx, field, fieldMessages, parentMappings));
						break;
					case MULTIPLE_CHOICE:
						break;
					case RADIO_CHOICE:
						break;
					default:
						throw new UnsupportedOperationException("Cannot render component with type " + type);
				}
			} else {
				throw new UnsupportedOperationException("Cannot render component with type " + type);
			}
		} else {
			// Placeholder hidden div so the field can be made visible later and placed to this reserved position
			sb.append(renderHiddenDivElement(ctx, field));
		}
		return sb.toString();
	}
	
	@Override
	public String renderSubmit(RenderContext<?> ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"form-group\">" + newLine());
		sb.append("<div class=\"col-sm-offset-2 col-sm-10\">" + newLine());
		sb.append("<button type=\"submit\" class=\"btn btn-default\">Submit</button>" + newLine());
		sb.append("</div>" + newLine());
		sb.append("</div>" + newLine());
		return sb.toString();
	}
	
	protected <T> String renderFormTag(RenderContext<T> ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append("<form action=\"");
		sb.append(renderUrl(ctx, ctx.getActionUrl()));
		sb.append("\" method=\"");
		sb.append(ctx.getMethod().name());
		sb.append("\" class=\"form-horizontal\" role=\"form\">" + newLine());
		return sb.toString();
	}
	
	protected String renderUrl(RenderContext<?> ctx, String url) {
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
	
	protected <T> String renderFieldLabelBefore(RenderContext<?> ctx, FormField<T> field, ParentMappings parentMappings) {
		StringBuilder sb = new StringBuilder();
		String type = getFieldType(field);
		if (checkInputTypes.contains(type)) {
			sb.append("<label>" + newLine());
		} else {
			// TODO: Render required mark
			sb.append("<label class=\"control-label col-sm-2\" for=\"id-" + field.getName() + "\">");
			sb.append(renderLabelText(ctx, field, parentMappings));
			sb.append(":");
			sb.append("</label>" + newLine());
		}
		return sb.toString();
	}
	
	protected <T> String renderFieldLabelAfter(RenderContext<?> ctx, FormField<T> field, ParentMappings parentMappings) {
		StringBuilder sb = new StringBuilder("");
		String type = getFieldType(field);
		if (checkInputTypes.contains(type)) {
			sb.append(renderLabelText(ctx, field, parentMappings));
			sb.append("</label>" + newLine());
		}
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
	
	protected String newLine() {
		return System.getProperty("line.separator");
	}
	
	protected String renderHiddenDivElement(RenderContext<?> ctx, FormElement element) {
		return "<div id=\"" + renderElementBoxId(ctx, element) + "\" class=\"hidden\"></div>" + newLine();
	}
	
	protected String renderElementBoxId(RenderContext<?> ctx, FormElement element) {
		return "box-" + element.getName();
	}
	
	<T> String renderHtmlPage(FormMapping<T> filledForm, FormMethod method, String actionUrl, Locale locale) {
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
		sb.append("<!-- Optional theme -->" + newLine());
		sb.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css\">" + newLine());
		sb.append("</head>" + newLine());
		sb.append("<body style=\"margin:1em\">" + newLine());

		RenderContext<T> ctx = new RenderContext<T>();
		ctx.setFilledForm(filledForm);
		ctx.setMethod(method);
		ctx.setActionUrl(actionUrl);
		ctx.setLocale(locale);
		sb.append(renderForm(ctx));
		
		sb.append("<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->" + newLine());	    
		sb.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js\"></script>" + newLine());
		sb.append("<!-- Latest compiled and minified JavaScript -->" + newLine());
		sb.append("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js\"></script>" + newLine());
		sb.append("</body>" + newLine());
		sb.append("</html>" + newLine());
		return sb.toString();
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
	
	protected String renderTextarea(RenderContext<?> ctx, FormField<?> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<textarea class=\"input-sm form-control\" name=\"" + field.getName() + "\" id=\"id-" + field.getName() + "\"");
		sb.append(renderAccessibilityAttributes(ctx, field) + ">");
		sb.append(renderValue(ctx, field.getValue()));
		sb.append("</textarea>" + newLine());
		return sb.toString();
	}	
	
	protected String renderInput(RenderContext<?> ctx, FormField<?> field, String type) {
		if (type == null) throw new IllegalArgumentException("type cannot be null");
		StringBuilder sb = new StringBuilder();
		sb.append("<input");
		if (isInputClassIncluded(type)) {
			sb.append(" class=\"input-sm form-control\"");
		}
		String value = "";
		if (checkInputTypes.contains(type)) {
			if (field.getValue() != null && !field.getValue().isEmpty()) { 
				String lc = field.getValue().toLowerCase();
				if (Boolean.valueOf(lc.equals("t") || lc.equals("y") || lc.equals("true") || lc.equals("1")).booleanValue()) {
					sb.append(" checked=\"checked\" ");
				}
			}
			value = renderValue(ctx, "1");
		} else {
			value = renderValue(ctx, field.getValue());
		}
		sb.append(" type=\"" + type + "\" name=\"" + field.getName() + "\" id=\"id-" + field.getName() + "\" ");
		if (!FormComponent.FILE_UPLOAD.getType().equals(type)) {
			sb.append("value=\"" + value + "\" ");
		}
		if (!type.equals(FormComponent.HIDDEN_FIELD.getType())) {
			sb.append(renderAccessibilityAttributes(ctx, field));
		}
		sb.append("/>" + newLine());
		return sb.toString();
	}
	
	protected String renderSelect(RenderContext<?> ctx, FormField<?> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<select class=\"input-sm form-control\" name=\"" + field.getName() + "\" id=\"id-" + field.getName() + "\"");
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
	
	protected String renderCheckboxes(RenderContext<?> ctx, FormField<?> field) {
		StringBuilder sb = new StringBuilder();
		if (field.getChoiceProvider() != null && field.getChoiceRenderer() != null) {
			List<?> items = field.getChoiceProvider().getItems();
			if (items != null) {
				ChoiceRenderer<Object> choiceRenderer = (ChoiceRenderer<Object>)field.getChoiceRenderer();
				int itemIndex = 0;
				for (Object item : items) {
					String value = renderValue(ctx, choiceRenderer.getId(item, itemIndex));
					String title = escapeHtml(choiceRenderer.getTitle(item, itemIndex));
					
					sb.append("<div class=\"checkbox\">" + newLine());
					sb.append("<label><input type=\"checkbox\" name=\"" + field.getName() + "\" value=\"" + value + "\"");
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

	protected boolean isInputClassIncluded(String type) {
		return !type.equals(FormComponent.FILE_UPLOAD.getType()) 
			&& !type.equals(FormComponent.HIDDEN_FIELD.getType())
			&& !type.equals(FormComponent.CHECK_BOX.getType());
	}
	
	protected String renderFieldBegin(RenderContext<?> ctx, FormField<?> field, boolean withoutLabel) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"");
		if (withoutLabel) {
			// offset instead of label
			sb.append("col-sm-offset-4 ");
		}
		sb.append("col-sm-4");
		sb.append("\">" + newLine());
		return sb.toString();
	}
	
	protected String renderFieldEnd(RenderContext<?> ctx, FormField<?> field) {
		return "</div>" + newLine();
	}
	
	protected <T> String getFieldType(FormField<T> field) {
		String type = field.getType();
		if (type == null) {
			type = "text";
		}
		return type.toLowerCase();
	}
	
	protected <T> String renderHiddenField(RenderContext<?> ctx, FormField<T> field) {
		return renderInput(ctx, field, FormComponent.HIDDEN_FIELD.getType()) + newLine();
	}
	
	protected <T> String renderTextField(RenderContext<?> ctx, FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings) {
		return renderBeforeField(ctx, field, fieldMessages, parentMappings) +
			renderFieldBegin(ctx, field, false) +  // withoutLabel = false
			renderFieldInput(ctx, field) + 
			renderFieldMessages(ctx, fieldMessages) + 
			renderFieldEnd(ctx, field) +
			renderAfterField(ctx, field, parentMappings);
	}
	
	protected <T> String renderTextArea(RenderContext<?> ctx, FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings) {
		return renderBeforeField(ctx, field, fieldMessages, parentMappings) +
			renderFieldBegin(ctx, field, false) +  // withoutLabel = false
			renderTextarea(ctx, field) +
			renderFieldMessages(ctx, fieldMessages) + 
			renderFieldEnd(ctx, field) +
			renderAfterField(ctx, field, parentMappings);
	}
	
	protected <T> String renderCheckBox(RenderContext<?> ctx, FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings) {
		return renderBeforeField(ctx, field, fieldMessages, parentMappings) +
			renderFieldInput(ctx, field) + 
			renderFieldMessages(ctx, fieldMessages) + 
			renderAfterField(ctx, field, parentMappings);
	}
	
	protected <T> String renderPassword(RenderContext<?> ctx, FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings) {
		return renderBeforeField(ctx, field, fieldMessages, parentMappings) +
			renderFieldBegin(ctx, field, false) +  // withoutLabel = false
			renderFieldInput(ctx, field) + 
			renderFieldMessages(ctx, fieldMessages) + 
			renderFieldEnd(ctx, field) +
			renderAfterField(ctx, field, parentMappings);
	}
	
	protected <T> String renderFileUpload(RenderContext<?> ctx, FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings) {
		return renderBeforeField(ctx, field, fieldMessages, parentMappings) +
			renderFieldBegin(ctx, field, false) +  // withoutLabel = false
			renderFieldInput(ctx, field) + 
			renderFieldMessages(ctx, fieldMessages) + 
			renderFieldEnd(ctx, field) +
			renderAfterField(ctx, field, parentMappings);
	}
	
	protected <T> String renderDropDownChoice(RenderContext<?> ctx, FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings) {
		return renderBeforeField(ctx, field, fieldMessages, parentMappings) +
			renderFieldBegin(ctx, field, false) +  // withoutLabel = false
			renderSelect(ctx, field) + 
			renderFieldMessages(ctx, fieldMessages) + 
			renderFieldEnd(ctx, field) +
			renderAfterField(ctx, field, parentMappings);
	}
	
	protected <T> String renderMultipleCheckbox(RenderContext<?> ctx, FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings) {
		return renderBeforeField(ctx, field, fieldMessages, parentMappings) +
			renderFieldBegin(ctx, field, false) +  // withoutLabel = false
			renderCheckboxes(ctx, field) + 
			renderFieldMessages(ctx, fieldMessages) + 
			renderFieldEnd(ctx, field) +
			renderAfterField(ctx, field, parentMappings);
	}

	protected <T> String renderBeforeField(RenderContext<?> ctx, FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings) {
		StringBuilder sb = new StringBuilder();
		// Form group begin
		sb.append(renderFormGroupBegin(ctx, field, getMaxSeverityClass(fieldMessages)));

		// Label
		boolean withoutLabel = false;
		if (!withoutLabel) {
			sb.append(renderFieldLabelBefore(ctx, field, parentMappings));
		}
		return sb.toString();
	}
	
	protected <T> String renderAfterField(RenderContext<?> ctx, FormField<T> field, ParentMappings parentMappings) {
		StringBuilder sb = new StringBuilder();
		boolean withoutLabel = false;
		if (!withoutLabel) {
			sb.append(renderFieldLabelAfter(ctx, field, parentMappings));
		}
		sb.append(renderFormGroupEnd(ctx, field)); // form-group
		return sb.toString();
	}
	
	protected String renderFormGroupBegin(RenderContext<?> ctx, FormField<?> field, String maxSeverityClass) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"form-group " + maxSeverityClass + "\" id=\"" + renderElementBoxId(ctx, field) + "\">" + newLine());
		if (field.getType().equals(FormComponent.CHECK_BOX.getType())) {
			sb.append("<div class=\"col-sm-offset-2 col-sm-10\">" + newLine());
			sb.append("<div class=\"checkbox\">" + newLine());
		}
		return sb.toString();
	}
	
	protected String renderFormGroupEnd(RenderContext<?> ctx, FormField<?> field) {
		StringBuilder sb = new StringBuilder();
		if (field.getType().equals(FormComponent.CHECK_BOX.getType())) {
			sb.append("</div>" + newLine());
			sb.append("</div>" + newLine());
		}
		sb.append("</div>" + newLine() + newLine());
		return sb.toString();
	}
	
	private String getMaxSeverityClass(List<ConstraintViolationMessage> fieldMessages) {
		Severity maxSeverity = Severity.max(fieldMessages);
		String maxSeverityClass = maxSeverity != null ? ("has-" + maxSeverity.getStyleClass()) : "";
		return maxSeverityClass;
	}
	
	private <T> String renderFieldInput(RenderContext<?> ctx, FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		String type = getFieldType(field);
		sb.append(renderInput(ctx, field, type));
		return sb.toString();
	}
	
	private String renderRequiredSign(RenderContext<?> ctx) {
		return "&nbsp;*";
	}
	
	private MessageTranslator createMessageTranslator(RenderContext<?> ctx, ParentMappings parentMappings) {
		return new MessageTranslator(
			parentMappings.getParentMapping().getDataClass(), ctx.getLocale(), 
			parentMappings.getRootMapping().getDataClass());
	}
	
	private <T> String renderLabelText(RenderContext<?> ctx, FormField<T> field, ParentMappings parentMappings) {
		StringBuilder sb = new StringBuilder();
		MessageTranslator tr = createMessageTranslator(ctx, parentMappings);
		sb.append(escapeHtml(tr.getMessage(field.getLabelKey(), ctx.getLocale())));
		if (field.isRequired()) {
			sb.append(renderRequiredSign(ctx));
		}
		return sb.toString();
	}
	
	private static final List<String> checkInputTypes = Arrays.asList(new String[] {
		FormComponent.CHECK_BOX.getType(),
		FormComponent.RADIO_CHOICE.getType()
	});
}
