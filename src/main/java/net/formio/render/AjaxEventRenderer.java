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
import net.formio.FormField;
import net.formio.ajax.AjaxParams;
import net.formio.ajax.JsEvent;
import net.formio.ajax.action.AjaxHandler;
import net.formio.internal.FormUtils;

/**
 * Renders script for invoking AJAX events.
 * @author Radek Beran
 */
class AjaxEventRenderer {
	private final FormRenderer renderer;

	AjaxEventRenderer(FormRenderer renderer) {
		if (renderer == null) {
			throw new IllegalArgumentException("renderer cannot be null");
		}
		this.renderer = renderer;
	}
	
	/**
	 * Returns AJAX URL for given link field.
	 * @param field
	 * @return
	 */
	protected <T> String getActionLinkUrl(FormField<T> field) {
		String url = null;
		if (Field.LINK.getType().equals(field.getType())) {
			for (AjaxHandler<?> eventHandler : field.getProperties().getAjaxHandlers()) {
				url = getActionUrl(field, eventHandler);
				break;
			}
		}
		return url;
	}
	
	/**
	 * Renders script for handling form field.
	 * @param field
	 * @param multipleInputs
	 * @return
	 */
	protected <T> String renderFieldScript(FormField<T> field, InputMultiplicity inputMultiplicity) {
		StringBuilder sb = new StringBuilder();
		List<AjaxHandler<?>> urlEvents = Arrays.asList(field.getProperties().getAjaxHandlers());
		if (urlEvents.size() > 0) {
			StringBuilder tdiSend = new StringBuilder();
			if (inputMultiplicity == InputMultiplicity.MULTIPLE) {
				if (field.getChoices() != null && field.getChoiceRenderer() != null) {
					List<?> items = field.getChoices().getItems();
					if (items != null) {
						for (int i = 0; i < items.size(); i++) {
							String itemId = field.getElementIdWithIndex(i);
							tdiSend.append(renderTdiSend(field, itemId, urlEvents));
						}
					}
				}
			} else {
				tdiSend.append(renderTdiSend(field, field.getElementId(), urlEvents));
			}
			if (tdiSend.length() > 0) {
				sb.append("<script>").append(renderer.newLine()).append(tdiSend).append("</script>").append(renderer.newLine());
			}
		}
		return sb.toString();
	}
	
	/**
	 * Composes JavaScript for given form field that initiates TDI AJAX request when
	 * some given event occurs - different JavaScript events can have different URL addresses
	 * for handling the AJAX request. The value of form field is part of the AJAX request
	 * (if some value is filled).
	 * @param formField
	 * @param inputId
	 * @param eventHandlers
	 * @return
	 */
	private <T> String renderTdiSend(FormField<T> formField, String inputId, List<AjaxHandler<?>> eventHandlers) {
		StringBuilder sb = new StringBuilder("");
		if (eventHandlers != null && eventHandlers.size() > 0) {
			if (Field.LINK.getType().equals(formField.getType()) && (formField.getValue() == null || formField.getValue().isEmpty())) {
				// nothing, AJAX URL is rendered directly in href attribute  
			} else {
				boolean actionWithJsType = false;
				for (AjaxHandler<?> handler : eventHandlers) {
					if (handler.getEvent() != null) {
						actionWithJsType = true;
					}
				}
				if (actionWithJsType) {
					String elm = "$(\"#" + inputId + "\")";
					sb.append(elm).append(".on({").append(renderer.newLine());
					for (int i = 0; i < eventHandlers.size(); i++) {
						AjaxHandler<?> eventToUrl = eventHandlers.get(i);
						JsEvent eventType = eventToUrl.getEvent();
						if (eventType != null) {
							String url = getActionUrl(formField, eventToUrl);
							sb.append(eventType.getEventName()).append(": function(evt) {").append(renderer.newLine());
							// Remember previous data-ajax-url (to revert it back) and set it temporarily to custom URL
							sb.append("var prevUrl = ").append(elm).append(".attr(\"data-ajax-url\");").append(renderer.newLine());
							sb.append(elm).append(".attr(\"data-ajax-url\", \"").append(url).append("\");").append(renderer.newLine());
							sb.append("TDI.Ajax.send(").append(elm).append(");").append(renderer.newLine());
							sb.append(elm).append(".attr(\"data-ajax-url\", prevUrl);").append(renderer.newLine());
							sb.append("var prevUrl = null;").append(renderer.newLine());
							sb.append("}");
							if (i < eventHandlers.size() - 1) {
								// not the last event handler
								sb.append(",");
							}
							sb.append(renderer.newLine());
						}
					}
					sb.append("});").append(renderer.newLine());
				}
			}
		}
		return sb.toString();
	}

	private <T> String getActionUrl(FormField<T> formField, AjaxHandler<?> eventHandler) {
		String url = eventHandler.getHandlerUrl(formField.getParent().getConfig().getUrlBase(), formField);
		if (url == null || url.isEmpty()) {
			throw new IllegalArgumentException("No URL for AJAX request is specified");
		}
		url = FormUtils.urlWithAppendedParameter(url, AjaxParams.SRC_ELEMENT_NAME, formField.getName());
		return url;
	}
}
