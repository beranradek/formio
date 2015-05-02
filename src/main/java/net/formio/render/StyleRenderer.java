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

import java.util.Arrays;
import java.util.List;

import net.formio.Field;
import net.formio.FormElement;
import net.formio.FormField;
import net.formio.ajax.action.HandledJsEvent;
import net.formio.validation.Severity;

/**
 * Renders CSS styles of forms.
 * @author Radek Beran
 */
class StyleRenderer {
	private final BasicFormRenderer renderer;
	private final RenderContext ctx;

	StyleRenderer(BasicFormRenderer renderer, RenderContext ctx) {
		if (renderer == null) {
			throw new IllegalArgumentException("renderer cannot be null");
		}
		if (ctx == null) {
			throw new IllegalArgumentException("ctx cannot be null");
		}
		this.renderer = renderer;
		this.ctx = ctx;
	}
	
	protected String getFormBoxClasses() {
		return "form-group";
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
	
	protected String getLabelClasses() {
		return "control-label col-sm-" + getLabelWidth();
	}
	
	/**
	 * Returns value of class attribute for the input of given form field.
	 * @param field
	 * @return
	 */
	protected <T> String getInputClasses(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		List<HandledJsEvent> ajaxEvents = Arrays.asList(field.getProperties().getDataAjaxActions());
		for (HandledJsEvent e : ajaxEvents) {
			if (e.getEvent() == null) {
				sb.append("tdi");
				break;
			}
		}
		if (isFullWidthInput(field)) {
			sb.append(" " + getFullWidthInputClasses());
		}
		String type = ctx.getFieldType(field);
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
	
	protected <T> String getButtonClasses(@SuppressWarnings("unused") FormField<T> field) {
		return "btn btn-default";
	}
	
	private int getLabelWidth() {
		return 2;
	}

	private <T> boolean isWithoutLeadingLabel(FormField<T> field) {
		return Field.SUBMIT_BUTTON.getType().equals(field.getType()) || 
			Field.CHECK_BOX.getType().equals(field.getType()) ||
			!field.getProperties().isLabelVisible();
	}
	
	private <T> boolean isFullWidthInput(FormField<T> field) {
		String type = ctx.getFieldType(field);
		Field fld = Field.findByType(type);
		return !type.equals(Field.FILE_UPLOAD.getType()) // otherwise border around field with "Browse" text is drawn
			&& !type.equals(Field.HIDDEN.getType())
			&& !type.equals(Field.CHECK_BOX.getType())
			&& !type.equals(Field.SUBMIT_BUTTON.getType())
			&& (fld == null || !Field.withMultipleInputs.contains(fld));
	}
}
