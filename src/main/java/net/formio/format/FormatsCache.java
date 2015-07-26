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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common cache for {@link DateFormat}s, {@link NumberFormat}s and {@link DecimalFormat}s.
 * @author Radek Beran
 */
class FormatsCache {
	
	private static final Map<FormatKey, DateFormat> DATE_FORMATS_CACHE = new ConcurrentHashMap<FormatKey, DateFormat>();
	private static final Map<FormatKey, DecimalFormat> DECIMAL_FORMATS_CACHE = new ConcurrentHashMap<FormatKey, DecimalFormat>();
	static final String DEFAULT_DATE_FORMAT = "d.M.yyyy";
	
	static DateFormat getOrCreateDateFormat(String pattern, Location loc) {
		// TODO: Use also time zone for formatting dates
		final FormatKey formatterKey = FormatKey.getInstance(pattern, loc);
		DateFormat format = DATE_FORMATS_CACHE.get(formatterKey);
		if (format == null) {
			if (pattern != null && !pattern.isEmpty()) {
				format = new SimpleDateFormat(pattern, loc.getLocale());
			} else {
				// Note: full precision could be expressed using pattern "yyyy-MM-dd'T'HH:mm:ss,S z"
				format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, loc.getLocale());
			}
			format.setLenient(false); // without heuristics - allowing only strict pattern 
			DATE_FORMATS_CACHE.put(formatterKey, format);
		}
		return format;
	}

	static DecimalFormat getOrCreateDecimalFormat(String pattern, Location loc) {
		final FormatKey formatterKey = FormatKey.getInstance(pattern, loc);
		DecimalFormat format = DECIMAL_FORMATS_CACHE.get(formatterKey);
		if (format == null) {
			if (pattern != null && !pattern.isEmpty()) {
				// Set grouping separator and decimal separator specific for given locale
				DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(loc.getLocale());
				format = new DecimalFormat(pattern, df.getDecimalFormatSymbols());
				format.setMaximumIntegerDigits(Short.MAX_VALUE);
				format.setMaximumFractionDigits(Short.MAX_VALUE);
			} else { 
				// Formatter for locale bears grouping separator and decimal separator specific for given locale
				format = (DecimalFormat)NumberFormat.getInstance(loc.getLocale());
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
		private final Location location;

		protected static FormatKey getInstance(String pattern, Location loc) {
			// Caching of FormatKey instances can be implemented here
			return new FormatKey(pattern, loc);
		}

		private FormatKey(String pattern, Location loc) {
			this.pattern = pattern;
			this.location = loc;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((location == null) ? 0 : location.hashCode());
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
			if (location == null) {
				if (other.location != null)
					return false;
			} else if (!location.equals(other.location))
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
