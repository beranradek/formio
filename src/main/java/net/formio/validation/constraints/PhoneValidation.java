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
 * Validation of phone number.
 * 
 * @author Radek Beran (original pattern from Play! framework)
 */
public final class PhoneValidation {
	/**
	 * <p>Format: +CCC (SSSSSS)9999999999xEEEE</p>
	 * <ul>
	 * <li>+CCC optional country code, up to 3 digits, it must be followed by a delimiter</li>
	 * <li>(SSSSSS) optional subzone, up to 6 digits</li>
	 * <li>9999999999 mandatory number, up to 20 digits (which should cover all know cases current and future)</li>
	 * <li>x optional extension, can also be spelled "ext" or "extension"</li>
	 * <li>EEEE optional extension number, up to 4 digits</li>
	 * </ul>
	 * <p>Delimiters can be either a space, '-', '.' or '/' and can be used anywhere in the number.</p>
	 */
	private static final Pattern PATTERN = Pattern.compile("^([\\+][0-9]{1,3}([ \\.\\-]))?([\\(]{1}[0-9]{2,6}[\\)])?([0-9 \\.\\-/]{3,20})((x|ext|extension)[ ]?[0-9]{1,4})?$");

	/**
	 * Returns true if given string is a valid phone number.
	 * The validation enforces a basic phone pattern. Implement your own more specific validator
	 * for desired country.
	 * @param input
	 * @return result of validation, false if input is {@code null} or empty
	 */
	public static boolean isPhone(String input) {
		if (input == null || input.isEmpty()) return false;
		Matcher matcher = PATTERN.matcher(input);
		return matcher.matches();
	}
	
	private PhoneValidation() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
