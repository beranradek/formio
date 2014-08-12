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
package net.formio.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.formio.FormField;
import net.formio.Forms;

/**
 * Utility methods for form processing.
 * @author Radek Beran
 */
public final class FormUtils {

	/**
	 * Trims all given values and returns new array with trimmed values.
	 * @param strValues
	 * @return
	 */
	public static String[] trimValues(String[] strValues) {
		if (strValues == null) return null;
		String[] trimmed = new String[strValues.length];
		for (int i = 0; i < strValues.length; i++) {
			trimmed[i] = strValues[i] != null ? strValues[i].trim() : null;
		}
		return trimmed;
	}
	
	/**
	 * Extracts property name located at the end of full field name (field
	 * name is the whole path, with possible terminating brackets).
	 * @param fieldName
	 * @return
	 */
	public static String fieldNameToLastPropertyName(String fieldName) {
		if (fieldName == null) return null;
		int lastDot = fieldName.lastIndexOf(Forms.PATH_SEP);
		String propName = fieldName;
		if (lastDot >= 0) {
			propName = fieldName.substring(lastDot + 1);
		}
		return removeTrailingBrackets(propName);
	}
	
	/**
	 * Returns simple names of properties that are represented with given fields.
	 * @param fields
	 * @return
	 */
	public static Set<String> getPropertiesFromFields(Map<String, FormField> fields) {
		final Set<String> props = new LinkedHashSet<String>();
		for (FormField field : fields.values()) {
			// name of field is a full path, already prefixed with form name
			String propName = fieldNameToLastPropertyName(field.getName());
			props.add(propName);
		}
		return props;
	}
	
	/**
	 * Constucts label key for given path.
	 * @param path
	 * @return
	 */
	public static String labelKeyForName(String path) {
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
	
	/**
	 * Decomposes object to list of objects.
	 * @param value input object - can be iterable, array or single value
	 * @return
	 */
	public static List<Object> convertObjectToList(Object value) {
		List<Object> values = new ArrayList<Object>();
		if (value instanceof Iterable) {
			for (Object v : ((Iterable<?>)value)) {
				values.add(v);
			}
		} else if (value != null && value.getClass().isArray()) {
			values.addAll(ArrayUtils.convertPrimitiveArrayToList(value));
	    } else {
			values.add(value);
		}
		return values;
	}
	
	/**
	 * Flattens collection of lists of elements to one list of elements.
	 * @param collOfLists
	 * @return
	 */
	public static <U> List<U> flatten(Collection<List<U>> collOfLists) {
		List<U> res = new ArrayList<U>();
		for (List<U> l : collOfLists) {
			res.addAll(l);
		}
		return res;
	}
	
	/**
	 * Returns path with index inserted before the last property in the path, or
	 * unchanged path if there is only single property name in the path.
	 * @param path
	 * @param indexInList
	 * @return
	 */
	public static String pathWithIndexBeforeLastProperty(String path, int indexInList) {
		if (path == null) return null;
		String retPath = path;
		int li = retPath.lastIndexOf(Forms.PATH_SEP);
		if (li >= 0) {
			retPath = retPath.substring(0, li) + "[" + indexInList + "]" + retPath.substring(li);
		}
		return retPath;
	}
	
	/**
	 * Finds maximum index for indexed path of mapping (with given path) that occurs in request parameters.
	 * @param params
	 * @return
	 */
	public static int findMaxIndex(Iterable<String> params, String path) {
		Pattern indexedPathPattern = Pattern.compile(path + "\\[([0-9]+)\\].*");
		List<Integer> indexes = new ArrayList<Integer>();
		for (String param : params) {
			Matcher m = indexedPathPattern.matcher(param);
			if (m.matches()) {
				Integer index = Integer.valueOf(m.group(1));
				indexes.add(index);
			}
		}
		if (indexes.isEmpty()) {
			return -1;
		}
		Collections.sort(indexes);
		return indexes.get(indexes.size() - 1).intValue();
	}
	
	private FormUtils() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
