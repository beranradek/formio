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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Renderer of a template - can be used to render JSP template into a String.
 * @author Radek Beran
 */
public class TemplateRenderer {
	
	/**
	 * Renders template to a string.
	 * 
	 * @param request
	 * @param response
	 * @param tplPath
	 * @return rendered template
	 */
	public String renderTemplate(final HttpServletRequest request, final HttpServletResponse response, final String tplPath) {
		String str = null;
		final StringWriter sw = new StringWriter();
		try {
			// final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(
					response) {
				@Override
				public PrintWriter getWriter() throws IOException {
					return new PrintWriter(sw);
				}

				@Override
				public ServletOutputStream getOutputStream() throws IOException {
					return new ServletOutputStream() {

						@Override
						public void write(int b) throws IOException {
							// nothing
						}
					};
				}
			};
			try {
				request.getRequestDispatcher(tplPath).include(request, responseWrapper);
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage(), ex);
			}
			str = sw.toString();
		} finally {
			try {
				sw.close();
			} catch (IOException e) {
				// in-memory writer closing error ignored
			}
		}
		return str;
	}
}
