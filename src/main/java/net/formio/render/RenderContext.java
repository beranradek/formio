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
import java.util.TimeZone;

/**
 * <p>Context for which the form is rendered: Locale, form URL and method, message translator for elements.
 * Contains also utility methods for manipulation with form elements.
 * <p>Thread-safe: Immutable.
 * @author Radek Beran
 */
public class RenderContext {
	private final Locale locale;
	private final TimeZone timeZone;
	private final FormMethod method;
	private final String actionUrl;
	
	public RenderContext() {
		this(Locale.getDefault());
	}
	
	public RenderContext(Locale locale) {
		this(FormMethod.POST, "#", locale, TimeZone.getDefault());
	}
	
	public RenderContext(Locale locale, TimeZone timeZone) {
		this(FormMethod.POST, "#", locale, timeZone);
	}
	
	public RenderContext(FormMethod method, String actionUrl, Locale locale) {
		this(method, actionUrl, locale, TimeZone.getDefault());
	}
	
	public RenderContext(FormMethod method, String actionUrl, Locale locale, TimeZone timeZone) {
		if (locale == null) {
			throw new IllegalArgumentException("locale cannot be null");
		}
		if (timeZone == null) {
			throw new IllegalArgumentException("timeZone cannot be null");
		}
		this.method = method;
		this.actionUrl = actionUrl;
		this.locale = locale;
		this.timeZone = timeZone;
	}

	public FormMethod getMethod() {
		return method;
	}

	public String getActionUrl() {
		return actionUrl;
	}

	public Locale getLocale() {
		return locale;
	}
	
	public TimeZone getTimeZone() {
		return timeZone;
	}
}
