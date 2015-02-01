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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

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
 * 
 * @author Radek Beran
 */
public class BasicFormRenderer {

	private final RenderContext ctx;

	public BasicFormRenderer(RenderContext ctx) {
		if (ctx == null) {
			throw new IllegalArgumentException("ctx cannot be null");
		}
		this.ctx = ctx;
	}

	public <T> String renderHtmlPage(FormMapping<T> formMapping) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>" + newLine());
		sb.append("<html lang=\"en\">" + newLine());
		sb.append("<head>" + newLine());
		sb.append("<meta charset=\"utf-8\">" + newLine());
		sb.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"
				+ newLine());
		sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
				+ newLine());
		sb.append("<title>Form rendering test</title>" + newLine());

		// Bootstrap CSS and JavaScript
		sb.append("<!-- Latest compiled and minified CSS -->" + newLine());
		sb.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css\">"
				+ newLine());

		sb.append("<!-- JQuery UI for datepicker -->" + newLine());
		sb.append("<link rel=\"stylesheet\" href=\"http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/ui-lightness/jquery-ui.min.css\">");
		sb.append("<!-- Optional theme -->" + newLine());
		sb.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css\">"
				+ newLine());

		sb.append("<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->"
				+ newLine());
		sb.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js\"></script>"
				+ newLine());
		sb.append("<!-- jQuery UI -->" + newLine());
		sb.append("<script src=\"http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js\"></script>"
				+ newLine());
		sb.append("<!-- Latest compiled and minified JavaScript -->"
				+ newLine());
		sb.append("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js\"></script>"
				+ newLine());

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
		sb.append(renderHtmlFormBegin());
		sb.append(renderGlobalMessages(formMapping));
		sb.append(renderMapping(formMapping));
		if (!containsSubmitButton(formMapping)) {
			sb.append(renderDefaultSubmitButton());
		}
		sb.append(renderHtmlFormEnd());
		return sb.toString();
	}

	public <T> String renderGlobalMessages(FormMapping<T> formMapping) {
		StringBuilder sb = new StringBuilder();
		ValidationResult validationResult = formMapping.getValidationResult();
		if (!validationResult.isEmpty() && !validationResult.isSuccess()) {
			sb.append("<div class=\"alert alert-danger\">" + newLine());
			sb.append("<div>Form contains validation errors.</div>" + newLine());
			for (ConstraintViolationMessage msg : validationResult
					.getGlobalMessages()) {
				sb.append(renderMessage(msg));
			}
			sb.append("</div>" + newLine());
		}
		return sb.toString();
	}

	public <T> String renderMapping(FormMapping<T> mapping) {
		StringBuilder sb = new StringBuilder();
		if (mapping.isVisible()) {
			sb.append(renderVisibleMapping(mapping));
		} else {
			sb.append(renderInvisibleElement(mapping));
		}
		return sb.toString();
	}

	public <T> String renderVisibleMapping(FormMapping<T> mapping) {
		StringBuilder sb = new StringBuilder();
		sb.append(newLine());
		// Form group
		sb.append(renderMappingBoxBeginTag(mapping));
		sb.append(renderMappingLabelAndMessages(mapping));
		if (mapping instanceof BasicListFormMapping) {
			for (FormMapping<?> m : ((BasicListFormMapping<?>) mapping)
					.getList()) {
				sb.append(renderMapping(m));
			}
		} else {
			for (FormElement el : mapping.getElements()) {
				if (el instanceof FormField) {
					FormField<?> field = (FormField<?>) el;
					sb.append(renderField(field));
				} else if (el instanceof FormMapping) {
					sb.append(renderMapping((FormMapping<?>) el));
				} else {
					throw new UnsupportedOperationException(
							"Unsupported form element "
									+ el.getClass().getName());
				}
			}
		}
		sb.append(renderMappingBoxEndTag(mapping));
		return sb.toString();
	}

	public <T> String renderField(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		String type = getFieldType(field);
		if (type != null && type.equals(FormFieldType.HIDDEN_FIELD.getTypeId())) {
			sb.append(renderFieldTypeHiddenField(field));
		} else if (field.isVisible()) {
			sb.append(renderVisibleField(field));
		} else {
			// Placeholder hidden div so the field can be made visible later and
			// placed to this reserved position
			sb.append(renderInvisibleElement(field));
		}
		return sb.toString();
	}

	public <T> String renderVisibleField(FormField<T> field) {
		String type = getFieldType(field);
		StringBuilder sb = new StringBuilder();
		FormFieldType formComponent = FormFieldType.findByTypeId(type);
		if (formComponent != null) {
			switch (formComponent) {
			case TEXT_FIELD:
				sb.append(renderFieldTypeText(field));
				break;
			case TEXT_AREA:
				sb.append(renderFieldTypeTextArea(field));
				break;
			case PASSWORD:
				sb.append(renderFieldTypePassword(field));
				break;
			case CHECK_BOX:
				sb.append(renderFieldTypeCheckBox(field));
				break;
			case DATE_PICKER:
				sb.append(renderFieldTypeDatePicker(field));
				break;
			case DROP_DOWN_CHOICE:
				sb.append(renderFieldTypeDropDownChoice(field));
				break;
			case FILE_UPLOAD:
				sb.append(renderFieldTypeFileUpload(field));
				break;
			case MULTIPLE_CHECK_BOX:
				sb.append(renderFieldTypeMultipleCheckbox(field));
				break;
			case MULTIPLE_CHOICE:
				sb.append(renderFieldTypeMultipleChoice(field));
				break;
			case RADIO_CHOICE:
				sb.append(renderFieldTypeRadioChoice(field));
				break;
			case COLOR:
				sb.append(renderFieldTypeColor(field));
				break;
			case DATE:
				sb.append(renderFieldTypeDate(field));
				break;
			case DATE_TIME:
				sb.append(renderFieldTypeDateTime(field));
				break;
			case DATE_TIME_LOCAL:
				sb.append(renderFieldTypeDateTimeLocal(field));
				break;
			case TIME:
				sb.append(renderFieldTypeTime(field));
				break;
			case EMAIL:
				sb.append(renderFieldTypeEmail(field));
				break;
			case MONTH:
				sb.append(renderFieldTypeMonth(field));
				break;
			case NUMBER:
				sb.append(renderFieldTypeNumber(field));
				break;
			case RANGE:
				sb.append(renderFieldTypeRange(field));
				break;
			case SEARCH:
				sb.append(renderFieldTypeSearch(field));
				break;
			case TEL:
				sb.append(renderFieldTypeTel(field));
				break;
			case URL:
				sb.append(renderFieldTypeUrl(field));
				break;
			case WEEK:
				sb.append(renderFieldTypeWeek(field));
				break;
			case SUBMIT_BUTTON:
				sb.append(renderFieldTypeSubmitButton(field));
				break;
			default:
				throw new UnsupportedOperationException(
						"Cannot render component with type " + type);
			}
		} else {
			throw new UnsupportedOperationException(
					"Cannot render component with type " + type);
		}
		return sb.toString();
	}

	/**
	 * Renders template to a string.
	 * 
	 * @param request
	 * @param response
	 * @param tplPath
	 * @return rendered template
	 */
	public String renderTemplate(final HttpServletRequest request,
			final HttpServletResponse response, final String tplPath) {
		String str = null;
		final StringWriter sw = new StringWriter();
		try {
			// final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(
					response) {
				@Override
				public PrintWriter getWriter() throws IOException {
					return new PrintWriter(sw);
				}

				@Override
				public ServletOutputStream getOutputStream() throws IOException {
					return new ServletOutputStream() {

						@Override
						public void write(int b) throws IOException {
							// nothing
						}
					};
				}
			};
			try {
				request.getRequestDispatcher(tplPath).include(request,
						responseWrapper);
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage(), ex);
			}
			str = sw.toString();
		} finally {
			try {
				sw.close();
			} catch (IOException e) {
				// in-memory writer closing error ignored
			}
		}
		return str;
	}

	public TdiResponseBuilder tdiResponse() {
		return createTdiResponseBuilder();
	}

	protected TdiResponseBuilder createTdiResponseBuilder() {
		return new TdiResponseBuilder(this);
	}

	protected String renderDefaultSubmitButton() {
		return renderFieldTypeSubmitButton(Forms.<String> field(
			PROPERTY_DEFAULT_SUBMIT,
			FormFieldType.SUBMIT_BUTTON.getTypeId()).build());
	}

	protected String renderInvisibleElement(FormElement element) {
		return "<div id=\"" + renderElementBoxId(element)
				+ "\" class=\"hidden\"></div>" + newLine();
	}

	protected <T> String renderHtmlFormBegin() {
		StringBuilder sb = new StringBuilder();
		sb.append("<form action=\"");
		sb.append(renderUrl(getRenderContext().getActionUrl()));
		sb.append("\" method=\"");
		sb.append(getRenderContext().getMethod().name());
		sb.append("\" class=\"form-horizontal\" role=\"form\">" + newLine());
		return sb.toString();
	}

	protected <T> String renderHtmlFormEnd() {
		return "</form>" + newLine();
	}

	protected <T> String renderMappingLabelAndMessages(FormMapping<T> mapping) {
		StringBuilder sb = new StringBuilder();

		// Label
		sb.append(renderMappingLabelElement(mapping));

		// Mapping messages
		sb.append(renderMessageList(mapping.getValidationMessages()));
		return sb.toString();
	}

	protected <T> String renderMappingBoxBeginTag(FormMapping<T> mapping) {
		String maxSeverityClass = getMaxSeverityClass(mapping
				.getValidationMessages());
		StringBuilder sb = new StringBuilder();
		sb.append("<div id=\"" + renderElementBoxId(mapping) + "\" class=\""
				+ maxSeverityClass + "\">" + newLine());
		return sb.toString();
	}

	protected <T> String renderMappingBoxEndTag(FormMapping<T> mapping) {
		return "</div>" + newLine();
	}

	protected <T> String renderUrl(String url) {
		return url;
	}

	protected String renderMessageList(
			List<ConstraintViolationMessage> fieldMessages) {
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
		sb.append("<div class=\"" + msg.getSeverity().getStyleClass() + "\">"
				+ escapeHtml(msg.getText()) + "</div>" + newLine());
		return sb.toString();
	}

	protected <T> String renderMappingLabelElement(FormMapping<T> mapping) {
		StringBuilder sb = new StringBuilder("");
		if (!mapping.isRootMapping()) {
			sb.append("<div class=\"" + getFormBlockClass() + "\">" + newLine());
			sb.append("<div class=\"" + getLabelIndentClass() + "\">"
					+ newLine());
			sb.append(renderLabelBeginTag(mapping));
			sb.append(renderLabelText(mapping));
			sb.append(":");
			sb.append(renderLabelEndTag(mapping));
			sb.append("</div>" + newLine());
			sb.append("</div>" + newLine());
		}
		return sb.toString();
	}

	protected <T> String renderHtmlLabel(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<label for=\"id-" + field.getName() + "\" class=\""
				+ getLabelIndentClass() + "\">");
		sb.append(renderLabelText(field));
		sb.append(":");
		sb.append(renderLabelEndTag(field));
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
	 * 
	 * @param s
	 * @return
	 */
	protected String escapeHtml(String s) {
		if (s == null)
			return null;
		if (s.isEmpty())
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	protected String renderElementBoxId(FormElement element) {
		return "box-" + element.getName();
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

	/**
	 * Renders AJAX attributes of TDI library.
	 * 
	 * @param ctx
	 * @param element
	 * @return
	 */
	protected String renderTdiAttributes(FormElement element) {
		StringBuilder sb = new StringBuilder();
		if (element.getDataAjaxUrl() != null
				&& !element.getDataAjaxUrl().isEmpty()) {
			sb.append(" class=\"tdi\"");
			sb.append(" data-ajax-url=\"" + element.getDataAjaxUrl() + "\"");
		}
		return sb.toString();
	}

	protected <T> String renderHtmlTextArea(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<textarea name=\"" + field.getName() + "\" id=\"id-"
				+ field.getName() + "\" class=\"input-sm form-control\"");
		sb.append(renderTdiAttributes(field));
		sb.append(renderAccessibilityAttributes(field) + ">");
		sb.append(renderValue(field.getValue()));
		sb.append("</textarea>" + newLine());
		return sb.toString();
	}

	protected <T> String renderHtmlInput(FormField<T> field) {
		String typeId = getFieldType(field);
		String htmlType = typeId;
		FormFieldType formComponent = FormFieldType.findByTypeId(typeId);
		if (formComponent != null) {
			htmlType = formComponent.getHtmlType();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<input type=\"" + htmlType + "\" name=\"" + field.getName()
				+ "\" id=\"id-" + field.getName() + "\"");
		if (!FormFieldType.FILE_UPLOAD.getTypeId().equals(typeId)) {
			String value = renderValue(field.getValue());
			sb.append(" value=\"" + value + "\"");
		}
		if (!FormFieldType.HIDDEN_FIELD.getTypeId().equals(typeId)) {
			sb.append(renderTdiAttributes(field));
			sb.append(renderAccessibilityAttributes(field));
		}
		if (isInputClassIncluded(typeId)) {
			sb.append(" class=\"input-sm form-control\"");
		}
		sb.append("/>" + newLine());
		return sb.toString();
	}

	protected <T> String renderHtmlCheckBox(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<input type=\"checkbox\" name=\"" + field.getName()
				+ "\" id=\"id-" + field.getName() + "\" value=\""
				+ renderValue("1") + "\"");
		if (field.getValue() != null && !field.getValue().isEmpty()) {
			String lc = field.getValue().toLowerCase();
			if (Boolean.valueOf(
					lc.equals("t") || lc.equals("y") || lc.equals("true")
							|| lc.equals("1")).booleanValue()) {
				sb.append(" checked=\"checked\" ");
			}
		}
		sb.append(renderTdiAttributes(field));
		sb.append(renderAccessibilityAttributes(field));
		sb.append("/>" + newLine());
		return sb.toString();
	}

	protected <T> String renderHtmlSelect(FormField<T> field, boolean multiple,
			Integer size) {
		StringBuilder sb = new StringBuilder();
		sb.append("<select name=\"" + field.getName() + "\" id=\"id-"
				+ field.getName() + "\"");
		if (multiple) {
			sb.append(" multiple=\"multiple\"");
		}
		if (size != null) {
			sb.append(" size=\"" + size + "\"");
		}
		sb.append(" class=\"input-sm form-control\"");
		sb.append(renderTdiAttributes(field));
		sb.append(renderAccessibilityAttributes(field));
		sb.append(">" + newLine());
		if (field.getChoiceProvider() != null
				&& field.getChoiceRenderer() != null) {
			List<?> items = field.getChoiceProvider().getItems();
			if (items != null) {
				ChoiceRenderer<Object> choiceRenderer = (ChoiceRenderer<Object>) field
						.getChoiceRenderer();
				int itemIndex = 0;
				for (Object item : items) {
					String value = renderValue(choiceRenderer.getId(item,
							itemIndex));
					String title = escapeHtml(choiceRenderer.getTitle(item,
							itemIndex));
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

	protected <T> String renderHtmlChecks(FormField<T> field) {
		String type = field.getType() != null
				&& field.getType().equals(
						FormFieldType.RADIO_CHOICE.getTypeId()) ? FormFieldType.RADIO_CHOICE
				.getTypeId() : FormFieldType.CHECK_BOX.getTypeId();
		StringBuilder sb = new StringBuilder();
		if (field.getChoiceProvider() != null
				&& field.getChoiceRenderer() != null) {
			List<?> items = field.getChoiceProvider().getItems();
			if (items != null) {
				ChoiceRenderer<Object> choiceRenderer = (ChoiceRenderer<Object>) field
						.getChoiceRenderer();
				int itemIndex = 0;
				for (Object item : items) {
					String value = renderValue(choiceRenderer.getId(item,
							itemIndex));
					String title = escapeHtml(choiceRenderer.getTitle(item,
							itemIndex));

					sb.append("<div class=\"" + type + "\">" + newLine());
					sb.append("<label><input type=\"" + type + "\" name=\""
							+ field.getName() + "\" value=\"" + value + "\"");
					if (field.getFilledObjects().contains(item)) {
						sb.append(" checked=\"checked\"");
					}
					sb.append(renderTdiAttributes(field));
					sb.append(renderAccessibilityAttributes(field));
					sb.append("/> " + title + renderLabelEndTag(field));
					sb.append("</div>" + newLine());
					itemIndex++;
				}
			}
		}
		return sb.toString();
	}

	protected <T> String renderFieldBegin(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"col-sm-4\">" + newLine());
		return sb.toString();
	}

	protected <T> String renderFieldEnd(FormField<T> field) {
		return "</div>" + newLine();
	}

	protected <T> String renderDatePickerJavaScript(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<script>" + newLine());
		sb.append("$(function(){" + newLine());
		sb.append("	$('#id-" + field.getName()
				+ "').datepicker({ dateFormat: \""
				+ getDatePickerPattern(field) + "\" });" + newLine());
		sb.append("});" + newLine());
		sb.append("</script>" + newLine());
		return sb.toString();
	}

	protected <T> String getDatePickerPattern(FormField<T> field) {
		return "d.m.yy";
	}

	protected <T> String renderTextFieldInternal(FormField<T> field) {
		return renderFieldBoxBeginTag(field) + renderHtmlLabel(field)
				+ renderFieldBegin(field) + renderHtmlInput(field)
				+ renderMessageList(field.getValidationMessages())
				+ renderFieldEnd(field) + renderFieldBoxEndTag(field);
	}

	// --- Various field types - begin ---

	protected <T> String renderFieldTypeSubmitButton(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"" + getFormBlockClass() + "\">" + newLine());
		sb.append("<div class=\"" + getInputIndentClass() + "\">" + newLine());
		sb.append("<button type=\"submit\" value=\""
				+ renderValue(field.getValue())
				+ "\" class=\"btn btn-default\">");
		String text = null;
		if (field.getLabelKey() != null
				&& !field.getLabelKey().equals(PROPERTY_DEFAULT_SUBMIT)) {
			MessageTranslator tr = createMessageTranslator(field);
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

	protected <T> String renderFieldTypeHiddenField(FormField<T> field) {
		return renderHtmlInput(field) + newLine();
	}

	protected <T> String renderFieldTypeText(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeColor(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeDate(FormField<T> field) {
		// TODO: Support for min, max attributes
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeDateTime(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeDateTimeLocal(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeTime(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeEmail(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeMonth(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeNumber(FormField<T> field) {
		// TODO: Support for min, max, step attributes
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeRange(FormField<T> field) {
		// TODO: Support for min, max attributes
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeSearch(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeTel(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeUrl(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeWeek(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTypeTextArea(FormField<T> field) {
		return renderFieldBoxBeginTag(field) + renderHtmlLabel(field)
				+ renderFieldBegin(field) + renderHtmlTextArea(field)
				+ renderMessageList(field.getValidationMessages())
				+ renderFieldEnd(field) + renderFieldBoxEndTag(field);
	}

	protected <T> String renderFieldTypeCheckBox(FormField<T> field) {
		return renderCheckBoxBegin(field) + renderHtmlCheckBox(field)
				+ renderMessageList(field.getValidationMessages())
				+ renderCheckBoxEnd(field);
	}

	protected <T> String renderFieldTypePassword(FormField<T> field) {
		return renderFieldBoxBeginTag(field) + renderHtmlLabel(field)
				+ renderFieldBegin(field) + renderHtmlInput(field)
				+ renderMessageList(field.getValidationMessages())
				+ renderFieldEnd(field) + renderFieldBoxEndTag(field);
	}

	protected <T> String renderFieldTypeFileUpload(FormField<T> field) {
		return renderFieldBoxBeginTag(field) + renderHtmlLabel(field)
				+ renderFieldBegin(field) + renderHtmlInput(field)
				+ renderMessageList(field.getValidationMessages())
				+ renderFieldEnd(field) + renderFieldBoxEndTag(field);
	}

	protected <T> String renderFieldTypeDatePicker(FormField<T> field) {
		return renderFieldBoxBeginTag(field) + renderHtmlLabel(field)
				+ renderFieldBegin(field) + renderHtmlInput(field)
				+ renderDatePickerJavaScript(field)
				+ renderMessageList(field.getValidationMessages())
				+ renderFieldEnd(field) + renderFieldBoxEndTag(field);
	}

	protected <T> String renderFieldTypeDropDownChoice(FormField<T> field) {
		return renderFieldBoxBeginTag(field) + renderHtmlLabel(field)
				+ renderFieldBegin(field)
				+ renderHtmlSelect(field, false, null)
				+ renderMessageList(field.getValidationMessages())
				+ renderFieldEnd(field) + renderFieldBoxEndTag(field);
	}

	protected <T> String renderFieldTypeMultipleChoice(FormField<T> field) {
		return renderFieldBoxBeginTag(field) + renderHtmlLabel(field)
				+ renderFieldBegin(field) + renderHtmlSelect(field, true, null)
				+ renderMessageList(field.getValidationMessages())
				+ renderFieldEnd(field) + renderFieldBoxEndTag(field);
	}

	protected <T> String renderFieldTypeMultipleCheckbox(FormField<T> field) {
		return renderFieldBoxBeginTag(field) + renderHtmlLabel(field)
				+ renderFieldBegin(field) + renderHtmlChecks(field)
				+ renderMessageList(field.getValidationMessages())
				+ renderFieldEnd(field) + renderFieldBoxEndTag(field);
	}

	protected <T> String renderFieldTypeRadioChoice(FormField<T> field) {
		return renderFieldBoxBeginTag(field) + renderHtmlLabel(field)
				+ renderFieldBegin(field) + renderHtmlChecks(field)
				+ renderMessageList(field.getValidationMessages())
				+ renderFieldEnd(field) + renderFieldBoxEndTag(field);
	}

	// --- /Various field types - end ---

	protected <T> String renderCheckBoxBegin(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		// Form group begin
		sb.append(renderFieldBoxBeginTag(field));
		sb.append(renderLabelBeginTag(field));
		return sb.toString();
	}

	protected <T> String renderCheckBoxEnd(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append(renderLabelText(field));
		sb.append(renderLabelEndTag(field));
		sb.append(renderFieldBoxEndTag(field));
		return sb.toString();
	}

	protected <T> String renderLabelBeginTag(FormElement formElement) {
		return "<label>" + newLine();
	}

	protected <T> String renderLabelEndTag(FormElement formElement) {
		return "</label>" + newLine();
	}

	protected <T> String renderFieldBoxBeginTag(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		String maxSeverityClass = getMaxSeverityClass(field
				.getValidationMessages());
		sb.append("<div id=\"" + renderElementBoxId(field) + "\" class=\""
				+ getFormBlockClass() + " " + maxSeverityClass + "\">"
				+ newLine());
		if (field.getType() != null
				&& field.getType().equals(FormFieldType.CHECK_BOX.getTypeId())) {
			sb.append("<div class=\"" + getInputIndentClass() + "\">"
					+ newLine());
			sb.append("<div class=\"" + FormFieldType.CHECK_BOX.getHtmlType()
					+ "\">" + newLine());
		}
		return sb.toString();
	}

	protected <T> String renderFieldBoxEndTag(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		if (field.getType() != null
				&& field.getType().equals(FormFieldType.CHECK_BOX.getTypeId())) {
			sb.append("</div>" + newLine());
			sb.append("</div>" + newLine());
		}
		sb.append("</div>" + newLine() + newLine());
		return sb.toString();
	}

	protected <T> String renderLabelText(FormElement formElement) {
		StringBuilder sb = new StringBuilder();
		MessageTranslator tr = createMessageTranslator(formElement);
		String msgKey = formElement.getLabelKey();
		if (formElement instanceof FormMapping) {
			FormMapping<?> m = (FormMapping<?>) formElement;
			if (m.getIndex() != null) {
				msgKey = msgKey + Forms.PATH_SEP + "singular";
			}
		}
		sb.append(escapeHtml(tr.getMessage(msgKey, getRenderContext()
				.getLocale())));
		if (formElement instanceof BasicListFormMapping) {
			FormMapping<?> listMapping = (FormMapping<?>) formElement;
			sb.append(" (" + listMapping.getList().size() + ")");
		}
		if (formElement.isRequired()) {
			sb.append(renderRequiredMark(formElement));
		}
		return sb.toString();
	}

	protected <T> String renderRequiredMark(FormElement formElement) {
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

	protected <T> boolean containsSubmitButton(FormMapping<T> mapping) {
		// searching only on top level
		for (FormField<?> field : mapping.getFields().values()) {
			if (field.getType() != null
					&& field.getType().equals(
							FormFieldType.SUBMIT_BUTTON.getTypeId())) {
				return true;
			}
		}
		return false;
	}

	protected RenderContext getRenderContext() {
		return ctx;
	}

	private <T> MessageTranslator createMessageTranslator(
			FormElement formElement) {
		FormMapping<?> rootMapping = getRootMapping(formElement);
		return new MessageTranslator(formElement.getParent().getDataClass(),
				getRenderContext().getLocale(), rootMapping.getDataClass());
	}

	private FormMapping<?> getRootMapping(FormElement formElement) {
		FormMapping<?> rootMapping = formElement.getParent();
		while (rootMapping != null && rootMapping.getParent() != null) {
			rootMapping = rootMapping.getParent();
		}
		return rootMapping;
	}

	private String newLine() {
		return System.getProperty("line.separator");
	}

	private boolean isInputClassIncluded(String type) {
		return type != null
				&& !type.equals(FormFieldType.FILE_UPLOAD.getTypeId())
				&& !type.equals(FormFieldType.HIDDEN_FIELD.getTypeId())
				&& !type.equals(FormFieldType.CHECK_BOX.getTypeId());
	}

	private <T> String getFieldType(FormField<T> field) {
		String type = field.getType();
		if (type == null) {
			type = "text";
		}
		return type.toLowerCase();
	}

	private String getMaxSeverityClass(
			List<ConstraintViolationMessage> fieldMessages) {
		Severity maxSeverity = Severity.max(fieldMessages);
		String maxSeverityClass = maxSeverity != null ? ("has-" + maxSeverity
				.getStyleClass()) : "";
		return maxSeverityClass;
	}

	private final String PROPERTY_DEFAULT_SUBMIT = "_defaultSubmitButton";
}
