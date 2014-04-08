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

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.Assert;
import net.formio.format.BasicFormatters;
import net.formio.format.Formatter;
import net.formio.format.FormatterNotFoundException;
import net.formio.format.FormattersCache;
import net.formio.format.StringParseException;
import net.formio.format.BasicFormatters.FormattersKey;
import net.formio.format.FormattersCache.FormatterKey;

import org.junit.Test;

/**
 * Tests for {@link BasicFormatters}.
 * @author Radek Beran
 */
public class BasicFormattersTest {

	@Test
	public void testClassesUsability() {
		Assert.assertEquals(Integer.class, Integer.class);
		Assert.assertEquals(BigDecimal.class, BigDecimal.class);
		Assert.assertFalse(BigInteger.class.equals(BigDecimal.class));
		Assert.assertFalse(BigInteger.class.equals(Number.class));
	}
	
	@Test
	public void testParseEnDecimal() {
		final BasicFormatters formatters = new BasicFormatters(new Locale("en", "EN"));
		BigDecimal bd = formatters.parseFromString("3,6", BigDecimal.class, new Locale("en"));
		Assert.assertNotNull("Result decimal is null", bd);
		// Separator "," considered as thousands delimiter
		Assert.assertEquals(new BigDecimal("36"), bd);
		
		BigDecimal bd2 = formatters.parseFromString("3.6", BigDecimal.class, new Locale("en"));
		Assert.assertNotNull("Result decimal is null", bd2);
		Assert.assertEquals(new BigDecimal("3.6"), bd2);
	}
	
