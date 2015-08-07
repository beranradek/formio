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
package net.formio.servlet.common;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import net.formio.ContentTypes;
import net.formio.RequestParams;
import net.formio.ajax.AjaxResponse;
import net.formio.ajax.action.AjaxAction;
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
	 * @param res response
	 * @param action
	 * @param errorHandler
	 */
	public static <T> void ajaxResponse(RequestParams requestParams, HttpServletResponse res, AjaxAction<T> action, AjaxErrorHandler<T> errorHandler) {
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
	public static <T> void ajaxResponse(RequestParams requestParams, HttpServletResponse res, AjaxAction<T> action) {
		ajaxResponse(requestParams, res, action, new AjaxAlertErrorHandler<T>());
	}
	
	public static void notFound(HttpServletResponse res, String msg) throws IOException {
		res.sendError(HttpServletResponse.SC_NOT_FOUND, msg);
	}
	
	public static void notFound(HttpServletResponse res) throws IOException {
		notFound(res, "Not Found");
	}
	
	private ServletResponses() {
	}
}
