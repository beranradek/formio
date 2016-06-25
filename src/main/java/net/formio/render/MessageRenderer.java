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

import net.formio.FormElement;
import net.formio.FormMapping;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.ValidationResult;

/**
 * Renders validation messages.
 * @author Radek Beran
 */
class MessageRenderer {
	private final FormRenderer renderer;

	MessageRenderer(FormRenderer renderer) {
		if (renderer == null) {
			throw new IllegalArgumentException("renderer cannot be null");
		}
		this.renderer = renderer;
	}
	
	protected <T> String renderGlobalMessages(FormMapping<T> formMapping) {
		StringBuilder sb = new StringBuilder();
		ValidationResult validationResult = formMapping.getValidationResult();
		if (!validationResult.isEmpty() && !validationResult.isSuccess()) {
			sb.append("<div class=\"alert alert-danger\">").append(renderer.newLine());
			sb.append("<div>Form contains validation errors.</div>").append(renderer.newLine());
			for (ConstraintViolationMessage msg : validationResult.getGlobalMessages()) {
				sb.append(renderer.renderMarkupMessage(msg));
			}
			sb.append("</div>").append(renderer.newLine());
		}
		return sb.toString();
	}
	
	protected <T> String renderMessageList(FormElement<T> element) {
		StringBuilder sb = new StringBuilder();
		List<ConstraintViolationMessage> messages = element.getValidationMessages();
		if (messages != null && !messages.isEmpty()) {
			for (ConstraintViolationMessage msg : messages) {
				sb.append(renderer.renderMarkupMessage(msg));
			}
		}
		return sb.toString();
	}

	protected String renderMessage(ConstraintViolationMessage msg) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"").append(msg.getSeverity().getStyleClass()).append("\">").append(renderer.escapeHtml(msg.getText())).append("</div>").append(renderer.newLine());
		return sb.toString();
	}
}
