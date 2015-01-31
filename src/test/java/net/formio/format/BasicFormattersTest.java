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
		final BasicFormatters formatters = new BasicFormatters();
		Locale locale = new Locale("en");
		
		BigDecimal bd = formatters.parseFromString("3,6", BigDecimal.class, locale);
		Assert.assertNotNull("Result decimal is null", bd);
		// Separator "," considered as thousands delimiter
		Assert.assertEquals(BigDecimal.valueOf(36L), bd);
		
		BigDecimal bd2 = formatters.parseFromString("3.6", BigDecimal.class, locale);
		Assert.assertNotNull("Result decimal is null", bd2);
		Assert.assertEquals(BigDecimal.valueOf(36, 1), bd2);
	}
	
	@Test
	public void testMakeStringBigDecimal() {
		final BasicFormatters formatters = new BasicFormatters();
		final Locale csLocale = new Locale("cs");
		final Locale enLocale = new Locale("en");
		Assert.assertEquals("50,6", formatters.makeString(BigDecimal.valueOf(5060, 2), csLocale));
		Assert.assertEquals("50.6", formatters.makeString(BigDecimal.valueOf(5060, 2), enLocale));
	}
	
	@Test
	public void testParseFromString() {
		final BasicFormatters formatters = new BasicFormatters();
		
		Locale csLocale = new Locale("cs", "CZ");
		Date date = formatters.parseFromString("12.3.2014 16:30", Date.class, "dd.MM.yyyy HH:mm", csLocale);
		Assert.assertNotNull("Result date is null", date);
		Calendar cal = getCalendarForDate(date);
		Assert.assertEquals(12, cal.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(3 - 1, cal.get(Calendar.MONTH));
		Assert.assertEquals(2014, cal.get(Calendar.YEAR));
		Assert.assertEquals(16, cal.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(30, cal.get(Calendar.MINUTE));
		
		BigDecimal bd = formatters.parseFromString("3,6", BigDecimal.class, "#.#", csLocale);
		Assert.assertNotNull("Result decimal is null", bd);
		Assert.assertEquals(BigDecimal.valueOf(36, 1), bd);
		
		BigDecimal bd2 = formatters.parseFromString("3.6", BigDecimal.class, "#.#", csLocale);
		Assert.assertNotNull("Result decimal is null", bd2);
		Assert.assertEquals(BigDecimal.valueOf(36, 1), bd2);
		
		BigDecimal bd3 = formatters.parseFromString("3.6", BigDecimal.class, csLocale);
		Assert.assertNotNull("Result decimal is null", bd3);
		Assert.assertEquals(BigDecimal.valueOf(36, 1), bd3);
		
		BigDecimal bd4 = formatters.parseFromString("3.6", BigDecimal.class, new Locale("en"));
		Assert.assertNotNull("Result decimal is null", bd4);
		Assert.assertEquals(BigDecimal.valueOf(36, 1), bd4);
		
		Double d = formatters.parseFromString("2.1", Double.class, csLocale);
		Assert.assertNotNull("Result double is null", d);
		Assert.assertEquals(2.1, d.doubleValue(), 0.001);
		
		Double d2 = formatters.parseFromString("2,1", Double.class, csLocale);
		Assert.assertNotNull("Result double is null", d2);
		Assert.assertEquals(2.1, d2.doubleValue(), 0.001);
		
		BigInteger bi = formatters.parseFromString("123", BigInteger.class, csLocale);
		Assert.assertNotNull("Result big integer is null", bi);
		Assert.assertEquals(BigInteger.valueOf(123), bi);
		
		BigInteger bi2 = formatters.parseFromString("123,3", BigInteger.class, csLocale);
		Assert.assertNotNull("Result big integer is null", bi2);
		Assert.assertEquals(BigInteger.valueOf(123), bi2);
		
		Short s = formatters.parseFromString("6", Short.class, csLocale);
		Assert.assertNotNull("Result Short is null", s);
		Assert.assertEquals(Short.valueOf("6"), s);
		
		Short s2 = formatters.parseFromString("45.45", Short.class, csLocale);
		Assert.assertNotNull("Result Short is null", s2);
		Assert.assertEquals(Short.valueOf("45"), s2);
		
		Integer i = formatters.parseFromString("45", Integer.class, csLocale);
		Assert.assertNotNull("Result Integer is null", i);
		Assert.assertEquals(Integer.valueOf(45), i);
		
		Integer i2 = formatters.parseFromString("45.45", Integer.class, csLocale);
		Assert.assertNotNull("Result Integer is null", i2);
		Assert.assertEquals(Integer.valueOf(45), i2);
		
		Long l = formatters.parseFromString("45", Long.class, csLocale);
		Assert.assertNotNull("Result Long is null", l);
		Assert.assertEquals(Long.valueOf(45), l);
		
		Long l2 = formatters.parseFromString("45.45", Long.class, csLocale);
		Assert.assertNotNull("Result Long is null", l2);
		Assert.assertEquals(Long.valueOf(45), l2);
		
		MluvnickyPad e = formatters.parseFromString("GENITIV", MluvnickyPad.class, csLocale);
		Assert.assertNotNull("Result Enum is null", e);
		Assert.assertEquals(MluvnickyPad.GENITIV, e);
	}
	
	@Test
	public void testParseFromString2() {
		final Locale locale = new Locale("cs", "CZ");
		final BasicFormatters formatters = new BasicFormatters();
		Assert.assertEquals("true", formatters.makeString(Boolean.TRUE, locale));
		Assert.assertEquals("false", formatters.makeString(Boolean.FALSE, locale));
		Assert.assertEquals("1", formatters.makeString(Byte.valueOf((byte)1), locale));
		Assert.assertEquals("1", formatters.makeString(Short.valueOf((short)1), locale)); // NOPMD by Radek on 2.3.14 19:27
		Assert.assertEquals("1", formatters.makeString(Integer.valueOf(1), locale));
		Assert.assertEquals("1", formatters.makeString(Long.valueOf(1L), locale));
		Assert.assertEquals("hello", formatters.makeString("hello", locale));
		Assert.assertEquals("3", formatters.makeString(new BigInteger("3"), locale));
		Assert.assertEquals("2,1", formatters.makeString(Double.valueOf(2.1), locale));
		
		String dateStr = "12.03.2014 16:30";
		String dateFormat = "dd.MM.yyyy HH:mm";
		Date date = formatters.parseFromString(dateStr, Date.class, dateFormat, locale);
		Assert.assertEquals(dateStr, formatters.makeString(date, dateFormat, locale));
		
		String bdStr = "3,6";
		String bdFormat = "#.#";
		BigDecimal bd = formatters.parseFromString(bdStr, BigDecimal.class, bdFormat, locale);
		Assert.assertEquals(bdStr, formatters.makeString(bd, bdFormat, locale));
		
		Assert.assertEquals(MluvnickyPad.GENITIV.name(), formatters.makeString(MluvnickyPad.GENITIV, locale));
		Assert.assertEquals(null, formatters.makeString(null, locale));
	}
	
	@Test
	public void testFormattersToAndFromString() {
		try {
			final Locale locale = new Locale("cs", "CZ");
			final BasicFormatters formatters = new BasicFormatters();
			
			Date date = new SimpleDateFormat(FormatsCache.DEFAULT_DATE_FORMAT).parse("1.12.2014");
			Assert.assertEquals(date, formatters.parseFromString(formatters.makeString(date, locale), Date.class, locale));
			Assert.assertEquals(Boolean.TRUE, formatters.parseFromString(formatters.makeString(Boolean.TRUE, locale), Boolean.class, locale));
			Assert.assertEquals(Boolean.FALSE, formatters.parseFromString(formatters.makeString(Boolean.FALSE, locale), Boolean.class, locale));
			Assert.assertEquals(Byte.valueOf((byte)1), formatters.parseFromString(formatters.makeString(Byte.valueOf((byte)1), locale), Byte.class, locale));
			Assert.assertEquals(Short.valueOf((short)1), formatters.parseFromString(formatters.makeString(Short.valueOf((short)1), locale), Short.class, locale)); // NOPMD by Radek on 2.3.14 19:27
			Assert.assertEquals(Integer.valueOf(1), formatters.parseFromString(formatters.makeString(Integer.valueOf(1), locale), Integer.class, locale));
			Assert.assertEquals(Long.valueOf(1L), formatters.parseFromString(formatters.makeString(Long.valueOf(1L), locale), Long.class, locale));
			Assert.assertEquals(new BigInteger("3"), formatters.parseFromString(formatters.makeString(new BigInteger("3"), locale), BigInteger.class, locale));
			Assert.assertEquals("hello, dolly", formatters.parseFromString(formatters.makeString("hello, dolly", locale), String.class, locale));
			String dStr = "123456789.0123456789";
			String dToStr = formatters.makeString(Double.valueOf(dStr), locale);
			// System.out.println(dToStr);
			Assert.assertEquals(Double.valueOf(dStr), formatters.parseFromString(dToStr, Double.class, locale));
			String bdStr = "123456789123456789876543210.012345678987654321987654321";
			String bdToStr = formatters.makeString(new BigDecimal(bdStr), locale);
			// System.out.println(bdToStr);
			Assert.assertEquals(new BigDecimal(bdStr), formatters.parseFromString(bdToStr, BigDecimal.class, locale));
			Assert.assertEquals(MluvnickyPad.GENITIV, formatters.parseFromString(formatters.makeString(MluvnickyPad.GENITIV, locale), MluvnickyPad.class, locale));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
	@Test(expected=FormatterNotFoundException.class)
	public void testNonRegisteredParser() {
		final Locale locale = new Locale("cs", "CZ");
		final BasicFormatters formatters = new BasicFormatters();
		formatters.parseFromString("12.3.2014 16:30", Day.class, "dd.MM.yyyy HH:mm", locale);
	}
	
	@Test(expected=StringParseException.class)
	public void testIncorrectFormat() {
		final Locale locale = new Locale("cs", "CZ");
		final BasicFormatters formatters = new BasicFormatters();
		formatters.parseFromString("12.3.2014 16:30", Date.class, "jj.ee.yyyy HH:mm", locale);
	}
	
	@Test(expected=StringParseException.class)
	public void testTooSpecificFormat() {
		final Locale locale = new Locale("cs", "CZ");
		final BasicFormatters formatters = new BasicFormatters();
		// But hours and minutes are not present in the string
		Date date2 = formatters.parseFromString("12.3.2014", Date.class, "dd.MM.yyyy HH:mm", locale);
		Assert.assertNotNull("Result date is null", date2);
		Calendar cal2 = getCalendarForDate(date2);
		Assert.assertEquals(12, cal2.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(3 - 1, cal2.get(Calendar.MONTH));
		Assert.assertEquals(2014, cal2.get(Calendar.YEAR));
	}
	
	@Test(expected=StringParseException.class)
	public void testInvalidBigDecimal() {
		final Locale locale = new Locale("cs", "CZ");
		final BasicFormatters formatters = new BasicFormatters();
		formatters.parseFromString("aaa", BigDecimal.class, locale);
	}
	
	@Test
	public void testExtensibility() {
		final Locale locale = new Locale("cs", "CZ");
		final BasicFormatters stringParsers = new ExtendedFormatters();
		// Extended formatter supporting special Day class:
		Day date = stringParsers.parseFromString("12.3.2014", Day.class, "dd.MM.yyyy", locale);
		Assert.assertNotNull("Result date is null", date);
		Calendar cal = getCalendarForDate(date);
		Assert.assertEquals(12, cal.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(3 - 1, cal.get(Calendar.MONTH));
		Assert.assertEquals(2014, cal.get(Calendar.YEAR));
	}
	
	@Test
	public void testParsersKeyEquality() {
		Assert.assertEquals(BasicFormatters.class, BasicFormatters.class);
	}
	
	@Test
	public void testFormatterKeyEquality() {
		Assert.assertEquals(BasicFormatters.class, BasicFormatters.class);
	}
	
	private Calendar getCalendarForDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	
	static class ExtendedFormatters extends BasicFormatters {
		
		@Override
		protected Map<Class<?>, Formatter<?>> registerFormatters() {
			Map<Class<?>, Formatter<?>> formatters = new HashMap<Class<?>, Formatter<?>>();
			formatters.putAll(super.registerFormatters());
			formatters.put(Day.class, new Formatter<Day>() {
				
				@Override
				public Day parseFromString(String str, Class<Day> destClass, String formatPattern, Locale locale) {
					try {
						Date date = FormatsCache.getOrCreateDateFormat(formatPattern, locale).parse(str);
						return date != null ? Day.valueOf(date) : null;
					} catch (Exception ex) {
						throw new StringParseException(Day.class, str, ex);
					}
				}
				
				@Override
				public String makeString(Day value, String formatPattern, Locale locale) {
					return FormatsCache.getOrCreateDateFormat(
						formatPattern, locale).format(value);
				}
	
			});
			return Collections.unmodifiableMap(formatters);
		}
	}

}
