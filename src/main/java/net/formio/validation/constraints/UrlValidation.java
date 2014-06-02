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
package net.formio.validation.constraints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Validation of URL.
 * @author Radek Beran (original pattern from Play! framework)
 */
public final class UrlValidation {
	private static final Pattern PATTERN = Pattern.compile("^(http|https|ftp)\\://[a-zA-Z0-9\\-\\.]+\\.[a-z" +
		"A-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~\\!])*$");

	/**
	 * Returns true if given string is a valid URL.
	 * @param input
	 * @return result of validation, false if input is {@code null} or empty
	 */
	public static boolean isUrl(String input) {
		if (input == null || input.isEmpty()) return false;
		Matcher matcher = PATTERN.matcher(input);
		return matcher.matches();
	}
	
	private UrlValidation() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
