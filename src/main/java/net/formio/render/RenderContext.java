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

import java.util.Locale;

import net.formio.FormMapping;

/**
 * Context with common data for rendering a form.
 * @author Radek Beran
 *
 * @param <T>
 */
public class RenderContext<T> {
	private FormMapping<T> filledForm;
	private FormMethod method;
	private String actionUrl;
	private Locale locale;

	public FormMapping<T> getFilledForm() {
		return filledForm;
	}

	public void setFilledForm(FormMapping<T> filledForm) {
		this.filledForm = filledForm;
	}

	public FormMethod getMethod() {
		return method;
	}

	public void setMethod(FormMethod method) {
		this.method = method;
	}

	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
