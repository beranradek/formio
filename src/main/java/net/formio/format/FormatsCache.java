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
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common cache for {@link DateFormat}s, {@link NumberFormat}s and {@link DecimalFormat}s.
 * @author Radek Beran
 */
class FormatsCache {
	
	private static final Map<FormatKey, DateFormat> DATE_FORMATS_CACHE = new ConcurrentHashMap<FormatKey, DateFormat>();
	private static final Map<FormatKey, NumberFormat> NUMBER_FORMATS_CACHE = new ConcurrentHashMap<FormatKey, NumberFormat>();
	private static final Map<FormatKey, DecimalFormat> DECIMAL_FORMATS_CACHE = new ConcurrentHashMap<FormatKey, DecimalFormat>();
	static final String DEFAULT_DATE_FORMAT = "d.M.yyyy";
	
	static DateFormat getOrCreateDateFormat(String pattern, Locale locale) {
		final FormatKey formatterKey = FormatKey.getInstance(pattern,
				locale);
		DateFormat format = DATE_FORMATS_CACHE.get(formatterKey);
		if (format == null) {
			if (pattern != null && !pattern.isEmpty()) {
				format = new SimpleDateFormat(pattern);
			} else {
				// Note: full precision could be expressed using pattern "yyyy-MM-dd'T'HH:mm:ss,S z"
				format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, locale);
			}
			format.setLenient(false); // without heuristics - allowing only strict pattern 
			DATE_FORMATS_CACHE.put(formatterKey, format);
		}
		return format;
	}

	static NumberFormat getOrCreateNumberFormat(String pattern, Locale locale) {
		final FormatKey formatterKey = FormatKey.getInstance(pattern,
				locale);
		NumberFormat format = NUMBER_FORMATS_CACHE.get(formatterKey);
		if (format == null) {
			if (pattern != null && !pattern.isEmpty()) {
				DecimalFormatSymbols symbols = new DecimalFormatSymbols();
				symbols.setGroupingSeparator(',');
				symbols.setDecimalSeparator('.');
				format = new DecimalFormat(pattern, symbols);
				format.setMaximumIntegerDigits(Short.MAX_VALUE);
				format.setMaximumFractionDigits(Short.MAX_VALUE);
			} else {
				format = NumberFormat.getInstance(locale);
				DecimalFormat decFormat = (DecimalFormat)format;
				DecimalFormatSymbols symbols = new DecimalFormatSymbols();
				symbols.setGroupingSeparator(',');
				symbols.setDecimalSeparator('.');
				decFormat.setDecimalFormatSymbols(symbols);
				format.setMaximumIntegerDigits(Short.MAX_VALUE);
				format.setMaximumFractionDigits(Short.MAX_VALUE);
			}
			NUMBER_FORMATS_CACHE.put(formatterKey, format);
		}
		return format;
	}

	static DecimalFormat getOrCreateDecimalFormat(String pattern, Locale locale) {
		final FormatKey formatterKey = FormatKey.getInstance(pattern, locale);
		DecimalFormat format = DECIMAL_FORMATS_CACHE.get(formatterKey);
		if (format == null) {
			if (pattern != null && !pattern.isEmpty()) {
				DecimalFormatSymbols symbols = new DecimalFormatSymbols();
				symbols.setGroupingSeparator(',');
				symbols.setDecimalSeparator('.');
				format = new DecimalFormat(pattern, symbols);
				format.setMaximumIntegerDigits(Short.MAX_VALUE);
				format.setMaximumFractionDigits(Short.MAX_VALUE);
			} else { 
				format = (DecimalFormat) NumberFormat.getInstance(locale);
				DecimalFormatSymbols symbols = new DecimalFormatSymbols();
				symbols.setGroupingSeparator(',');
				symbols.setDecimalSeparator('.');
				format.setDecimalFormatSymbols(symbols);
				format.setMaximumIntegerDigits(Short.MAX_VALUE);
				format.setMaximumFractionDigits(Short.MAX_VALUE);
			}
			format.setParseBigDecimal(true);
			DECIMAL_FORMATS_CACHE.put(formatterKey, format);
		}
		return format;
	}
	
	protected static final class FormatKey {
		private final String pattern;
		private final Locale locale;

		protected static FormatKey getInstance(String pattern, Locale locale) {
			// Caching of FormatKey instances can be implemented here
			return new FormatKey(pattern, locale);
		}

		private FormatKey(String pattern, Locale locale) {
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
			if (!(obj instanceof FormatKey))
				return false;
			FormatKey other = (FormatKey) obj;
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
