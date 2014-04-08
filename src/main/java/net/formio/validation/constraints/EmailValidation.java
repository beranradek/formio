/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
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
 * Validation of e-mail.
 * 
 * @author Radek Beran
 */
public final class EmailValidation {
	private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\\b");
	
	/**
	 * Returns true if given string is valid e-mail, 
	 * false if it is not or {@code null} is given. 
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.isEmpty()) return false;
		Matcher matcher = EMAIL_PATTERN.matcher(email);
		boolean valid = matcher.matches();
		return valid;
	}
	
	private EmailValidation() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
