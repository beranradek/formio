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
import net.formio.ajax.AjaxResponseBuilder;
import net.formio.render.TdiResponseBuilder;
import net.formio.servlet.ajax.FormStateHandler;

/**
 * Convenience methods for handling (AJAX or non-AJAX) responses in servlet API.
 * @author Radek Beran
 */
public final class ServletResponses {

	/**
	 * Writes AJAX response to {@link HttpServletResponse}. Response is closed for further writing.
	 * @param response
	 * @param content
	 */
	public static void write(final HttpServletResponse response, String content) {
		response.setContentType(ContentTypes.XML);
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
	 * Renders AJAX response constructed using given response builder.
	 * @param request
	 * @param response
	 * @param formStateHandler manipulates form state and handles error that occured when processing an AJAX request
	 * @param respBuilder processes the AJAX request using given form state and builds the AJAX response
	 */
	public static <T> void ajaxResponse(HttpServletRequest request, HttpServletResponse response, 
		final FormStateHandler<T> formStateHandler,
		final AjaxResponseBuilder<T> respBuilder) {
		try {
			T formState = formStateHandler.findFormState(request);
			TdiResponseBuilder ajResp = respBuilder.apply(formState);
			formStateHandler.saveFormState(request, formState);
			// Update filled form elements using AJAX response
			ServletResponses.write(response, ajResp.asString());
		} catch (RuntimeException ex) {
			formStateHandler.handleError(request, response, ex);
		}
	}
	
	private ServletResponses() {
	}
}
