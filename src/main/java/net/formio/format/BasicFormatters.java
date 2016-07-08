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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
	public BasicFormatters() {
		this.formatters = registerFormatters();
	}

	@Override
	public <T> T parseFromString(String str, Class<T> destClass, String formatPattern, Location loc) {
		Formatter<T> formatter = (Formatter<T>) formatters.get(destClass);
		if (formatter == null) {
			// fallback for enumerations
			if (Enum.class.isAssignableFrom(destClass)) {
				formatter = (Formatter<T>) ENUM_FORMATTER;
			}
		}
		if (formatter == null) {
			throw new FormatterNotFoundException(destClass);
		}
		return formatter.parseFromString(str, destClass, formatPattern, loc);
	}

	public <T> T parseFromString(String str, Class<T> cls, Location loc) {
		return parseFromString(str, cls, null, loc);
	}
	
	@Override
	public <T> String makeString(T value, String formatPattern, Location loc) {
		if (value == null) return null;
		Formatter<T> formatter = (Formatter<T>) formatters.get(value.getClass());
		if (formatter == null) {
			// fallback to common string maker
			formatter = (Formatter<T>)COMMON_STR_MAKER;
		}
		return formatter.makeString(value, formatPattern, loc);
	}
	
	public <T> String makeString(T value, Location loc) {
		return makeString(value, (String)null, loc);
	}
	
	@Override
	public boolean canHandle(Class<?> cls) {
		return cls.isAssignableFrom(String.class) || cls.isEnum() || this.formatters.containsKey(cls);
	}

	// -- API to override --
	/**
	 * Returns all formatters available.
	 * 
	 * @return
	 */
	protected Map<Class<?>, Formatter<?>> registerFormatters() {
		final Class<? extends Formatters> formattersKey = getClass();
		Map<Class<?>, Formatter<?>> formatters = FORMATTERS_CACHE.get(formattersKey);
		if (formatters == null) {
			formatters = new HashMap<Class<?>, Formatter<?>>();
			formatters.put(Boolean.class, BOOLEAN_FORMATTER);
			formatters.put(boolean.class, BOOLEAN_FORMATTER);
			formatters.put(String.class, STRING_FORMATTER);
			
			final Formatter<Date> dateFormatter = new Formatter<Date>() {

				@Override
				public Date parseFromString(String str, Class<Date> destClass,
						String formatPattern, Location loc) {
					try {
						return FormatsCache.getOrCreateDateFormat(formatPattern, loc).parse(str);
					} catch (Exception ex) {
						throw new StringParseException(Date.class, str, ex);
					}
				}
				
				@Override
				public String makeString(Date value, String formatPattern, Location loc) {
					return FormatsCache.getOrCreateDateFormat(
						formatPattern, loc).format(value);
				}

			};
			
			formatters.put(Date.class, dateFormatter);

			final Formatter<Byte> byteFormatter = new Formatter<Byte>() {

				@Override
				public Byte parseFromString(String str, Class<Byte> destClass,
						String formatPattern, Location loc) {
					try {
						String amendedStr = removeDecimalPart(str, loc.getLocale());
						return Byte.valueOf(FormatsCache.getOrCreateDecimalFormat(formatPattern, loc)
							.parse(amendedStr).byteValue());
					} catch (Exception ex) {
						throw new StringParseException(Byte.class, str, ex);
					}
				}

				@Override
				public String makeString(Byte value, String formatPattern, Location loc) {
					return COMMON_STR_MAKER.makeString(value, formatPattern, loc);
				}

			};
			formatters.put(Byte.class, byteFormatter);
			formatters.put(byte.class, byteFormatter);

			final Formatter<Short> shortFormatter = new Formatter<Short>() {

				@Override
				public Short parseFromString(String str,
						Class<Short> destClass, String formatPattern,
						Location loc) {
					try {
						String amendedStr = removeDecimalPart(str, loc.getLocale());
						return Short.valueOf(FormatsCache.getOrCreateDecimalFormat(formatPattern, loc)
							.parse(amendedStr).shortValue());
					} catch (Exception ex) {
						throw new StringParseException(Short.class, str, ex);
					}
				}

				@Override
				public String makeString(Short value, String formatPattern, Location loc) {
					return COMMON_STR_MAKER.makeString(value, formatPattern, loc);
				}

			};
			formatters.put(Short.class, shortFormatter);
			formatters.put(short.class, shortFormatter); // NOPMD by Radek on 2.3.14 19:10

			final Formatter<Integer> integerFormatter = new Formatter<Integer>() {

				@Override
				public Integer parseFromString(String str,
						Class<Integer> destClass, String formatPattern,
						Location loc) {
					try {
						String amendedStr = removeDecimalPart(str, loc.getLocale());
						return Integer.valueOf(FormatsCache.getOrCreateDecimalFormat(formatPattern, loc)
							.parse(amendedStr).intValue());
					} catch (Exception ex) {
						throw new StringParseException(Integer.class, str, ex);
					}
				}

				@Override
				public String makeString(Integer value, String formatPattern, Location loc) {
					return COMMON_STR_MAKER.makeString(value, formatPattern, loc);
				}

			};
			formatters.put(Integer.class, integerFormatter);
			formatters.put(int.class, integerFormatter);

			final Formatter<Long> longFormatter = new Formatter<Long>() {

				@Override
				public Long parseFromString(String str, Class<Long> destClass,
						String formatPattern, Location loc) {
					try {
						String amendedStr = removeDecimalPart(str, loc.getLocale());
						return Long.valueOf(FormatsCache.getOrCreateDecimalFormat(formatPattern, loc)
							.parse(amendedStr).byteValue());
					} catch (Exception ex) {
						throw new StringParseException(Long.class, str, ex);
					}
				}

				@Override
				public String makeString(Long value, String formatPattern, Location loc) {
					return COMMON_STR_MAKER.makeString(value, formatPattern, loc);
				}

			};
			formatters.put(Long.class, longFormatter);
			formatters.put(long.class, longFormatter);

			final Formatter<BigInteger> bigIntegerFormatter = new Formatter<BigInteger>() {

				@Override
				public BigInteger parseFromString(String str,
						Class<BigInteger> destClass, String formatPattern,
						Location loc) {
					try {
						return new BigInteger(removeDecimalPart(str, loc.getLocale()));
					} catch (Exception ex) {
						throw new StringParseException(BigInteger.class, str, ex);
					}
				}

				@Override
				public String makeString(BigInteger value, String formatPattern, Location loc) {
					return COMMON_STR_MAKER.makeString(value, formatPattern, loc);
				}

			};
			formatters.put(BigInteger.class, bigIntegerFormatter);

			final Formatter<Double> doubleFormatter = new Formatter<Double>() {

				@Override
				public Double parseFromString(String str, Class<Double> destClass, String formatPattern, Location loc) {
					try {
						return Double.valueOf(FormatsCache.getOrCreateDecimalFormat(formatPattern, loc)
							.parse(str).doubleValue());
					} catch (Exception ex) {
						throw new StringParseException(Double.class, str, ex);
					}
				}
				
				@Override
				public String makeString(Double value, String formatPattern, Location loc) {
					return FormatsCache.getOrCreateDecimalFormat(formatPattern, loc).format(value);
				}

			};
			formatters.put(Double.class, doubleFormatter);
			formatters.put(double.class, doubleFormatter);

			final Formatter<BigDecimal> bigDecimalFormatter = new Formatter<BigDecimal>() {

				@Override
				public BigDecimal parseFromString(String str, Class<BigDecimal> destClass, String formatPattern, Location loc) {
					BigDecimal bd = null;
					try {
						DecimalFormat format = FormatsCache.getOrCreateDecimalFormat(formatPattern, loc);
						format.setParseBigDecimal(true);
						bd = (BigDecimal) format.parseObject(str);
					} catch (Exception ex) {
						throw new StringParseException(BigDecimal.class, str, ex);
					}
					if (bd == null && str != null) {
						throw new StringParseException(BigDecimal.class, str, null);
					}
					return bd;
				}
				
				@Override
				public String makeString(BigDecimal value, String formatPattern, Location loc) {
					return FormatsCache.getOrCreateDecimalFormat(formatPattern, loc).format(value);
				}
			};
			formatters.put(BigDecimal.class, bigDecimalFormatter);
			formatters = Collections.unmodifiableMap(formatters);
			FORMATTERS_CACHE.put(formattersKey, formatters);
		}
		return formatters;
	}

	protected static final Formatter<Boolean> BOOLEAN_FORMATTER = new Formatter<Boolean>() {

		@Override
		public Boolean parseFromString(String str, Class<Boolean> destClass,
				String formatPattern, Location loc) {
			if (str == null || str.isEmpty()) return Boolean.FALSE;
			String amendedStr = str.toLowerCase();
			return Boolean.valueOf(amendedStr.equals("t") || amendedStr.equals("y") || amendedStr.equals("true") || amendedStr.equals("1") || amendedStr.equals("on"));
		}

		@Override
		public String makeString(Boolean value, String formatPattern, Location loc) {
			return COMMON_STR_MAKER.makeString(value, formatPattern, loc);
		}

	};
	
	protected static final Formatter<String> STRING_FORMATTER = new Formatter<String>() {

		@Override
		public String parseFromString(String str, Class<String> destClass,
				String formatPattern, Location loc) {
			return str;
		}

		@Override
		public String makeString(String value, String formatPattern, Location loc) {
			return value;
		}

	};

	/**
	 * Enumeration formatter. Does not convert string to uppercase (not all
	 * enumerations have their constants in uppercase form.
	 */
	protected static class EnumFormatter<E extends Enum<E>> implements Formatter<E> {

		@Override
		public E parseFromString(String str, Class<E> destClass, String formatPattern, Location loc) {
			try {
				if (str == null || str.isEmpty())
					return null;
				return Enum.valueOf(destClass, str);
			} catch (Exception ex) {
				throw new StringParseException(destClass, str, ex);
			}
		}

		@Override
		public String makeString(E value, String formatPattern, Location loc) {
			return value != null ? value.name() : "";
		}
		
	}
	protected static final EnumFormatter<?> ENUM_FORMATTER = new EnumFormatter();
	
	/**
	 * Auxiliary formatter used only for converting objects to strings using toString method.
	 */
	protected static final Formatter<Object> COMMON_STR_MAKER = new Formatter<Object>() {

		@Override
		public String makeString(Object value, String formatPattern, Location loc) {
			return value != null ? "" + value : "";
		}
		
		@Override
		public Object parseFromString(String str, Class<Object> destClass,
				String formatPattern, Location loc) {
			return null;
		}

	};

	// -- Internal implementation --
	private final Map<Class<?>, Formatter<?>> formatters;

	private static final Map<Class<? extends Formatters>, Map<Class<?>, Formatter<?>>> FORMATTERS_CACHE = 
		new ConcurrentHashMap<Class<? extends Formatters>, Map<Class<?>, Formatter<?>>>();

	static String removeDecimalPart(String str, Locale locale) {
		char decimalSep = '.';
		if (locale != null) {
			DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
			decimalSep = dfs.getDecimalSeparator();
		}
		String amendedStr = str;
		if (amendedStr != null && !amendedStr.isEmpty()) {
			int pointIndex = amendedStr.indexOf(decimalSep);
			if (pointIndex == -1 && locale != null && locale.getLanguage().toLowerCase().equals("cs")) {
				// Czech language does not use thousand separators...
				pointIndex = amendedStr.indexOf(".");
			}
			if (pointIndex > -1) {
				amendedStr = amendedStr.substring(0, pointIndex);
			}
		}
		return amendedStr;
	}
}
