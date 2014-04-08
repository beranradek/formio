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
package net.formio.binding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regular expression specifying method name and group used for extraction of
 * property name.
 * 
 * @author Radek Beran
 */
public class PropertyMethodRegex {
	private final String regex;
	private final int propertyNameGroup;
	private final Pattern pattern;

	public PropertyMethodRegex(String regex, int propertyNameGroup) {
		if (regex == null) throw new IllegalArgumentException("regex cannot be null");
		if (propertyNameGroup < 1) throw new IllegalArgumentException("propertyNameGroup must be > 0");
		this.regex = regex;
		this.propertyNameGroup = propertyNameGroup;
		this.pattern = Pattern.compile(regex);
	}

	public String getRegex() {
		return regex;
	}

	public int getPropertyNameGroup() {
		return propertyNameGroup;
	}
	
	public boolean matchesPropertyMethod(String methodName, String propertyName) {
		boolean matches = false;
		String prop = getPropertyName(methodName);
		if (prop != null && prop.equalsIgnoreCase(propertyName)) {
			matches = true;
		}
		return matches;
	}
	
	public boolean matchesMethod(String methodName) {
		return pattern.matcher(methodName).matches();
	}
	
	public String getPropertyName(String methodName) {
		String prop = null;
		Matcher matcher = pattern.matcher(methodName);
		if (matcher.matches()) {
			prop = matcher.group(propertyNameGroup);
		}
		if (prop != null && !prop.isEmpty()) {
			prop = Character.toLowerCase(prop.charAt(0)) + prop.substring(1);
		}
		return prop;
	}

}
