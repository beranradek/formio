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
package net.formio.format;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common cache for {@link DateFormat}ters, {@link NumberFormat}ters and 
 * {@link DecimalFormat}ters.
 * @author Radek Beran
 */
class FormattersCache {
	
	private static final Map<FormatterKey, DateFormat> DATE_FORMATTERS_CACHE = new ConcurrentHashMap<FormatterKey, DateFormat>();
	private static final Map<FormatterKey, NumberFormat> NUMBER_FORMATTERS_CACHE = new ConcurrentHashMap<FormatterKey, NumberFormat>();
	private static final Map<FormatterKey, DecimalFormat> DECIMAL_FORMATTERS_CACHE = new ConcurrentHashMap<FormatterKey, DecimalFormat>();
	static final String DEFAULT_DATE_FORMAT = "d.M.yyyy";
	
	static DateFormat getOrCreateDateFormatter(String pattern, Locale locale) {
		final FormatterKey formatterKey = FormatterKey.getInstance(pattern,
				locale);
		DateFormat format = DATE_FORMATTERS_CACHE.get(formatterKey);
		if (format == null) {
			if (pattern != null && !pattern.isEmpty()) {
				format = new SimpleDateFormat(pattern);
			} else {
				// Note: full precision could be expressed using pattern "yyyy-MM-dd'T'HH:mm:ss,S z"
				format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, locale);
			}
			format.setLenient(false); // without heuristics - allowing only strict pattern 
			DATE_FORMATTERS_CACHE.put(formatterKey, format);
		}
		return format;
	}

	static NumberFormat getOrCreateNumberFormatter(String pattern, Locale locale) {
		final FormatterKey formatterKey = FormatterKey.getInstance(pattern,
				locale);
		NumberFormat format = NUMBER_FORMATTERS_CACHE.get(formatterKey);
		if (format == null) {
			if (pattern != null && !pattern.isEmpty()) {
				format = new DecimalFormat(pattern);
			} else {
				format = NumberFormat.getInstance(locale);
				format.setMaximumIntegerDigits(Short.MAX_VALUE);
				format.setMaximumFractionDigits(Short.MAX_VALUE);
			}
			NUMBER_FORMATTERS_CACHE.put(formatterKey, format);
		}
		return format;
	}

	static DecimalFormat getOrCreateDecimalFormatter(String pattern, Locale locale) {
		final FormatterKey formatterKey = FormatterKey.getInstance(pattern, locale);
		DecimalFormat format = DECIMAL_FORMATTERS_CACHE.get(formatterKey);
		if (format == null) {
			if (pattern != null && !pattern.isEmpty()) {
				format = new DecimalFormat(pattern);
			} else { 
				format = (DecimalFormat) NumberFormat.getInstance(locale);
				format.setMaximumIntegerDigits(Short.MAX_VALUE);
				format.setMaximumFractionDigits(Short.MAX_VALUE);
			}
			format.setParseBigDecimal(true);
			DECIMAL_FORMATTERS_CACHE.put(formatterKey, format);
		}
		return format;
	}
	
	protected static final class FormatterKey {
		private final String pattern;
		private final Locale locale;

		protected static FormatterKey getInstance(String pattern, Locale locale) {
			// Caching of FormatterKey instances can be implemented here
			return new FormatterKey(pattern, locale);
		}

		private FormatterKey(String pattern, Locale locale) {
			this.pattern = pattern;
			this.locale = locale;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((locale == null) ? 0 : locale.hashCode());
			result = prime * result
					+ ((pattern == null) ? 0 : pattern.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof FormatterKey))
				return false;
			FormatterKey other = (FormatterKey) obj;
			if (locale == null) {
				if (other.locale != null)
					return false;
			} else if (!locale.equals(other.locale))
				return false;
			if (pattern == null) {
				if (other.pattern != null)
					return false;
			} else if (!pattern.equals(other.pattern))
				return false;
			return true;
		}

	}
}
