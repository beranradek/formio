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
package net.formio;

/**
 * Utility methods for manipulating with forms or customization of forms.
 * @author Radek Beran
 */
public final class FormUtils {

	/**
	 * Extracts property name located at the end of full field name (field
	 * name is the whole path, with possible terminating brackets).
	 * @param fieldName
	 * @return
	 */
	static String fieldNameToPropertyName(String fieldName) {
		if (fieldName == null) return null;
		int lastDot = fieldName.lastIndexOf(Forms.PATH_SEP);
		String propName = fieldName;
		if (lastDot >= 0) {
			propName = fieldName.substring(lastDot + 1);
		}
		return removeTrailingBrackets(propName);
	}
	
	/**
	 * Constucts label key for given path.
	 * @param path
	 * @return
	 */
	static String labelKeyForName(String path) {
		if (path == null) return null;
		return path.replaceAll("\\[[0-9]*\\]", "");
	}
	
	/**
	 * Removes possible brackets at the end of given string that
	 * do not contain any index (for e.g. name[] will be transformed to name).
	 * @param str
	 * @return
	 */
	public static String removeTrailingBrackets(String str) {
		if (str == null) return null;
		String res = str;
		final String bracketsStr = "[]";
		if (res.endsWith(bracketsStr)) {
			res = res.substring(0, res.length() - bracketsStr.length());
		}
		return res;
	}
	
	private FormUtils() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
