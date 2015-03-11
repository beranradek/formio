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
package net.formio.servlet.ajax;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.formio.render.BasicFormRenderer;
import net.formio.render.RenderContext;
import net.formio.servlet.ErrorHandler;
import net.formio.servlet.ServletResponses;

/**
 * {@link ErrorHandler} that shows an alert using AJAX response.
 * @author Radek Beran
 */
public abstract class AjaxAlertErrorHandler implements ErrorHandler {

	/**
	 * Handles error in processing an AJAX request by generating AJAX response that shows "AJAX error" alert.
	 * Subclasses should call super implementation in the end of overriden method. 
	 * @param request
	 * @param response
	 * @param t exception thrown during processing an AJAX request
	 */
	@Override
	public void handleError(HttpServletRequest request, HttpServletResponse response, Throwable t) {
		ServletResponses.write(response,
			new BasicFormRenderer(new RenderContext(Locale.ENGLISH)).ajaxResponse()
				.status("ERROR")
				.script("alert(\"AJAX Error\")")
				.asString());
	}

}
