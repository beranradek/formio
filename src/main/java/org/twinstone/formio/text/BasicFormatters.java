package org.twinstone.formio.text;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Transforms objects of common type(s) to a String and back from a String.
 * Different subclasses with different registered formatters can be
 * created: Method {@link #registerFormatters()} can be overridden.
 * 
 * @author Radek Beran
 */
public class BasicFormatters implements Formatters {

	// -- Public API --
	/**
	 * Intentionally left default constructor, that can be used with dependency
	 * injection. Subclasses can define and inject their own mechanisms to
	 * resolve string values to instances of classes.
	 */
	public BasicFormatters() {
		this(Locale.getDefault());
	}

	public BasicFormatters(Locale locale) {
		this.formatters = registerFormatters(locale);
		this.defaultLocale = locale;
	}

	@Override
	public <T> T parseFromString(Class<T> cls, String str, String formatPattern, Locale locale) {
		Formatter<T> formatter = (Formatter<T>) formatters.get(cls);
		if (formatter == null) {
			// fallback for enumerations
			if (Enum.class.isAssignableFrom(cls)) {
				formatter = (Formatter<T>) ENUM_FORMATTER;
			}
		}
		if (formatter == null)
			throw new FormatterNotFoundException(cls);
		return formatter.parseFromString(str, cls, formatPattern, locale);
	}

	public <T> T parseFromString(Class<T> cls, String str, String formatPattern) {
		return parseFromString(cls, str, formatPattern, getDefaultLocale());
	}

	public <T> T parseFromString(Class<T> cls, String str, Locale locale) {
		return parseFromString(cls, str, null, locale);
	}

	public <T> T parseFromString(Class<T> cls, String str) {
		return parseFromString(cls, str, null, getDefaultLocale());
	}
	
	@Override
	public <T> String makeString(T value, String formatPattern, Locale locale) {
		if (value == null) return null;
		Formatter<T> formatter = (Formatter<T>) formatters.get(value.getClass());
		if (formatter == null) {
			// fallback to common string maker
			formatter = (Formatter<T>)COMMON_STRING_MAKER;
		}
		return formatter.makeString(value, formatPattern, locale);
	}
	
	public <T> String makeString(T value, String formatPattern) {
		return makeString(value, formatPattern, getDefaultLocale());
	}
	
	public <T> String makeString(T value) {
		return makeString(value, null, getDefaultLocale());
	}

	// -- API to override --
	protected interface Formatter<T> {
		/**
		 * Parses object from given string.
		 * @param str string to parse
		 * @param destClass desired class of parsed object (useful for specifying concrete enumeration class)
		 * @param formatPattern specified format of string; or {@code null}
		 * @param locale specified locale of string; or {@code null}
		 * @return parsed object
		 */
		T parseFromString(String str, Class<T> destClass, String formatPattern,
				Locale locale);
		
		/**
		 * Creates string from given value.
		 * @param value value to convert to string
		 * @param formatPattern specified format of string; or {@code null}
		 * @param locale specified locale of string; or {@code null}
		 * @return result string
		 */
		String makeString(T value, String formatPattern, Locale locale);
	}

	/**
	 * Returns all formatters available.
	 * 
	 * @return
	 */
	protected Map<Class<?>, Formatter<?>> registerFormatters(Locale locale) {
		final FormattersKey formattersKey = FormattersKey
				.getInstance(getClass(), locale);
		Map<Class<?>, Formatter<?>> formatters = formattersCache.get(formattersKey);
		if (formatters == null) {
			formatters = new HashMap<Class<?>, Formatter<?>>();
			formatters.put(Boolean.class, BOOLEAN_FORMATTER);
			formatters.put(boolean.class, BOOLEAN_FORMATTER);
			formatters.put(String.class, STRING_FORMATTER);
			
			final Formatter<Date> dateFormatter = new Formatter<Date>() {

				@Override
				public Date parseFromString(String str, Class<Date> destClass,
						String formatPattern, Locale locale) {
					try {
						return FormattersCache.getOrCreateDateFormatter(
							formatPattern, getLocaleElseDefault(locale)).parse(str);
					} catch (Exception ex) {
						throw new StringParseException(Date.class, str, ex);
					}
				}
				
				@Override
				public String makeString(Date value, String formatPattern, Locale locale) {
					return FormattersCache.getOrCreateDateFormatter(
						formatPattern, getLocaleElseDefault(locale)).format(value);
				}

			};
			
			formatters.put(Date.class, dateFormatter);

			final Formatter<Byte> byteFormatter = new Formatter<Byte>() {

				@Override
				public Byte parseFromString(String str, Class<Byte> destClass,
						String formatPattern, Locale locale) {
					try {
						String amendedStr = removeDecimalPart(str);
						return Byte.valueOf(FormattersCache.getOrCreateNumberFormatter(
							formatPattern, getLocaleElseDefault(locale)).parse(amendedStr).byteValue());
					} catch (Exception ex) {
						throw new StringParseException(Byte.class, str, ex);
					}
				}

				@Override
				public String makeString(Byte value, String formatPattern, Locale locale) {
					return COMMON_STRING_MAKER.makeString(value, formatPattern, locale);
				}

			};
			formatters.put(Byte.class, byteFormatter);
			formatters.put(byte.class, byteFormatter);

			final Formatter<Short> shortFormatter = new Formatter<Short>() {

				@Override
				public Short parseFromString(String str,
						Class<Short> destClass, String formatPattern,
						Locale locale) {
					try {
						String amendedStr = removeDecimalPart(str);
						return Short.valueOf(FormattersCache.getOrCreateNumberFormatter(
							formatPattern, getLocaleElseDefault(locale)).parse(amendedStr).shortValue());
					} catch (Exception ex) {
						throw new StringParseException(Short.class, str, ex);
					}
				}

				@Override
				public String makeString(Short value, String formatPattern, Locale locale) {
					return COMMON_STRING_MAKER.makeString(value, formatPattern, locale);
				}

			};
			formatters.put(Short.class, shortFormatter);
			formatters.put(short.class, shortFormatter);

			final Formatter<Integer> integerFormatter = new Formatter<Integer>() {

				@Override
				public Integer parseFromString(String str,
						Class<Integer> destClass, String formatPattern,
						Locale locale) {
					try {
						String amendedStr = removeDecimalPart(str);
						return Integer.valueOf(FormattersCache.getOrCreateNumberFormatter(
							formatPattern, getLocaleElseDefault(locale)).parse(amendedStr).intValue());
					} catch (Exception ex) {
						throw new StringParseException(Integer.class, str, ex);
					}
				}

				@Override
				public String makeString(Integer value, String formatPattern, Locale locale) {
					return COMMON_STRING_MAKER.makeString(value, formatPattern, locale);
				}

			};
			formatters.put(Integer.class, integerFormatter);
			formatters.put(int.class, integerFormatter);

			final Formatter<Long> longFormatter = new Formatter<Long>() {

				@Override
				public Long parseFromString(String str, Class<Long> destClass,
						String formatPattern, Locale locale) {
					try {
						String amendedStr = removeDecimalPart(str);
						return Long.valueOf(FormattersCache.getOrCreateNumberFormatter(
							formatPattern, getLocaleElseDefault(locale)).parse(amendedStr).byteValue());
					} catch (Exception ex) {
						throw new StringParseException(Long.class, str, ex);
					}
				}

				@Override
				public String makeString(Long value, String formatPattern, Locale locale) {
					return COMMON_STRING_MAKER.makeString(value, formatPattern, locale);
				}

			};
			formatters.put(Long.class, longFormatter);
			formatters.put(long.class, longFormatter);

			final Formatter<BigInteger> bigIntegerFormatter = new Formatter<BigInteger>() {

				@Override
				public BigInteger parseFromString(String str,
						Class<BigInteger> destClass, String formatPattern,
						Locale locale) {
					try {
						return new BigInteger(removeDecimalPart(str));
					} catch (Exception ex) {
						throw new StringParseException(BigInteger.class, str, ex);
					}
				}

				@Override
				public String makeString(BigInteger value, String formatPattern, Locale locale) {
					return COMMON_STRING_MAKER.makeString(value, formatPattern, locale);
				}

			};
			formatters.put(BigInteger.class, bigIntegerFormatter);

			final Formatter<Double> doubleFormatter = new Formatter<Double>() {

				@Override
				public Double parseFromString(String str,
						Class<Double> destClass, String formatPattern,
						Locale locale) {
					try {
						return Double.valueOf(FormattersCache.getOrCreateNumberFormatter(
							formatPattern, getLocaleElseDefault(locale)).parse(
								amendDecimalPoint(str, locale)).doubleValue());
					} catch (Exception ex) {
						throw new StringParseException(Double.class, str, ex);
					}
				}
				
				@Override
				public String makeString(Double value, String formatPattern,
						Locale locale) {
					return FormattersCache.getOrCreateNumberFormatter(
						formatPattern, getLocaleElseDefault(locale)).format(value);
				}

			};
			formatters.put(Double.class, doubleFormatter);
			formatters.put(double.class, doubleFormatter);

			final Formatter<BigDecimal> bigDecimalFormatter = new Formatter<BigDecimal>() {

				@Override
				public BigDecimal parseFromString(String str,
						Class<BigDecimal> destClass, String formatPattern,
						Locale locale) {
					try {
						DecimalFormat format = FormattersCache.getOrCreateDecimalFormatter(
							formatPattern, getLocaleElseDefault(locale));
						// Always isParseBigDecimal
						return (BigDecimal) format.parse(amendDecimalPoint(str, locale),
								new ParsePosition(0));
					} catch (Exception ex) {
						throw new StringParseException(BigDecimal.class, str,
								ex);
					}
				}
				
				@Override
				public String makeString(BigDecimal value,
						String formatPattern, Locale locale) {
					return FormattersCache.getOrCreateDecimalFormatter(
						formatPattern, getLocaleElseDefault(locale)).format(value);
				}
			};
			formatters.put(BigDecimal.class, bigDecimalFormatter);
			formatters = Collections.unmodifiableMap(formatters);
			formattersCache.put(formattersKey, formatters);
		}
		return formatters;
	}

	protected static final Formatter<Boolean> BOOLEAN_FORMATTER = new Formatter<Boolean>() {

		@Override
		public Boolean parseFromString(String str, Class<Boolean> destClass,
				String formatPattern, Locale locale) {
			if (str == null || str.isEmpty()) return Boolean.FALSE;
			String amendedStr = str.toLowerCase();
			return Boolean.valueOf(amendedStr.equals("t") || amendedStr.equals("y") || amendedStr.equals("true") || amendedStr.equals("1"));
		}

		@Override
		public String makeString(Boolean value, String formatPattern, Locale locale) {
			return COMMON_STRING_MAKER.makeString(value, formatPattern, locale);
		}

	};
	
	protected static final Formatter<String> STRING_FORMATTER = new Formatter<String>() {

		@Override
		public String parseFromString(String str, Class<String> destClass,
				String formatPattern, Locale locale) {
			return str;
		}

		@Override
		public String makeString(String value, String formatPattern, Locale locale) {
			return value;
		}

	};

	/**
	 * Enumeration formatter. Does not convert string to uppercase (not all
	 * enumerations have their constants in uppercase form.
	 */
	protected static class EnumFormatter<E extends Enum<E>> implements Formatter<E> {

		@Override
		public E parseFromString(String str, Class<E> destClass,
				String formatPattern, Locale locale) {
			try {
				if (str == null || str.isEmpty())
					return null;
				return Enum.valueOf(destClass, str);
			} catch (Exception ex) {
				throw new StringParseException(destClass, str, ex);
			}
		}

		@Override
		public String makeString(E value, String formatPattern, Locale locale) {
			return value != null ? value.name() : "";
		}
		
	}
	protected static final EnumFormatter<?> ENUM_FORMATTER = new EnumFormatter();
	
	/**
	 * Auxiliary formatter used only for converting objects to strings using toString method.
	 */
	protected static final Formatter<Object> COMMON_STRING_MAKER = new Formatter<Object>() {

		@Override
		public String makeString(Object value, String formatPattern, Locale locale) {
			return value != null ? (value + "") : "";
		}
		
		@Override
		public Object parseFromString(String str, Class<Object> destClass,
				String formatPattern, Locale locale) {
			return null;
		}

	};

	protected Locale getDefaultLocale() {
		return defaultLocale;
	}

	protected static final class FormattersKey implements Serializable {
		private static final long serialVersionUID = -1L;
		private final Class<? extends BasicFormatters> formattersClass;
		private final Locale locale;

		protected static FormattersKey getInstance(
				Class<? extends BasicFormatters> formattersClass, Locale locale) {
			// Caching of FormattersKey instances can be implemented here
			return new FormattersKey(formattersClass, locale);
		}

		private FormattersKey(Class<? extends BasicFormatters> formattersClass, Locale locale) {
			this.formattersClass = formattersClass;
			this.locale = locale;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((locale == null) ? 0 : locale.hashCode());
			result = prime
					* result
					+ ((formattersClass == null) ? 0 : formattersClass
							.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof FormattersKey)) {
				return false;
			}
			FormattersKey other = (FormattersKey) obj;
			if (locale == null) {
				if (other.locale != null)
					return false;
			} else if (!locale.equals(other.locale))
				return false;
			if (formattersClass == null) {
				if (other.formattersClass != null)
					return false;
			} else if (!formattersClass.equals(other.formattersClass))
				return false;
			return true;
		}

	}

	// -- Internal implementation --
	Locale getLocaleElseDefault(Locale locale) {
		return locale != null ? locale : getDefaultLocale();
	}
	
	private final Map<Class<?>, Formatter<?>> formatters;

	private final Locale defaultLocale;

	private static final Map<FormattersKey, Map<Class<?>, Formatter<?>>> formattersCache = new ConcurrentHashMap<FormattersKey, Map<Class<?>, Formatter<?>>>();

	static String amendDecimalPoint(String str, Locale locale) {
		String amendedStr = str;
		if (amendedStr != null && !amendedStr.isEmpty()) {
			if (locale != null
					&& locale.getLanguage().toLowerCase().equals("cs")) {
				amendedStr = amendedStr.replaceAll("\\.", ",");
			} else {
				amendedStr = amendedStr.replaceAll(",", ".");
			}
		}
		return amendedStr;
	}

	static String removeDecimalPart(String str) {
		String amendedStr = str;
		if (amendedStr != null && !amendedStr.isEmpty()) {
			int pointIndex = amendedStr.indexOf(".");
			if (pointIndex == -1)
				pointIndex = amendedStr.indexOf(",");
			if (pointIndex > -1) {
				amendedStr = amendedStr.substring(0, pointIndex);
			}
		}
		return amendedStr;
	}
}
