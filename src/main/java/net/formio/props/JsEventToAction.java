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
package net.formio.props;

import java.io.Serializable;

import net.formio.FormElement;
import net.formio.ajax.AjaxAction;
import net.formio.ajax.AjaxParams;
import net.formio.ajax.JsEvent;
import net.formio.internal.FormUtils;

/**
 * JavaScript event mapped to handling AJAX action.
 * @author Radek Beran
 */
public class JsEventToAction implements JsEventUrlResolvable, Serializable {
	private static final long serialVersionUID = 2178054031308176325L;
	private final JsEvent event;
	private final AjaxAction action;
	private final String requestParam;
	
	public JsEventToAction(JsEvent event, AjaxAction action) {
		this(event, action, null);
	}
	
	public JsEventToAction(AjaxAction action) {
		this(null, action, null);
	}
	
	public JsEventToAction(String requestParam, AjaxAction action) {
		this(null, action, requestParam);
	}
	
	private JsEventToAction(JsEvent event, AjaxAction action, String requestParam) {
		if (action == null) {
			throw new IllegalArgumentException("action must be specified");
		}
		this.event = event;
		this.action = action;
		this.requestParam = requestParam;
	}

	@Override
	public JsEvent getEvent() {
		return event;
	}

	public AjaxAction getAction() {
		return action;
	}
	
	public String getRequestParam() {
		return requestParam;
	}

	@Override
	public String getUrl(String urlBase, FormElement<?> element) {
		String url = urlBase;
		if (event != null) {
			url = FormUtils.urlWithAppendedParameter(url, AjaxParams.EVENT, event.getEventName());
		}
		return url;
	}
}
