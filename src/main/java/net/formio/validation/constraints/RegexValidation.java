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

import jakarta.validation.constraints.Pattern;

/** 
 * Validates if given value matches the regular expresssion pattern.
 * @author Radek Beran
 */
public class RegexValidation {

	public static boolean isValid(CharSequence value, String regexp, Pattern.Flag ... patternFlags) {
		if (value == null) {
			return false;
		}
		int intFlag = 0;
		for (Pattern.Flag flag : patternFlags) {
			intFlag = intFlag | flag.getValue();
		}
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regexp, intFlag);
		Matcher m = pattern.matcher(value);
		return m.matches();
	}
	
	private RegexValidation() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