	@Test
	public void testParseFromString() {
		final BasicFormatters formatters = new BasicFormatters(new Locale("cs", "CZ"));
		// for caching debugging: final BasicFormatters formatters2 = new BasicFormatters(new Locale("cs", "CZ"));
		
		Date date = formatters.parseFromString("12.3.2014 16:30", Date.class, "dd.MM.yyyy HH:mm");
		Assert.assertNotNull("Result date is null", date);
		Calendar cal = getCalendarForDate(date);
		Assert.assertEquals(12, cal.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(3 - 1, cal.get(Calendar.MONTH));
		Assert.assertEquals(2014, cal.get(Calendar.YEAR));
		Assert.assertEquals(16, cal.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(30, cal.get(Calendar.MINUTE));
		
		BigDecimal bd = formatters.parseFromString("3,6", BigDecimal.class, "#.#");
		Assert.assertNotNull("Result decimal is null", bd);
		Assert.assertEquals(new BigDecimal("3.6"), bd);
		
		BigDecimal bd2 = formatters.parseFromString("3.6", BigDecimal.class, "#.#");
		Assert.assertNotNull("Result decimal is null", bd2);
		Assert.assertEquals(new BigDecimal("3.6"), bd2);
		
		BigDecimal bd3 = formatters.parseFromString("3.6", BigDecimal.class);
		Assert.assertNotNull("Result decimal is null", bd3);
		Assert.assertEquals(new BigDecimal("3.6"), bd3);
		
		BigDecimal bd4 = formatters.parseFromString("3.6", BigDecimal.class, new Locale("en"));
		Assert.assertNotNull("Result decimal is null", bd4);
		Assert.assertEquals(new BigDecimal("3.6"), bd4);
		
		Double d = formatters.parseFromString("2.1", Double.class);
		Assert.assertNotNull("Result double is null", d);
		Assert.assertEquals(2.1, d.doubleValue(), 0.001);
		
		Double d2 = formatters.parseFromString("2,1", Double.class);
		Assert.assertNotNull("Result double is null", d2);
		Assert.assertEquals(2.1, d2.doubleValue(), 0.001);
		
		BigInteger bi = formatters.parseFromString("123", BigInteger.class);
		Assert.assertNotNull("Result big integer is null", bi);
		Assert.assertEquals(BigInteger.valueOf(123), bi);
		
		BigInteger bi2 = formatters.parseFromString("123,3", BigInteger.class);
		Assert.assertNotNull("Result big integer is null", bi2);
		Assert.assertEquals(BigInteger.valueOf(123), bi2);
		
		Short s = formatters.parseFromString("6", Short.class);
		Assert.assertNotNull("Result Short is null", s);
		Assert.assertEquals(Short.valueOf("6"), s);
		
		Short s2 = formatters.parseFromString("45.45", Short.class);
		Assert.assertNotNull("Result Short is null", s2);
		Assert.assertEquals(Short.valueOf("45"), s2);
		
		Integer i = formatters.parseFromString("45", Integer.class);
		Assert.assertNotNull("Result Integer is null", i);
		Assert.assertEquals(Integer.valueOf(45), i);
		
		Integer i2 = formatters.parseFromString("45.45", Integer.class);
		Assert.assertNotNull("Result Integer is null", i2);
		Assert.assertEquals(Integer.valueOf(45), i2);
		
		Long l = formatters.parseFromString("45", Long.class);
		Assert.assertNotNull("Result Long is null", l);
		Assert.assertEquals(Long.valueOf(45), l);
		
		Long l2 = formatters.parseFromString("45.45", Long.class);
		Assert.assertNotNull("Result Long is null", l2);
		Assert.assertEquals(Long.valueOf(45), l2);
		
		MluvnickyPad e = formatters.parseFromString("GENITIV", MluvnickyPad.class);
		Assert.assertNotNull("Result Enum is null", e);
		Assert.assertEquals(MluvnickyPad.GENITIV, e);
	}
	
	@Test
	public void testParseFromString2() {
		final Locale locale = new Locale("cs", "CZ");
		final BasicFormatters formatters = new BasicFormatters(locale);
		Assert.assertEquals("true", formatters.makeString(Boolean.TRUE));
		Assert.assertEquals("false", formatters.makeString(Boolean.FALSE));
		Assert.assertEquals("1", formatters.makeString(Byte.valueOf((byte)1)));
		Assert.assertEquals("1", formatters.makeString(Short.valueOf((short)1))); // NOPMD by Radek on 2.3.14 19:27
		Assert.assertEquals("1", formatters.makeString(Integer.valueOf(1)));
		Assert.assertEquals("1", formatters.makeString(Long.valueOf(1L)));
		Assert.assertEquals("hello", formatters.makeString("hello"));
		Assert.assertEquals("3", formatters.makeString(new BigInteger("3")));
		Assert.assertEquals("2,1", formatters.makeString(Double.valueOf(2.1)));
		
		
		String dateStr = "12.03.2014 16:30";
		String dateFormat = "dd.MM.yyyy HH:mm";
		Date date = formatters.parseFromString(dateStr, Date.class, dateFormat);
		Assert.assertEquals(dateStr, formatters.makeString(date, dateFormat));
		
		String bdStr = "3,6";
		String bdFormat = "#.#";
		BigDecimal bd = formatters.parseFromString(bdStr, BigDecimal.class, bdFormat);
		Assert.assertEquals(bdStr, formatters.makeString(bd, bdFormat));
		
		Assert.assertEquals(MluvnickyPad.GENITIV.name(), formatters.makeString(MluvnickyPad.GENITIV));
		Assert.assertEquals(null, formatters.makeString(null));
	}
	
	@Test
	public void testFormattersToAndFromString() {
		try {
			final Locale locale = new Locale("cs", "CZ");
			final BasicFormatters formatters = new BasicFormatters(locale);
			
			Date date = new SimpleDateFormat(FormattersCache.DEFAULT_DATE_FORMAT).parse("1.12.2014");
			Assert.assertEquals(date, formatters.parseFromString(formatters.makeString(date), Date.class));
			Assert.assertEquals(Boolean.TRUE, formatters.parseFromString(formatters.makeString(Boolean.TRUE), Boolean.class));
			Assert.assertEquals(Boolean.FALSE, formatters.parseFromString(formatters.makeString(Boolean.FALSE), Boolean.class));
			Assert.assertEquals(Byte.valueOf((byte)1), formatters.parseFromString(formatters.makeString(Byte.valueOf((byte)1)), Byte.class));
			Assert.assertEquals(Short.valueOf((short)1), formatters.parseFromString(formatters.makeString(Short.valueOf((short)1)), Short.class)); // NOPMD by Radek on 2.3.14 19:27
			Assert.assertEquals(Integer.valueOf(1), formatters.parseFromString(formatters.makeString(Integer.valueOf(1)), Integer.class));
			Assert.assertEquals(Long.valueOf(1L), formatters.parseFromString(formatters.makeString(Long.valueOf(1L)), Long.class));
			Assert.assertEquals(new BigInteger("3"), formatters.parseFromString(formatters.makeString(new BigInteger("3")), BigInteger.class));
			Assert.assertEquals("hello, dolly", formatters.parseFromString(formatters.makeString("hello, dolly"), String.class));
			String dStr = "123456789.0123456789";
			String dToStr = formatters.makeString(Double.valueOf(dStr));
			// System.out.println(dToStr);
			Assert.assertEquals(Double.valueOf(dStr), formatters.parseFromString(dToStr, Double.class));
			String bdStr = "123456789123456789876543210.012345678987654321987654321";
			String bdToStr = formatters.makeString(new BigDecimal(bdStr));
			// System.out.println(bdToStr);
			Assert.assertEquals(new BigDecimal(bdStr), formatters.parseFromString(bdToStr, BigDecimal.class));
			Assert.assertEquals(MluvnickyPad.GENITIV, formatters.parseFromString(formatters.makeString(MluvnickyPad.GENITIV), MluvnickyPad.class));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
	@Test(expected=FormatterNotFoundException.class)
	public void testNonRegisteredParser() {
		final BasicFormatters formatters = new BasicFormatters(new Locale("cs", "CZ"));
		formatters.parseFromString("12.3.2014 16:30", Day.class, "dd.MM.yyyy HH:mm");
	}
	
	@Test(expected=StringParseException.class)
	public void testIncorrectFormat() {
		final BasicFormatters formatters = new BasicFormatters(new Locale("cs", "CZ"));
		formatters.parseFromString("12.3.2014 16:30", Date.class, "jj.ee.yyyy HH:mm");
	}
	
	@Test(expected=StringParseException.class)
	public void testTooSpecificFormat() {
		final BasicFormatters formatters = new BasicFormatters(new Locale("cs", "CZ"));
		// But hours and minutes are not present in the string
		Date date2 = formatters.parseFromString("12.3.2014", Date.class, "dd.MM.yyyy HH:mm");
		Assert.assertNotNull("Result date is null", date2);
		Calendar cal2 = getCalendarForDate(date2);
		Assert.assertEquals(12, cal2.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(3 - 1, cal2.get(Calendar.MONTH));
		Assert.assertEquals(2014, cal2.get(Calendar.YEAR));
	}
	
	@Test
	public void testExtensibility() {
		final BasicFormatters stringParsers = new ExtendedFormatters(new Locale("cs", "CZ"));
		// Extended formatter supporting special Day class:
		Day date = stringParsers.parseFromString("12.3.2014", Day.class, "dd.MM.yyyy");
		Assert.assertNotNull("Result date is null", date);
		Calendar cal = getCalendarForDate(date);
		Assert.assertEquals(12, cal.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(3 - 1, cal.get(Calendar.MONTH));
		Assert.assertEquals(2014, cal.get(Calendar.YEAR));
	}
	
	@Test
	public void testParsersKeyEquality() {
		Assert.assertEquals(FormattersKey.getInstance(null, null), FormattersKey.getInstance(null, null));
		Assert.assertEquals(FormattersKey.getInstance(null, new Locale("cs", "CZ")), FormattersKey.getInstance(null, new Locale("cs", "CZ")));
		Assert.assertEquals(FormattersKey.getInstance(BasicFormatters.class, null), FormattersKey.getInstance(BasicFormatters.class, null));
		Assert.assertEquals(FormattersKey.getInstance(BasicFormatters.class, new Locale("cs", "CZ")), FormattersKey.getInstance(BasicFormatters.class, new Locale("cs", "CZ")));
	}
	
	@Test
	public void testFormatterKeyEquality() {
		Assert.assertEquals(FormatterKey.getInstance(null, null), FormatterKey.getInstance(null, null));
		Assert.assertEquals(FormatterKey.getInstance(null, new Locale("cs", "CZ")), FormatterKey.getInstance(null, new Locale("cs", "CZ")));
		Assert.assertEquals(FormatterKey.getInstance("dd.MM.yyyy", null), FormatterKey.getInstance("dd.MM.yyyy", null));
		Assert.assertEquals(FormatterKey.getInstance("dd.MM.yyyy", new Locale("cs", "CZ")), FormatterKey.getInstance("dd.MM.yyyy", new Locale("cs", "CZ")));
	}
	
	private Calendar getCalendarForDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	
	static class ExtendedFormatters extends BasicFormatters {
		
		public ExtendedFormatters(Locale locale) {
			super(locale);
		}
		
		@Override
		protected Map<Class<?>, Formatter<?>> registerFormatters(Locale locale) {
			Map<Class<?>, Formatter<?>> formatters = new HashMap<Class<?>, Formatter<?>>();
			formatters.putAll(super.registerFormatters(locale));
			formatters.put(Day.class, new Formatter<Day>() {
				
				@Override
				public Day parseFromString(String str, Class<Day> destClass, String formatPattern, Locale locale) {
					try {
						Date date = FormattersCache.getOrCreateDateFormatter(formatPattern, getLocaleElseDefault(locale)).parse(str);
						return date != null ? Day.valueOf(date) : null;
					} catch (Exception ex) {
						throw new StringParseException(Day.class, str, ex);
					}
				}
				
				@Override
				public String makeString(Day value, String formatPattern, Locale locale) {
					return FormattersCache.getOrCreateDateFormatter(
						formatPattern, getLocaleElseDefault(locale)).format(value);
				}
	
			});
			
			// Possible caching using locale can be implemented...
			return Collections.unmodifiableMap(formatters);
		}
	}

}
