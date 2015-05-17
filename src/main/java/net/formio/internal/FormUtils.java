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
package net.formio.internal;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.formio.FormElement;
import net.formio.FormField;
import net.formio.FormMapping;
import net.formio.Forms;

/**
 * Utility methods for form processing.
 * This class is NOT intended as a part of public API
 * and should not be used outside the library.
 * @author Radek Beran
 */
public class FormUtils {
	
	/**
	 * This method is NOT intended as a part of public API and should not be used outside the library!
	 * Truncate String to maximum length
	 * @param str input string
	 * @param maxLength maximum allowed length in characters
	 * @return truncated string
	 */
	public static String truncate(String str, int maxLength) {
		if (str==null || str.length()<=maxLength) return str;
		return str.substring(0, maxLength);
	}

	/**
	 * This method is NOT intended as a part of public API and should not be used outside the library!
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
	 * This method is NOT intended as a part of public API and should not be used outside the library!
	 * Returns simple names of formProperties that are represented with given fields.
	 * @param fields
	 * @return
	 */
	public static <T> Set<String> getPropertiesFromFields(Map<String, FormField<?>> fields) {
		final Set<String> props = new LinkedHashSet<String>();
		for (FormField<?> field : fields.values()) {
			props.add(field.getPropertyName());
		}
		return props;
	}
	
	/**
	 * Returns last property name from given path.
	 * @param path
	 * @return
	 */
	public static String propertyName(String path) {
		if (path == null) return null;
		int lastSeparator = path.lastIndexOf(Forms.PATH_SEP);
		String propName = path;
		if (lastSeparator >= 0) {
			propName = path.substring(lastSeparator + 1);
		}
		return removeTrailingBrackets(propName);
	}
	
	/**
	 * This method is NOT intended as a part of public API and should not be used outside the library!
	 * Constucts label key for given path.
	 * @param path
	 * @return
	 */
	public static String labelKeyForName(String path) {
		if (path == null) return null;
		return path.replaceAll("\\[[0-9]*\\]", "");
	}
	
	/**
	 * This method is NOT intended as a part of public API and should not be used outside the library!
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
	 * This method is NOT intended as a part of public API and should not be used outside the library!
	 * Removes possible brackets from given string.
	 * @param str
	 * @return
	 */
	public static String removeBrackets(String str) {
		if (str == null) return null;
		return str.replaceAll("\\[[^\\]]*\\]", "");
	}
	
	/**
	 * This method is NOT intended as a part of public API and should not be used outside the library!
	 * Decomposes object to list of objects.
	 * @param value input object - can be iterable, array or single value
	 * @return
	 */
	public static <T> List<T> convertObjectToList(T value) {
		List<T> values = new ArrayList<T>();
		if (value instanceof Iterable) {
			for (T v : ((Iterable<T>)value)) {
				values.add(v);
			}
		} else if (value != null && value.getClass().isArray()) {
			values.addAll(ArrayUtils.<T>convertPrimitiveArrayToList(value));
	    } else {
			values.add(value);
		}
		return values;
	}
	
	/**
	 * This method is NOT intended as a part of public API and should not be used outside the library!
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
	 * This method is NOT intended as a part of public API and should not be used outside the library!
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
	
	/**
	 * This method is NOT intended as a part of public API and should not be used outside the library!
	 * @param cls
	 * @param name
	 * @return
	 */
	public static <U> FormElement<U> findElementRecursive(Class<U> cls, String name, FormElement<?> startElem) {
		FormElement<U> foundEl = null;
		if (startElem.getName().equals(name)) {
			foundEl = (FormElement<U>)startElem;
		} else if (startElem instanceof FormMapping<?>) {
			FormMapping<?> fm = (FormMapping<?>)startElem;
			if (fm.getList() != null && !fm.getList().isEmpty()) {
				for (FormElement<?> element : fm.getList()) {
					foundEl = findElementRecursive(cls, name, element);
					if (foundEl != null) {
						break;
					}
				}
			} else {
				for (FormElement<?> element : fm.getElements()) {
					foundEl = findElementRecursive(cls, name, element);
					if (foundEl != null) {
						break;
					}
				}
			}
		}
		return foundEl;
	}
	
	/**
	 * This method is NOT intended as a part of public API and should not be used outside the library!
	 * Opens given URL in default browser of operating system.
	 * @param uri
	 */
	public static void openUrlInBrowser(String uri) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(new URI(uri));
	            } catch (Exception ex) {
	            	ex.printStackTrace();
	            }
			}
		}
	}
	
	/**
	 * This method is NOT intended as a part of public API and should not be used outside the library!
	 * Opens given HTML in default browser of operating system.
	 * @param html
	 */
	public static void openHtmlInBrowser(String html) {
		File f = null;
		try {
			f = File.createTempFile("html_preview", ".html", FormUtils.getTempDir());
			saveTextInFile(f, html, "UTF-8");
			openUrlInBrowser("file:///" + f.getAbsolutePath().replace("\\", "/"));
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	/**
	 * This method is NOT intended as a part of public API and should not be used outside the library!
	 * Returns directory with temporary files.
	 * @return
	 */
	public static File getTempDir() {
		return new File(System.getProperty("java.io.tmpdir"));
	}
	
	/**
	 * Saves given text content to given file.
	 * @param file
	 * @param content
	 * @param encoding
	 * @throws IOException
	 */
	public static void saveTextInFile(File file, String content, String encoding) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
			bw.write(content);
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (bw != null) {
				try {
					bw.flush();
					bw.close();
				} catch (IOException ignored) {
					// ignored
				}
			}
		}
	}
	
	public static String urlWithAppendedParameter(String url, String paramName, String paramValue) {
		if (url == null || url.isEmpty()) return null;
		if (url.contains("?") && !url.endsWith("?")) {
			if (!url.endsWith("&")) {
				url = url + "&";
			}
		} else if (!url.endsWith("?")) {
			url = url + "?"; 
		}
		url = url + paramName + "=" + paramValue;
		return url;
	}
	
	private FormUtils() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
