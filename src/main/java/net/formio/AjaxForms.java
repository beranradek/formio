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
package net.formio;

import java.util.Iterator;

import net.formio.ajax.AjaxParams;
import net.formio.ajax.JsEvent;
import net.formio.ajax.action.AjaxAction;
import net.formio.ajax.action.HandledJsEvent;
import net.formio.ajax.action.JsEventToAction;

/**
 * Methods for serving AJAX forms.
 * @author Radek Beran
 */
class AjaxForms {

	static <U, T> AjaxAction<T> findAjaxAction(AbstractRequestParams requestParams, FormMapping<U> mapping) {
		// try to find action in given form mapping according to presence of request parameter
		AjaxAction<T> action = findAjaxActionByRequestParam(requestParams, mapping);
		if (action == null) {
			// find action according to name of source form element and name of
			// JavaScript event
			String srcElement = requestParams.getParamValue(AjaxParams.SRC_ELEMENT_NAME);
			if (srcElement != null && !srcElement.isEmpty()) {
				FormElement<Object> el = mapping.findElement(srcElement);
				if (el != null) {
					String eventType = requestParams.getParamValue(AjaxParams.EVENT);
					for (HandledJsEvent ev : el.getProperties().getDataAjaxActions()) {
						if (ev instanceof JsEventToAction) {
							JsEventToAction<T> evToAction = (JsEventToAction<T>) ev;
							if (eventMatches(eventType, evToAction.getEvent())) {
								action = evToAction.getAction();
								break;
							}
						}
					}
				}
			}
		}
		return action;
	}

	private static boolean eventMatches(String event, JsEvent jsEvent) {
		if (event == null && jsEvent == null) {
			return true;
		}
		if (event == null || jsEvent == null) {
			return false;
		}
		return jsEvent.getEventName().equals(event);
	}

	private static <T, U> AjaxAction<T> findAjaxActionByRequestParam(AbstractRequestParams requestParams, FormElement<U> element) {
		AjaxAction<T> action = null;
		for (HandledJsEvent ev : element.getProperties().getDataAjaxActions()) {
			if (ev instanceof JsEventToAction) {
				JsEventToAction<T> evToAction = (JsEventToAction<T>) ev;
				if (evToAction.getRequestParam() != null
						&& !evToAction.getRequestParam().isEmpty()
						&& containsParam(requestParams.getParamNames(), evToAction.getRequestParam())) {
					action = evToAction.getAction();
					break;
				}
			}
		}
		if (action == null) {
			if (element instanceof FormMapping<?>) {
				FormMapping<?> mapping = (FormMapping<?>) element;
				for (FormElement<?> el : mapping.getElements()) {
					action = findAjaxActionByRequestParam(requestParams, el);
					if (action != null) {
						break;
					}
				}
			}
		}
		return action;
	}

	private static boolean containsParam(Iterable<String> paramNames, String requestParam) {
		boolean found = false;
		if (paramNames != null && requestParam != null) {
			for (Iterator<String> it = paramNames.iterator(); it.hasNext();) {
				String p = it.next();
				if (p != null && p.equals(requestParam)) {
					found = true;
					break;
				}
			}
		}
		return found;
	}

	private AjaxForms() {
		throw new AssertionError("Not instantiable, use static members.");
	}
}
