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
package net.formio.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import net.formio.ContentTypes;
import net.formio.FormElement;
import net.formio.FormMapping;
import net.formio.ajax.AjaxParams;
import net.formio.ajax.AjaxResponse;
import net.formio.ajax.JsEvent;
import net.formio.ajax.TdiAjaxRequestParams;
import net.formio.ajax.action.AjaxAction;
import net.formio.ajax.action.HandledJsEvent;
import net.formio.ajax.action.JsEventToAction;
import net.formio.ajax.error.AjaxAlertErrorHandler;
import net.formio.ajax.error.AjaxErrorHandler;

/**
 * Convenience methods for handling (AJAX or non-AJAX) responses in servlet API.
 * @author Radek Beran
 */
public final class ServletResponses {
	
	/**
	 * Writes response to {@link HttpServletResponse}. Response is closed for further writing.
	 * @param response
	 * @param content
	 * @param contentType
	 */
	public static void write(final HttpServletResponse response, String content, String contentType) {
		response.setContentType(contentType);
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write(content);
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	/**
	 * Writes HTML response to {@link HttpServletResponse}. Response is closed for further writing.
	 * @param response
	 * @param content
	 */
	public static void writeHtml(final HttpServletResponse response, String content) {
		write(response, content, ContentTypes.HTML);
	}

	/**
	 * Writes AJAX response to {@link HttpServletResponse}. Response is closed, further 
	 * response writing is not possible.
	 * @param response
	 * @param content
	 */
	public static void ajaxResponse(final HttpServletResponse response, String content) {
		write(response, content, ContentTypes.XML);
	}

	/**
	 * Renders AJAX response by applying given action and rendering obtained AJAX response.
	 * If given action is {@code null}, HTTP 404 status is returned.
	 * @param requestParams
	 * @param res
	 * @param action
	 * @param errorHandler
	 */
	public static <T> void ajaxResponse(TdiAjaxRequestParams requestParams, HttpServletResponse res, AjaxAction<T> action, AjaxErrorHandler<T> errorHandler) {
		if (requestParams == null) {
			throw new IllegalArgumentException("request params must be specified");
		}
		if (res == null) {
			throw new IllegalArgumentException("response must be specified");
		}
		if (errorHandler == null) {
			throw new IllegalArgumentException("error handler must be specified");
		}
		try {
			if (action == null) {
				notFound(res, "Action handling AJAX request was not specified");
			} else {
				AjaxResponse<T> ajRes = action.apply(requestParams);
				ajaxResponse(res, ajRes.getResponse());
			}
		} catch (Exception ex) {
			AjaxResponse<T> ajRes = errorHandler.errorResponse(requestParams, ex);
			ajaxResponse(res, ajRes.getResponse());
		}
	}
	
	/**
	 * Renders AJAX response by applying given action and rendering obtained AJAX response.
	 * If given action is {@code null}, HTTP 404 status is returned.
	 * @param requestParams
	 * @param res
	 * @param action
	 */
	public static <T> void ajaxResponse(TdiAjaxRequestParams requestParams, HttpServletResponse res, AjaxAction<T> action) {
		ajaxResponse(requestParams, res, action, new AjaxAlertErrorHandler<T>());
	}
	
	// TODO RBe: Move to formio package independent on servlet request
	/**
	 * Finds an action capable of handling given AJAX request that was initiated by some source form element.
	 * If no such source element with its handling action
	 * is found or no AJAX action matching the request parameters is registered for this element, 
	 * {@code null} is returned.
	 * @param requestParams
	 * @param formDefinition form definition for finding the form element that invoked the AJAX event
	 */
	public static <U, T> AjaxAction<T> findAjaxAction(TdiAjaxRequestParams requestParams, FormMapping<U> formDefinition) {
		// try to find action in given form mapping according to presence of request parameter
		AjaxAction<T> action = findAjaxActionByRequestParam(requestParams, formDefinition);
		if (action == null) {
			// find action according to name of source form element and name of JavaScript event
			String srcElement = requestParams.getParamValue(AjaxParams.SRC_ELEMENT_NAME);
			if (srcElement != null && !srcElement.isEmpty()) {
				FormElement<Object> el = formDefinition.findElement(srcElement);
				if (el != null) {
					String eventType = requestParams.getParamValue(AjaxParams.EVENT);
					for (HandledJsEvent ev : el.getProperties().getDataAjaxActions()) {
						if (ev instanceof JsEventToAction) {
							JsEventToAction<T> evToAction = (JsEventToAction<T>)ev;
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
	
	private static void notFound(HttpServletResponse res, String msg) throws IOException {
		res.sendError(HttpServletResponse.SC_NOT_FOUND, msg);
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
	
	private static <T, U> AjaxAction<T> findAjaxActionByRequestParam(TdiAjaxRequestParams requestParams, FormElement<U> element) {
		AjaxAction<T> action = null;
		for (HandledJsEvent ev : element.getProperties().getDataAjaxActions()) {
			if (ev instanceof JsEventToAction) {
				JsEventToAction<T> evToAction = (JsEventToAction<T>)ev;
				if (evToAction.getRequestParam() != null && 
					!evToAction.getRequestParam().isEmpty() && 
					containsParam(requestParams.getParamNames(), evToAction.getRequestParam())) {
					action = evToAction.getAction();
					break;
				}
			}
		}
		if (action == null) {
			if (element instanceof FormMapping<?>) {
				FormMapping<?> mapping = (FormMapping<?>)element;
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
	
	private ServletResponses() {
	}
}
