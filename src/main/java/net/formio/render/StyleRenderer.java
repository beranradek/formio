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
import net.formio.FormField;
import net.formio.ajax.action.AjaxHandler;
import net.formio.props.types.InlinePosition;

/**
 * Renders CSS styles of forms.
 * @author Radek Beran
 */
class StyleRenderer {
	private final FormRenderer renderer;

	StyleRenderer(FormRenderer renderer) {
		if (renderer == null) {
			throw new IllegalArgumentException("renderer cannot be null");
		}
		this.renderer = renderer;
	}
	
	protected String getFormGroupClasses() {
		return "form-group";
	}
	
	protected <T> String getInputEnvelopeClasses(FormField<T> field) {
		Integer colInputWidth = field.getProperties().getColInputWidth();
		if (colInputWidth == null) {
			colInputWidth = Integer.valueOf(field.getParent().getConfig().getColInputWidth());
		}
		int colLabelWidth = field.getParent().getConfig().getColLabelWidth();
		if (field.getProperties().getColLabelWidth() != null) {
			colLabelWidth = field.getProperties().getColLabelWidth().intValue(); 
		}
		InlinePosition inlinePos = field.getProperties().getInline();
		StringBuilder sb = new StringBuilder();
		boolean withoutLeadingLabel = isWithoutLeadingLabel(field);
		if (withoutLeadingLabel && (inlinePos == null || inlinePos == InlinePosition.FIRST)) {
			sb.append(getColOffsetClassPrefix()).append(colLabelWidth).append(" ");
		}
		sb.append(getColWidthClassPrefix()).append(colInputWidth);
		return sb.toString();
	}
	
	protected String getLabelClasses() {
		return "control-label";
	}
	
	protected String getColOffsetClassPrefix() {
		return "col-sm-offset-";
	}
	
	protected String getColWidthClassPrefix() {
		return "col-sm-";
	}
	
	/**
	 * Returns value of class attribute for the input of given form field.
	 * @param field
	 * @return
	 */
	protected <T> String getInputClasses(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		AjaxHandler<?> handlerWithoutEvent = field.getProperties().getAjaxHandlerWithoutEvent();
		if (handlerWithoutEvent != null) {
			sb.append("tdi");
		}
		if (isFullWidthInput(field)) {
			sb.append(" ").append(getFullWidthInputClasses());
		}
		if (Field.BUTTON.getType().equals(field.getType())) {
			sb.append(" ").append(getButtonClasses(field));
		}
		return sb.toString();
	}

	private <T> boolean isWithoutLeadingLabel(FormField<T> field) {
		return Field.BUTTON.getType().equals(field.getType()) || 
			Field.CHECK_BOX.getType().equals(field.getType()) ||
			Field.LINK.getType().equals(field.getType()) ||
			!field.getProperties().isLabelVisible();
	}
	
	private <T> boolean isFullWidthInput(FormField<T> field) {
		String type = field.getType();
		return !Field.FILE_UPLOAD.getType().equals(type) // otherwise border around field with "Browse" text is drawn
			&& !Field.HIDDEN.getType().equals(type)
			&& !Field.CHECK_BOX.getType().equals(type)
			&& !Field.BUTTON.getType().equals(type)
			&& !Field.MULTIPLE_CHECK_BOX.getType().equals(type)
			&& !Field.RADIO_CHOICE.getType().equals(type)
			&& !Field.LINK.getType().equals(type);
	}
	
	private String getFullWidthInputClasses() {
		return "input-sm form-control";
	}
	
	private <T> String getButtonClasses(@SuppressWarnings("unused") FormField<T> field) {
		return "btn btn-default";
	}
}
