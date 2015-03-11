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

import java.util.Locale;

import net.formio.Field;
import net.formio.FormElement;
import net.formio.FormField;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.common.MessageTranslator;
import net.formio.validation.Severity;

/**
 * Context with common data for rendering a form.
 * @author Radek Beran
 */
public class RenderContext {
	private Locale locale;
	private FormMethod method;
	private String actionUrl;
	
	public RenderContext() {
		this(Locale.getDefault());
	}
	
	public RenderContext(Locale locale) {
		this(locale, FormMethod.POST, "#");
	}
	
	private RenderContext(Locale locale, FormMethod method, String actionUrl) {
		if (locale == null) {
			throw new IllegalArgumentException("locale cannot be null");
		}
		this.locale = locale;
		this.method = method;
		this.actionUrl = actionUrl;
	}

	public FormMethod getMethod() {
		return method;
	}

	public void setMethod(FormMethod method) {
		this.method = method;
	}

	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
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
	
	protected String escapeValue(String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}
		return escapeHtml(value);
	}
	
	protected <T> MessageTranslator createMessageTranslator(FormElement<T> element) {
		FormMapping<?> rootMapping = getRootMapping(element);
		return new MessageTranslator(element.getParent().getDataClass(),
			getLocale(), rootMapping.getDataClass());
	}
	
	protected String getFormBoxClasses() {
		return "form-group";
	}

	protected String getLabelClasses() {
		return "control-label col-sm-" + getLabelWidth();
	}
	
	protected <T> String getInputEnvelopeClasses(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		boolean withoutLeadingLabel = isWithoutLeadingLabel(field);
		if (withoutLeadingLabel) {
			sb.append("col-sm-offset-" + getLabelWidth());
		}
		if (sb.length() > 0) {
			sb.append(" ");
		}
		sb.append("col-sm-4");
		return sb.toString();
	}
	
	/**
	 * Returns value of class attribute for the input of given form field.
	 * @param field
	 * @return
	 */
	protected <T> String getInputClasses(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		boolean customJsEventsServed = field.getProperties().getDataAjaxEvents() != null && 
			field.getProperties().getDataAjaxEvents().length > 0;
		if (field.getProperties().getDataAjaxUrl() != null && 
			!field.getProperties().getDataAjaxUrl().isEmpty() && 
			!customJsEventsServed) {
			sb.append("tdi");
		}
		if (isFullWidthInput(field)) {
			sb.append(" " + getFullWidthInputClasses());
		}
		String type = getFieldType(field);
		Field fld = Field.findByType(type);
		if (fld != null && type.equals(Field.SUBMIT_BUTTON.getType())) {
			sb.append(" " + getButtonClasses(field));
		}
		return sb.toString();
	}
	
	protected String getFullWidthInputClasses() {
		return "input-sm form-control";
	}
	
	protected <T> String getMaxSeverityClass(FormElement<T> el) {
		Severity maxSeverity = Severity.max(el.getValidationMessages());
		return maxSeverity != null ? ("has-" + maxSeverity.getStyleClass()) : "";
	}
	
	protected <T> String getButtonClasses(FormField<T> field) {
		return "btn btn-default";
	}
	
	String getElementId(FormElement<?> element) {
		return getIdForName(element.getName());
	}
	
	String getIdForName(String name) {
		return "id" + Forms.PATH_SEP + name;
	}
	
	<T> String getFieldType(FormField<T> field) {
		String type = field.getType();
		if (type == null) {
			type = Field.TEXT.getType();
		}
		return type.toLowerCase();
	}
	
	<T> String getElementIdWithIndex(FormField<T> field, int itemIndex) {
		return getElementId(field) + Forms.PATH_SEP + itemIndex;
	}
	
	private int getLabelWidth() {
		return 2;
	}

	private <T> boolean isWithoutLeadingLabel(FormField<T> field) {
		return Field.SUBMIT_BUTTON.getType().equals(field.getType()) || 
			Field.CHECK_BOX.getType().equals(field.getType()) ||
			!field.getProperties().isLabelVisible();
	}
	
	private <T> FormMapping<?> getRootMapping(FormElement<T> element) {
		FormMapping<?> rootMapping = element.getParent();
		while (rootMapping != null && rootMapping.getParent() != null) {
			rootMapping = rootMapping.getParent();
		}
		return rootMapping;
	}
	
	private <T> boolean isFullWidthInput(FormField<T> field) {
		String type = getFieldType(field);
		Field fld = Field.findByType(type);
		return !type.equals(Field.FILE_UPLOAD.getType()) // otherwise border around field with "Browse" text is drawn
			&& !type.equals(Field.HIDDEN.getType())
			&& !type.equals(Field.CHECK_BOX.getType())
			&& !type.equals(Field.SUBMIT_BUTTON.getType())
			&& (fld == null || !Field.withMultipleInputs.contains(fld));
	}
	
	String newLine() {
		return System.getProperty("line.separator");
	}
}
