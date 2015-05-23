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

import net.formio.BasicListFormMapping;
import net.formio.FormElement;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.common.MessageTranslator;

/**
 * Renders labels of mappings or form fields.
 * @author Radek Beran
 */
class LabelRenderer {
	private final BasicFormRenderer renderer;
	private final StyleRenderer styleRenderer;

	LabelRenderer(BasicFormRenderer renderer, StyleRenderer styleRenderer) {
		if (renderer == null) {
			throw new IllegalArgumentException("renderer cannot be null");
		}
		if (styleRenderer == null) {
			throw new IllegalArgumentException("styleRenderer cannot be null");
		}
		this.renderer = renderer;
		this.styleRenderer = styleRenderer;
	}
	
	protected <T> String renderMappingLabel(FormMapping<T> mapping) {
		StringBuilder sb = new StringBuilder("");
		if (mapping.getProperties().isLabelVisible() && !mapping.isRootMapping()) {
			int colLabelWidth = mapping.getConfig().getColLabelWidth();
			int colFormWidth = mapping.getConfig().getColFormWidth();
			sb.append("<div class=\"row\">" + renderer.newLine());
			sb.append("<div class=\"" + styleRenderer.getFormGroupClasses() + " " + 
				styleRenderer.getColWidthClassPrefix() + colFormWidth +
				"\">" + renderer.newLine());
			sb.append("<div class=\"" + styleRenderer.getLabelClasses() + " " +
				styleRenderer.getColWidthClassPrefix() + colLabelWidth + " mapping-label\">" + renderer.newLine());
			sb.append("<label>" + renderer.getLabelText(mapping) + ":</label>");
			sb.append("</div>" + renderer.newLine());
			sb.append("</div>" + renderer.newLine());
			sb.append("</div>" + renderer.newLine());
		}
		return sb.toString();
	}

	protected <T> String renderFieldLabel(FormElement<T> element) {
		StringBuilder sb = new StringBuilder("");
		int colLabelWidth = element.getParent().getConfig().getColLabelWidth();
		if (element.getProperties().isLabelVisible()) {
			sb.append("<div class=\"" + styleRenderer.getColWidthClassPrefix() + colLabelWidth + " field-label\">" + renderer.newLine());
			sb.append("<label for=\"id-" + element.getName() + "\" class=\"" + styleRenderer.getLabelClasses() + "\">");
			sb.append(renderer.getLabelText(element));
			sb.append(":");
			sb.append("</label>" + renderer.newLine());
			sb.append("</div>" + renderer.newLine());
		}
		return sb.toString();
	}

	protected <T> String getLabelText(FormElement<T> formElement) {
		StringBuilder sb = new StringBuilder("");
		if (formElement.getProperties().isLabelVisible()) {
			MessageTranslator tr = renderer.getMessageTranslator(formElement);
			String msgKey = formElement.getLabelKey();
			if (formElement instanceof FormMapping) {
				FormMapping<?> m = (FormMapping<?>) formElement;
				if (m.getIndex() != null) {
					msgKey = msgKey + Forms.PATH_SEP + "single";
				}
			}
			sb.append(renderer.escapeHtml(tr.getMessage(msgKey, renderer.getRenderContext().getLocale())));
			if (formElement instanceof BasicListFormMapping) {
				FormMapping<?> listMapping = (FormMapping<?>) formElement;
				sb.append(" (" + listMapping.getList().size() + ")");
			}
			if (formElement.isRequired()) {
				sb.append(renderer.getRequiredMark(formElement));
			}
		}
		return sb.toString();
	}

	protected <T> String getRequiredMark(@SuppressWarnings("unused") FormElement<T> formElement) {
		return "&nbsp;*";
	}
}
