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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>Retrieves translations from {@link ResourceBundle}s.</p>
 * <ul>
 * 	<li>Thread-safe: Immutable
 * </ul>
 * @author Radek Beran
 */
public class BundleMessageTranslator implements MessageTranslator {
	
	private final String bundleName;
	private final String fallbackBundleName;
	private final Locale locale;
	
	/**
	 * Creates new message translator.
	 * @param bundleName
	 * @param locale
	 * @param fallbackBundleName
	 */
	public BundleMessageTranslator(String bundleName, Locale locale, String fallbackBundleName) {
		if (bundleName == null) throw new IllegalArgumentException("bundleName cannot be null");
		if (locale == null) throw new IllegalArgumentException("locale cannot be null");
		this.locale = locale;
		this.bundleName = bundleName;
		this.fallbackBundleName = fallbackBundleName;
		try { 
			ResourceBundle.getBundle(bundleName, locale);
		} catch (MissingResourceException ex) {
			// ignored, so the user is not forced to create properties for all classes
		}
	}
	
	/**
	 * Creates new message translator.
	 * @param bundleName
	 * @param locale
	 */
	public BundleMessageTranslator(String bundleName, Locale locale) {
		this(bundleName, locale, (String)null);
	}
	
	/**
	 * Creates new message translator.
	 * @param bundleName
	 * @param fallbackBundleName
	 */
	public BundleMessageTranslator(String bundleName, String fallbackBundleName) {
		this(bundleName, Locale.getDefault(), fallbackBundleName);
	}
	
	/**
	 * Creates new message translator.
	 * @param bundleName
	 */
	public BundleMessageTranslator(String bundleName) {
		this(bundleName, (String)null);
	}
	
	/**
	 * Creates new message translator.
	 * @param cls class for which the resource bundle is searched
	 * @param locale
	 */
	public BundleMessageTranslator(Class<?> cls, Locale locale) {
		this(classToBundleName(cls), locale);
	}
	
	/**
	 * Creates new message translator.
	 * @param cls class for which the resource bundle is searched
	 * @param locale
	 * @param cls2 fallback class for which the resource bundle is searched if not found for first class
	 */
	public BundleMessageTranslator(Class<?> cls, Locale locale, Class<?> cls2) {
		this(classToBundleName(cls), locale, classToBundleName(cls2));
	}
	
	/**
	 * Creates new message translator.
	 * @param cls class for which the resource bundle is searched
	 */
	public BundleMessageTranslator(Class<?> cls) {
		this(classToBundleName(cls));
	}
	
	/**
	 * Returns translation of the message for given message key, locale and arguments.
	 * @param msgKey
	 * @param locale
	 * @param args
	 * @return
	 */
	@Override
	public String getMessage(String msgKey, Locale locale, Object ... args) {
		if (msgKey == null) throw new IllegalArgumentException("msgKey cannot be null");
		String text = null;
		try {
			try {
				text = getStrFromBundle(this.bundleName, msgKey, locale);
			} catch (MissingResourceException ex) {
				// message was not found in resource bundle, ignored
				text = null;
			}
			if ((text == null || text.equals(createMissingMessage(msgKey))) && this.fallbackBundleName != null) {
				text = getStrFromBundle(this.fallbackBundleName, msgKey, locale);
			}
		} catch (MissingResourceException ex) {
			// message was not found in resource bundle, ignored
			text = null;
		}
		if (text == null) {
			text = createMissingMessage(msgKey);
		} else {
			if (args != null && args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					Object arg = args[i];
					text = text.replaceAll("\\{" + i + "\\}", arg != null ? arg.toString() : "{" + i + "}");
				}
			}
		}
		return text;
	}
	
	/**
	 * Returns translation of the message for given message key and arguments.
	 * @param msgKey
	 * @param args
	 * @return
	 */
	@Override
	public String getMessage(String msgKey, Object ... args) {
		return getMessage(msgKey, this.locale, args);
	}
	
	public static String classToBundleName(Class<?> cls) {
		if (cls == null) return null;
		return cls.getName();
	}
	
	private String createMissingMessage(String msgKey) {
		return "???" + msgKey + "???";
	}
	
	private String getStrFromBundle(String baseName, String msgKey, Locale locale) {
		// getBundle caches resulting bundles
		ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
		return bundle.getString(msgKey);
	}
}