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

import net.formio.FormField;
import net.formio.ajax.AjaxParams;
import net.formio.ajax.JsEvent;
import net.formio.props.JsEventToUrl;

/**
 * Renders script for invoking AJAX events.
 * @author Radek Beran
 */
class AjaxEventRenderer {
	private final RenderContext ctx;

	AjaxEventRenderer(RenderContext ctx) {
		if (ctx == null) {
			throw new IllegalArgumentException("ctx cannot be null");
		}
		this.ctx = ctx;
	}
	
	/**
	 * Renders script for handling form field.
	 * @param field
	 * @param multipleInputs
	 * @return
	 */
	protected <T> String renderFieldScript(FormField<T> field, boolean multipleInputs) {
		StringBuilder sb = new StringBuilder();
		if (field.getProperties().getDataAjaxEvents() != null && field.getProperties().getDataAjaxEvents().length > 0) {
			sb.append("<script>" + newLine());
			if (multipleInputs) {
				if (field.getChoices() != null && field.getChoiceRenderer() != null) {
					List<?> items = field.getChoices().getItems();
					if (items != null) {
						for (int i = 0; i < items.size(); i++) {
							String itemId = getRenderContext().getElementIdWithIndex(field, i);
							sb.append(renderTdiSend(field, itemId, field.getProperties().getDataAjaxEvents()));
						}
					}
				}
			} else {
				sb.append(renderTdiSend(field, getRenderContext().getElementId(field), field.getProperties().getDataAjaxEvents()));
			}
			sb.append("</script>" + newLine());
		}
		return sb.toString();
	}
	
	protected RenderContext getRenderContext() {
		return ctx;
	}
	
	/**
	 * Composes JavaScript for given form field that initiates TDI AJAX request when
	 * some given event occurs - different JavaScript events can have different URL addresses
	 * for handling the AJAX request. The value of form field is part of the AJAX request
	 * (if some value is filled).
	 * @param formField
	 * @param inputId
	 * @param events
	 * @return
	 */
	private <T> String renderTdiSend(FormField<T> formField, String inputId, JsEventToUrl[] events) {
		StringBuilder sb = new StringBuilder();
		if (events != null && events.length > 0) {
			String elm = "$(\"#" + inputId + "\")";
			sb.append(elm + ".on({" + newLine());
			for (int i = 0; i < events.length; i++) {
				JsEventToUrl eventToUrl = events[i];
				JsEvent eventType = eventToUrl.getEvent();
				String url = eventToUrl.getUrl();
				if (url == null || url.isEmpty()) {
					url = formField.getProperties().getDataAjaxUrl();
				}
				if (url == null || url.isEmpty()) {
					throw new IllegalArgumentException("No URL for AJAX request is specified");
				}
				url = urlWithAppendedParameter(url, AjaxParams.SRC_ELEMENT_NAME, formField.getName());
				sb.append(eventType.getEventName() + ": function(evt) {"  + newLine());
				// Remember previous data-ajax-url (to revert it back) and set it temporarily to custom URL
				sb.append("var prevUrl = " + elm + ".attr(\"data-ajax-url\");" + newLine());
				sb.append(elm + ".attr(\"data-ajax-url\", \"" + url + "\");" + newLine());
				sb.append("TDI.Ajax.send(" + elm + ");" + newLine());
				sb.append(elm + ".attr(\"data-ajax-url\", prevUrl);" + newLine());
				sb.append("var prevUrl = null;" + newLine());
				sb.append("}");
				if (i < events.length - 1) {
					// not the last event handler
					sb.append(",");
				}
				sb.append(newLine());
			}
			sb.append("});" + newLine());
		}
		return sb.toString();
	}

	private String urlWithAppendedParameter(String url, String paramName, String paramValue) {
		if (url == null || url.isEmpty()) return null;
		if (url.contains("?") && !url.endsWith("?")) {
			if (!url.endsWith("&")) {
				url = url + "&";
			}
		} else if (!url.endsWith("?")) {
			url = url + "?"; 
		}
		url = url + paramName + "=" + paramValue;
		return url;
	}
	
	private String newLine() {
		return getRenderContext().newLine();
	}
}
