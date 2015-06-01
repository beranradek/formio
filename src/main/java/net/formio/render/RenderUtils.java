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

import net.formio.FormElement;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.common.BundleMessageTranslator;
import net.formio.common.IdentityMessageTranslator;
import net.formio.common.MessageTranslator;

/**
 * Utility methods for rendering the forms.
 * @author Radek Beran
 */
public class RenderUtils {
	
	/**
	 * Returns form element id for given form element name. 
	 * @param name
	 * @return
	 */
	public static String getElementIdForName(String name) {
		return "id" + Forms.PATH_SEP + name;
	}
	
	/**
	 * Escapes HTML (converts HTML text to XML entities).
	 * Escape using XSS recommendations from https://www.owasp.org/index.php/Cross_Site_Scripting
	 * @param s
	 * @return
	 */
	public static String escapeHtml(String s) {
		if (s == null || s.isEmpty()) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '&':
					sb.append("&amp;");
					break;
				case '"':
					sb.append("&#034;");
					break;
				case '\'':
					sb.append("&#039;");
					break;
				case '(':
					sb.append("&#040;");
					break;
				case ')':
					sb.append("&#041;");
					break;
				case '#':
					sb.append("&#035;");
					break;
				case '%':
					sb.append("&#037;");
					break;
				case ';':
					sb.append("&#059;");
					break;
				case '+':
					sb.append("&#043;");
					break;
				case '-':
					sb.append("&#045;");
					break;
				default:
					sb.append(c);
					break;
			}
		}
		return sb.toString();
	}
	
	/**
	 * Returns translator of message keys to localized messages for given form element.
	 * @param element
	 * @param locale
	 * @return
	 */
	public static <T> MessageTranslator getMessageTranslator(FormElement<T> element, Locale locale) {
		MessageTranslator tr = null;
		FormMapping<?> rootMapping = element.getRoot();
		if (rootMapping == null) {
			tr = new IdentityMessageTranslator();
		} else {
			tr = new BundleMessageTranslator(element.getParent().getDataClass(), locale, rootMapping.getDataClass());
		}
		return tr;
	}
	
	private RenderUtils() {
		throw new AssertionError("Not instantiable, use static members.");
	}
}
