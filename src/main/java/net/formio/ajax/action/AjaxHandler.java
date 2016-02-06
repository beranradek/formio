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
package net.formio.ajax.action;

import java.io.Serializable;

import net.formio.FormElement;
import net.formio.ajax.AjaxParams;
import net.formio.ajax.JsEvent;
import net.formio.internal.FormUtils;

/**
 * JavaScript event mapped to handling AJAX action.
 * @param <T> type of updated form state object
 * @author Radek Beran
 */
public class AjaxHandler<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final JsEvent event;
	private final AjaxAction<T> action;
	private final String requestParam;
	
	public AjaxHandler(AjaxAction<T> action, JsEvent event) {
		this(action, event, null);
	}
	
	public AjaxHandler(AjaxAction<T> action) {
		this(action, (JsEvent)null);
	}
	
	public AjaxHandler(AjaxAction<T> action, String requestParam) {
		this(action, null, requestParam);
	}
	
	private AjaxHandler(AjaxAction<T> action, JsEvent event, String requestParam) {
		if (action == null) {
			throw new IllegalArgumentException("action must be specified");
		}
		this.action = action;
		this.event = event;
		this.requestParam = requestParam;
	}

	public JsEvent getEvent() {
		return event;
	}

	public AjaxAction<T> getAction() {
		return action;
	}
	
	public String getRequestParam() {
		return requestParam;
	}

	/**
	 * URL handling AJAX event.
	 * @param urlBase base URL to which additional event parameters are appended
	 * @param element form element that is source of the event
	 * @return
	 */
	public String getHandlerUrl(String urlBase, FormElement<?> element) {
		String url = urlBase;
		if (event != null) {
			url = FormUtils.urlWithAppendedParameter(url, AjaxParams.EVENT, event.getEventName());
		}
		return url;
	}
}
