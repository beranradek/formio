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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.formio.ContentTypes;
import net.formio.FormElement;
import net.formio.FormMapping;
import net.formio.ajax.AjaxAction;
import net.formio.ajax.AjaxParams;
import net.formio.ajax.AjaxResponse;
import net.formio.ajax.AjaxResponseBuilder;
import net.formio.ajax.JsEvent;
import net.formio.props.JsEventToAction;
import net.formio.props.JsEventUrlResolvable;
import net.formio.render.TdiResponseBuilder;
import net.formio.servlet.ajax.FormStateHandler;
import net.formio.servlet.ajax.action.DefaultAjaxAction;
import net.formio.servlet.ajax.action.FormStateAjaxAction;

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
	 * Writes AJAX response to {@link HttpServletResponse}. Response is closed for further writing.
	 * @param response
	 * @param content
	 */
	public static void ajaxResponse(final HttpServletResponse response, String content) {
		write(response, content, ContentTypes.XML);
	}
	
	/** 
	 * Updates data representing the state of form on the server
	 * and renders AJAX response constructed using given response builder.
	 * @param request
	 * @param response
	 * @param formStateHandler manipulates form state and handles error that occured when processing an AJAX request
	 * @param respBuilder processes the AJAX request using given form state and builds the AJAX response
	 */
	public static <T> void ajaxResponse(HttpServletRequest request, HttpServletResponse response, 
		final FormStateHandler<T> formStateHandler,
		final AjaxResponseBuilder<T> respBuilder) {
		try {
			AjaxResponse<T> ajResp = ajaxResponseBuilder(request, response, formStateHandler, respBuilder);
			formStateHandler.saveFormState(request, ajResp.getUpdatedFormState());
			ServletResponses.ajaxResponse(response, ajResp.getResponseBuilder().asString());
		} catch (Exception ex) {
			formStateHandler.handleError(request, response, ex);
		}
	}
	
	/**
	 * Finds an action capable of handling given AJAX request that was initiated by some source form element.
	 * If no such source element with its handling action
	 * is found or no AJAX action matching the request parameters is registered for this element, 
	 * {@code null} is returned.
	 * @param req
	 * @param formDefinition form definition for finding the form element that invoked the AJAX event
	 */
	public static <U> FormStateAjaxAction<U> findFormStateAjaxAction(HttpServletRequest req, FormMapping<U> formDefinition) {
		return (FormStateAjaxAction<U>)findAjaxAction(FormStateAjaxAction.class, req, formDefinition);
	}
	
	/**
	 * Finds an action capable of handling given AJAX request that was initiated by some source form element.
	 * If no such source element with its handling action
	 * is found or no AJAX action matching the request parameters is registered for this element, 
	 * {@code null} is returned.
	 * @param req
	 * @param formDefinition form definition for finding the form element that invoked the AJAX event
	 */
	public static <U> DefaultAjaxAction findDefaultAjaxAction(HttpServletRequest req, FormMapping<U> formDefinition) {
		return findAjaxAction(DefaultAjaxAction.class, req, formDefinition);
	}
	
	/**
	 * Finds an action capable of handling given AJAX request that was initiated by some source form element.
	 * If no such source element with its handling action
	 * is found or no AJAX action matching the request parameters is registered for this element, 
	 * {@code null} is returned.
	 * @param actionClass
	 * @param req
	 * @param formDefinition form definition for finding the form element that invoked the AJAX event
	 */
	private static <U, T extends AjaxAction> T findAjaxAction(Class<T> actionClass, HttpServletRequest req, FormMapping<U> formDefinition) {
		// try to find action in given form mapping according to presence of request parameter
		T action = findAjaxActionByRequestParam(actionClass, req, formDefinition);
		if (action == null) {
			// find action according to name of source form element and name of JavaScript event
			String srcElement = req.getParameter(AjaxParams.SRC_ELEMENT_NAME);
			if (srcElement != null && !srcElement.isEmpty()) {
				FormElement<Object> el = formDefinition.findElement(srcElement);
				if (el != null) {
					String eventType = req.getParameter(AjaxParams.EVENT);
					for (JsEventUrlResolvable ev : el.getProperties().getDataAjaxActions()) {
						if (ev instanceof JsEventToAction) {
							JsEventToAction evToAction = (JsEventToAction)ev;
							if (eventMatches(eventType, evToAction.getEvent())) {
								if (actionClass.isAssignableFrom(evToAction.getAction().getClass())) {
									action = actionClass.cast(evToAction.getAction());
									break;
								}
							}
						}
					}
				}
			}
		}
		return action;
	}

	private static <T extends AjaxAction, U> T findAjaxActionByRequestParam(Class<T> actionClass, HttpServletRequest req, FormElement<U> element) {
		T action = null;
		for (JsEventUrlResolvable ev : element.getProperties().getDataAjaxActions()) {
			if (ev instanceof JsEventToAction) {
				JsEventToAction evToAction = (JsEventToAction)ev;
				if (evToAction.getRequestParam() != null && 
					!evToAction.getRequestParam().isEmpty() && 
					req.getParameter(evToAction.getRequestParam()) != null) {
					if (actionClass.isAssignableFrom(evToAction.getAction().getClass())) {
						action = actionClass.cast(evToAction.getAction());
						break;
					}
				}
			}
		}
		if (action == null) {
			if (element instanceof FormMapping<?>) {
				FormMapping<?> mapping = (FormMapping<?>)element;
				for (FormElement<?> el : mapping.getElements()) {
					action = findAjaxActionByRequestParam(actionClass, req, el);
					if (action != null) {
						break;
					}
				}
			}
		}
		return action;
	}
	
	/**
	 * Renders AJAX response by applying given action and rendering obtained AJAX response.
	 * If given action is {@code null}, HTTP 404 status is returned.
	 * @param req
	 * @param res
	 * @param action
	 * @param errorHandler
	 */
	public static void ajaxResponse(HttpServletRequest req, HttpServletResponse res, 
		DefaultAjaxAction action, ErrorHandler errorHandler) {
		try {
			if (action == null) {
				notFound(res, "Action handling AJAX request was not found");
			} else {
				TdiResponseBuilder ajResp = action.apply(req);
				ajaxResponse(res, ajResp.asString());
			}
		} catch (Exception ex) {
			errorHandler.handleError(req, res, ex);
		}
	}
	
	/**
	 * Renders AJAX response by applying given action and rendering obtained AJAX response.
	 * If given action is {@code null}, HTTP 404 status is returned.
	 * @param req
	 * @param res
	 * @param action
	 * @param formStateHandler
	 */
	public static <T> void ajaxResponse(final HttpServletRequest req, final HttpServletResponse res, 
		final FormStateAjaxAction<T> action, FormStateHandler<T> formStateHandler) {
		try {
			if (action == null) {
				notFound(res, "Action handling AJAX request was not found");
			} else {
				AjaxResponse<T> ajResp = ServletResponses.ajaxResponseBuilder(req, res, formStateHandler, new AjaxResponseBuilder<T>() {
					@Override
					public AjaxResponse<T> apply(T formState) {
						return action.apply(req, formState);
					}
				});
				formStateHandler.saveFormState(req, ajResp.getUpdatedFormState());
				ajaxResponse(res, ajResp.getResponseBuilder().asString());
			}
		} catch (Exception ex) {
			formStateHandler.handleError(req, res, ex);
		}
	}
	
	/** 
	 * Updates data representing the state of form on the server
	 * and returns AJAX response builder that contains instructions
	 * to generate AJAX response.
	 * @param request
	 * @param response
	 * @param formStateHandler manipulates form state and handles error that occured when processing an AJAX request
	 * @param respBuilder processes the AJAX request using given form state and builds the AJAX response
	 * @return AJAX response builder
	 */
	static <T> AjaxResponse<T> ajaxResponseBuilder(HttpServletRequest request, HttpServletResponse response, 
		final FormStateHandler<T> formStateHandler,
		final AjaxResponseBuilder<T> respBuilder) {
		T formState = formStateHandler.findFormState(request);
		return respBuilder.apply(formState);
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
	
	private ServletResponses() {
	}
}
