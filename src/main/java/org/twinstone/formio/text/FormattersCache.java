package org.twinstone.formio.text;

import java.io.Serializable;
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
	
	private static final Map<FormatterKey, DateFormat> dateFormattersCache = new ConcurrentHashMap<FormatterKey, DateFormat>();
	private static final Map<FormatterKey, NumberFormat> numberFormattersCache = new ConcurrentHashMap<FormatterKey, NumberFormat>();
	private static final Map<FormatterKey, DecimalFormat> decimalFormattersCache = new ConcurrentHashMap<FormatterKey, DecimalFormat>();
	
	static DateFormat getOrCreateDateFormatter(String pattern, Locale locale) {
		final FormatterKey formatterKey = FormatterKey.getInstance(pattern,
				locale);
		DateFormat format = dateFormattersCache.get(formatterKey);
		if (format == null) {
			if (pattern != null && !pattern.isEmpty()) {
				format = new SimpleDateFormat(pattern);
			} else {
				// TODO: Support for configurable date style (configuring DateFormat.FULL, DateFormat.SHORT, ...)
				// DateFormat.FULL, otherwise precision will be lost when converting string back to the date
				// Updated: DateFormat.getDateInstance(DateFormat.FULL, locale); is not sufficient
				format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,S z", locale);
			}
			format.setLenient(false); // without heuristics - allowing only strict pattern 
			dateFormattersCache.put(formatterKey, format);
		}
		return format;
	}

	static NumberFormat getOrCreateNumberFormatter(String pattern, Locale locale) {
		final FormatterKey formatterKey = FormatterKey.getInstance(pattern,
				locale);
		NumberFormat format = numberFormattersCache.get(formatterKey);
		if (format == null) {
			if (pattern != null && !pattern.isEmpty()) {
				format = new DecimalFormat(pattern);
			} else {
				format = NumberFormat.getInstance(locale);
				format.setMaximumIntegerDigits(Short.MAX_VALUE);
				format.setMaximumFractionDigits(Short.MAX_VALUE);
			}
			numberFormattersCache.put(formatterKey, format);
		}
		return format;
	}

	static DecimalFormat getOrCreateDecimalFormatter(String pattern, Locale locale) {
		final FormatterKey formatterKey = FormatterKey.getInstance(pattern, locale);
		DecimalFormat format = decimalFormattersCache.get(formatterKey);
		if (format == null) {
			if (pattern != null && !pattern.isEmpty()) {
				format = new DecimalFormat(pattern);
			} else { 
				format = (DecimalFormat) NumberFormat.getInstance(locale);
				format.setMaximumIntegerDigits(Short.MAX_VALUE);
				format.setMaximumFractionDigits(Short.MAX_VALUE);
			}
			format.setParseBigDecimal(true);
			decimalFormattersCache.put(formatterKey, format);
		}
		return format;
	}
	
	protected static final class FormatterKey implements Serializable {
		private static final long serialVersionUID = -1461552906099512286L;
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
