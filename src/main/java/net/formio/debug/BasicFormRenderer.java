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
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.formio.FormComponent;
import net.formio.FormElement;
import net.formio.FormField;
import net.formio.FormMapping;
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
		sb.append(renderGlobalMessages(ctx.getFilledForm().getValidationResult()));
		sb.append(renderMapping(ctx, ctx.getFilledForm(), new ParentMappings(ctx.getFilledForm(), null)));
		sb.append(renderSubmit());
		sb.append("</form>" + newLine());
		return sb.toString();
	}
	
	@Override
	public String renderGlobalMessages(ValidationResult validationResult) {
		StringBuilder sb = new StringBuilder();
		if (!validationResult.isEmpty() && !validationResult.isSuccess()) {
			sb.append("<div class=\"alert alert-danger\">" + newLine());
			sb.append("<div>Form contains validation errors.</div>" + newLine());
			for (ConstraintViolationMessage msg : validationResult.getGlobalMessages()) {
				sb.append(renderMessage(msg));	
			}
			sb.append("</div>" + newLine());
		}
		return sb.toString();
	}
	
	@Override
	public <T> String renderMapping(RenderContext<?> ctx, FormMapping<T> mapping, ParentMappings parentMappings) {
		StringBuilder sb = new StringBuilder();
		if (mapping.isVisible()) {
			sb.append("<div id=\"" + renderElementBoxId(mapping) + "\">" + newLine());
			ParentMappings nestedParents = new ParentMappings(parentMappings.getRootMapping(), mapping);
			for (Map.Entry<String, FormField<?>> e : mapping.getFields().entrySet()) {
				FormField<?> field = e.getValue();
				sb.append(renderField(ctx, field, mapping.getValidationResult().getFieldMessages().get(field.getName()), nestedParents));
			}
			for (Map.Entry<String, FormMapping<?>> e : mapping.getNested().entrySet()) {
				FormMapping<?> nestedMapping = e.getValue();
				sb.append(renderMapping(ctx, nestedMapping, nestedParents));
			}
			sb.append("</div>" + newLine());
		} else {
			sb.append(renderHiddenDivElement(mapping));
		}
		return sb.toString();
	}

	@Override
	public <T> String renderField(RenderContext<?> ctx, FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings) {
		StringBuilder sb = new StringBuilder();
		String type = getFieldType(field);
		if (type.equals(FormComponent.HIDDEN_FIELD.getType())) {
			sb.append(renderHiddenField(field));
		} else if (field.isVisible()) {
			if (type.equals(FormComponent.TEXT_FIELD.getType())) {
				sb.append(renderTextField(field, fieldMessages, parentMappings, ctx.getLocale()));
			} else if (type.equals(FormComponent.TEXT_AREA.getType())) {
				sb.append(renderTextArea(field, fieldMessages, parentMappings, ctx.getLocale()));
			} else if (type.equals(FormComponent.PASSWORD.getType())) {
				sb.append(renderPassword(field, fieldMessages, parentMappings, ctx.getLocale()));
			}
		} else {
			// Placeholder hidden div so the field can be made visible later and placed to this reserved position
			sb.append(renderHiddenDivElement(field));
		}
		return sb.toString();
	}
	
	@Override
	public String renderSubmit() {
		return "<button type=\"submit\">Submit</button>" + newLine();
	}
	
	protected <T> String renderFormTag(RenderContext<T> ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append("<form action=\"");
		sb.append(renderUrl(ctx.getActionUrl()));
		sb.append("\" method=\"");
		sb.append(ctx.getMethod().name());
		sb.append("\" class=\"form-horizontal\" role=\"form\">" + newLine());
		return sb.toString();
	}
	
	protected String renderUrl(String url) {
		return url;
	}

	protected String renderFieldMessages(List<ConstraintViolationMessage> fieldMessages) {
		StringBuilder sb = new StringBuilder();
		if (fieldMessages != null && !fieldMessages.isEmpty()) {
			for (ConstraintViolationMessage msg : fieldMessages) {
				sb.append(renderMessage(msg));
			}
		}
		return sb.toString();
	}
	
	protected String renderMessage(ConstraintViolationMessage msg) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"" + msg.getSeverity().getStyleClass() + "\">" + escapeHtml(msg.getText()) + "</div>" + newLine());
		return sb.toString();
	}
	
	protected <T> String renderFieldLabelBefore(FormField<T> field, ParentMappings parentMappings, Locale locale) {
		StringBuilder sb = new StringBuilder();
		String type = getFieldType(field);
		if (checkInputTypes.contains(type)) {
			sb.append("<label>" + newLine());
		} else {
			sb.append("<label class=\"control-label col-sm-2\" for=\"id-" + field.getName() + "\">");
			sb.append(renderLabelText(field, parentMappings, locale));
			sb.append(":");
			sb.append("</label>" + newLine());
		}
		return sb.toString();
	}
	
	protected <T> String renderFieldLabelAfter(FormField<T> field, ParentMappings parentMappings, Locale locale) {
		StringBuilder sb = new StringBuilder("");
		String type = getFieldType(field);
		if (checkInputTypes.contains(type)) {
			sb.append(renderLabelText(field, parentMappings, locale));
			sb.append("</label>" + newLine());
		}
		return sb.toString();
	}

	protected String renderValue(String value) {
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
	
	protected String renderHiddenDivElement(FormElement element) {
		return "<div id=\"" + renderElementBoxId(element) + "\" class=\"hidden\"></div>" + newLine();
	}
	
	protected String renderElementBoxId(FormElement element) {
		return "box-" + element.getName();
	}
	
	<T> String renderHtmlPage(FormMapping<T> filledForm, FormMethod method, String actionUrl, Locale locale) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><title>Form rendering test</title></head><body>" + newLine());
		RenderContext<T> ctx = new RenderContext<T>();
		ctx.setFilledForm(filledForm);
		ctx.setMethod(method);
		ctx.setActionUrl(actionUrl);
		ctx.setLocale(locale);
		sb.append(renderForm(ctx));
		sb.append("</body></html>" + newLine());
		return sb.toString();
	}
	
	protected String renderAccessibilityAttributes(FormElement element) {
		StringBuilder sb = new StringBuilder();
		if (!element.isEnabled()) {
			sb.append(" disabled=\"disabled\"");
		}
		if (element.isReadonly()) {
			sb.append(" readonly=\"readonly\"");
		}
		return sb.toString();
	}
	
	protected String renderTextarea(FormField<?> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<textarea class=\"input-sm form-control\" name=\"" + field.getName() + "\" id=\"id-" + field.getName() + "\"");
		sb.append(renderAccessibilityAttributes(field) + ">");
		sb.append(renderValue(field.getValue()));
		sb.append("</textarea>" + newLine());
		return sb.toString();
	}	
	
	protected String renderInput(FormField<?> field, String type) {
		if (type == null) throw new IllegalArgumentException("type cannot be null");
		StringBuilder sb = new StringBuilder();
		sb.append("<input class=\"");
		if (isInputClassIncluded(type)) {
			sb.append("input-sm form-control");
		}
		String value = "";
		if (checkInputTypes.contains(type)) {
			// TODO: itemId
			String itemId = "1";
			if (contains(field.getFilledObjects(), itemId)) {
				sb.append("checked=\"checked\" ");
			}
			value = renderValue(itemId);
		} else {
			value = field.getValue();
		}
		sb.append("\" type=\"" + type + "\" name=\"" + field.getName() + "\" id=\"id-" + field.getName() + "\" value=\"" + value + "\" ");
		if (!type.equals(FormComponent.HIDDEN_FIELD.getType())) {
			sb.append(renderAccessibilityAttributes(field));
		}
		sb.append("/>" + newLine());
		return sb.toString();
	}

	protected boolean isInputClassIncluded(String type) {
		return !type.equals(FormComponent.FILE_UPLOAD.getType()) && !type.equals(FormComponent.HIDDEN_FIELD.getType());
	}
	
	protected String renderFieldBegin(boolean withoutLabel) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"");
		if (withoutLabel) {
			// offset instead of label
			sb.append("col-sm-offset-2 ");
		}
		sb.append("col-sm-4\">" + newLine());
		return sb.toString();
	}
	
	protected String renderFieldEnd() {
		return "</div>" + newLine();
	}
	
	protected <T> String getFieldType(FormField<T> field) {
		String type = field.getType();
		if (type == null) {
			type = "text";
		}
		return type.toLowerCase();
	}
	
	protected <T> String renderHiddenField(FormField<T> field) {
		return renderInput(field, FormComponent.HIDDEN_FIELD.getType());
	}
	
	protected <T> String renderTextField(FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings, Locale locale) {
		return renderBeforeField(field, fieldMessages, parentMappings, locale) +
			renderFieldBegin(false) +  // withoutLabel = false
			renderFieldInput(field) + 
			renderFieldMessages(fieldMessages) + 
			renderFieldEnd() +
			renderAfterField(field, parentMappings, locale);
	}
	
	protected <T> String renderTextArea(FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings, Locale locale) {
		return renderBeforeField(field, fieldMessages, parentMappings, locale) +
			renderFieldBegin(false) +  // withoutLabel = false
			renderFieldInput(field) + 
			renderFieldMessages(fieldMessages) + 
			renderFieldEnd() +
			renderAfterField(field, parentMappings, locale);
	}
	
	protected <T> String renderPassword(FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings, Locale locale) {
		return renderBeforeField(field, fieldMessages, parentMappings, locale) +
			renderFieldBegin(false) +  // withoutLabel = false
			renderFieldInput(field) + 
			renderFieldMessages(fieldMessages) + 
			renderFieldEnd() +
			renderAfterField(field, parentMappings, locale);
	}

	protected <T> String renderBeforeField(FormField<T> field, List<ConstraintViolationMessage> fieldMessages, ParentMappings parentMappings, Locale locale) {
		StringBuilder sb = new StringBuilder();
		// Form group begin
		sb.append(renderFormGroupBegin(field, getMaxSeverityClass(fieldMessages)));

		// Label
		boolean withoutLabel = false;
		if (!withoutLabel) {
			sb.append(renderFieldLabelBefore(field, parentMappings, locale));
		}
		return sb.toString();
	}
	
	protected <T> String renderAfterField(FormField<T> field, ParentMappings parentMappings, Locale locale) {
		StringBuilder sb = new StringBuilder();
		boolean withoutLabel = false;
		if (!withoutLabel) {
			sb.append(renderFieldLabelAfter(field, parentMappings, locale));
		}
		sb.append(renderFormGroupEnd()); // form-group
		return sb.toString();
	}
	
	protected String renderFormGroupBegin(FormField<?> field, String maxSeverityClass) {
		return "<div class=\"form-group " + maxSeverityClass + "\" id=\"" + renderElementBoxId(field) + "\">" + newLine();
	}
	
	protected String renderFormGroupEnd() {
		return "</div>" + newLine();
	}
	
	private String getMaxSeverityClass(List<ConstraintViolationMessage> fieldMessages) {
		Severity maxSeverity = Severity.max(fieldMessages);
		String maxSeverityClass = maxSeverity != null ? ("has-" + maxSeverity.getStyleClass()) : "";
		return maxSeverityClass;
	}
	
	private <T> String renderFieldInput(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		String type = getFieldType(field);
		if (type.equals(FormComponent.TEXT_AREA.getType())) {
			sb.append(renderTextarea(field));
		} else if (inputTypes.contains(type) || checkInputTypes.contains(type)) {
			sb.append(renderInput(field, type));
		}
		return sb.toString();
	}
	
	private String renderRequiredSign() {
		return "&nbsp;*";
	}
	
	private MessageTranslator createMessageTranslator(ParentMappings parentMappings, Locale locale) {
		return new MessageTranslator(
			parentMappings.getParentMapping().getDataClass(), locale, 
			parentMappings.getRootMapping().getDataClass());
	}
	
	private <T> String renderLabelText(FormField<T> field, ParentMappings parentMappings, Locale locale) {
		StringBuilder sb = new StringBuilder();
		MessageTranslator tr = createMessageTranslator(parentMappings, locale);
		sb.append(escapeHtml(tr.getMessage(field.getLabelKey(), locale)));
		if (field.isRequired()) {
			sb.append(renderRequiredSign());
		}
		return sb.toString();
	}
	
	private boolean contains(Collection<?> col, Object o) {
		return col != null && col.contains(o);
	}
	
	private static final List<String> inputTypes = Arrays.asList(new String[] { 
		FormComponent.TEXT_FIELD.getType(), 
		FormComponent.PASSWORD.getType(),
		FormComponent.HIDDEN_FIELD.getType(), 
		FormComponent.FILE_UPLOAD.getType()
	});
	
	private static final List<String> checkInputTypes = Arrays.asList(new String[] {
		FormComponent.CHECK_BOX.getType(),
		FormComponent.RADIO_CHOICE.getType()
	});
}
