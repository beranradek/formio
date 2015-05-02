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

import net.formio.Field;
import net.formio.FormElement;
import net.formio.FormField;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.common.MessageTranslator;

/**
 * <p>Context for which the form is rendered: Locale, form URL and method, message translator for elements.
 * Contains also utility methods for manipulation with form elements.
 * <p>Thread-safe: Immutable.
 * @author Radek Beran
 */
public class RenderContext {
	private final Locale locale;
	private final FormMethod method;
	private final String actionUrl;
	
	public RenderContext() {
		this(Locale.getDefault());
	}
	
	public RenderContext(Locale locale) {
		this(locale, FormMethod.POST, "#");
	}
	
	public RenderContext(Locale locale, FormMethod method, String actionUrl) {
		if (locale == null) {
			throw new IllegalArgumentException("locale cannot be null");
		}
		this.locale = locale;
		this.method = method;
		this.actionUrl = actionUrl;
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
	
	/**
	 * Escapes HTML (converts HTML text to XML entities).
	 * @param s
	 * @return
	 */
	public String escapeHtml(String s) {
		if (s == null || s.isEmpty()) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}
	
	public <T> MessageTranslator getMessageTranslator(FormElement<T> element) {
		FormMapping<?> rootMapping = element.getRoot();
		return new MessageTranslator(element.getParent().getDataClass(),
			getLocale(), rootMapping.getDataClass());
	}
	
	String getElementId(FormElement<?> element) {
		return getIdForName(element.getName());
	}
	
	String getIdForName(String name) {
		return "id" + Forms.PATH_SEP + name;
	}
	
	<T> String getFieldType(FormField<T> field) {
		String type = field.getType();
		if (type == null) {
			type = Field.TEXT.getType();
		}
		return type.toLowerCase();
	}
	
	<T> String getElementIdWithIndex(FormField<T> field, int itemIndex) {
		return getElementId(field) + Forms.PATH_SEP + itemIndex;
	}
	
	String newLine() {
		return System.getProperty("line.separator");
	}
}
