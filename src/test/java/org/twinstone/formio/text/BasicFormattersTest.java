package org.twinstone.formio.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.twinstone.formio.text.BasicFormatters.FormattersKey;
import org.twinstone.formio.text.FormattersCache.FormatterKey;

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
	public void testParseFromString() {
		final BasicFormatters stringParsers = new BasicFormatters(new Locale("cs", "CZ"));
		// for caching debugging: final StringParsers stringParsers2 = new StringParsers(new Locale("cs", "CZ"));
		
		Date date = stringParsers.parseFromString(Date.class, "12.3.2014 16:30", "dd.MM.yyyy HH:mm");
		Assert.assertNotNull("Result date is null", date);
		Calendar cal = getCalendarForDate(date);
		Assert.assertEquals(12, cal.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(3 - 1, cal.get(Calendar.MONTH));
		Assert.assertEquals(2014, cal.get(Calendar.YEAR));
		Assert.assertEquals(16, cal.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(30, cal.get(Calendar.MINUTE));
		
		BigDecimal bd = stringParsers.parseFromString(BigDecimal.class, "3,6", "#.#");
		Assert.assertNotNull("Result decimal is null", bd);
		Assert.assertEquals(new BigDecimal("3.6"), bd);
		
		BigDecimal bd2 = stringParsers.parseFromString(BigDecimal.class, "3.6", "#.#");
		Assert.assertNotNull("Result decimal is null", bd2);
		Assert.assertEquals(new BigDecimal("3.6"), bd2);
		
		BigDecimal bd3 = stringParsers.parseFromString(BigDecimal.class, "3.6");
		Assert.assertNotNull("Result decimal is null", bd3);
		Assert.assertEquals(new BigDecimal("3.6"), bd3);
		
		Double d = stringParsers.parseFromString(Double.class, "2.1");
		Assert.assertNotNull("Result double is null", d);
		Assert.assertEquals(2.1, d.doubleValue(), 0.001);
		
		Double d2 = stringParsers.parseFromString(Double.class, "2,1");
		Assert.assertNotNull("Result double is null", d2);
		Assert.assertEquals(2.1, d2.doubleValue(), 0.001);
		
		BigInteger bi = stringParsers.parseFromString(BigInteger.class, "123");
		Assert.assertNotNull("Result big integer is null", bi);
		Assert.assertEquals(BigInteger.valueOf(123), bi);
		
		BigInteger bi2 = stringParsers.parseFromString(BigInteger.class, "123,3");
		Assert.assertNotNull("Result big integer is null", bi2);
		Assert.assertEquals(BigInteger.valueOf(123), bi2);
		
		Short s = stringParsers.parseFromString(Short.class, "6");
		Assert.assertNotNull("Result Short is null", s);
		Assert.assertEquals(Short.valueOf("6"), s);
		
		Short s2 = stringParsers.parseFromString(Short.class, "45.45");
		Assert.assertNotNull("Result Short is null", s2);
		Assert.assertEquals(Short.valueOf("45"), s2);
		
		Integer i = stringParsers.parseFromString(Integer.class, "45");
		Assert.assertNotNull("Result Integer is null", i);
		Assert.assertEquals(Integer.valueOf(45), i);
		
		Integer i2 = stringParsers.parseFromString(Integer.class, "45.45");
		Assert.assertNotNull("Result Integer is null", i2);
		Assert.assertEquals(Integer.valueOf(45), i2);
		
		Long l = stringParsers.parseFromString(Long.class, "45");
		Assert.assertNotNull("Result Long is null", l);
		Assert.assertEquals(Long.valueOf(45), l);
		
		Long l2 = stringParsers.parseFromString(Long.class, "45.45");
		Assert.assertNotNull("Result Long is null", l2);
		Assert.assertEquals(Long.valueOf(45), l2);
		
		MluvnickyPad e = stringParsers.parseFromString(MluvnickyPad.class, "GENITIV");
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
		Assert.assertEquals("1", formatters.makeString(Short.valueOf((short)1)));
		Assert.assertEquals("1", formatters.makeString(Integer.valueOf(1)));
		Assert.assertEquals("1", formatters.makeString(Long.valueOf(1L)));
		Assert.assertEquals("hello", formatters.makeString("hello"));
		Assert.assertEquals("3", formatters.makeString(new BigInteger("3")));
		Assert.assertEquals("2,1", formatters.makeString(Double.valueOf(2.1)));
		
		
		String dateStr = "12.03.2014 16:30";
		String dateFormat = "dd.MM.yyyy HH:mm";
		Date date = formatters.parseFromString(Date.class, dateStr, dateFormat);
		Assert.assertEquals(dateStr, formatters.makeString(date, dateFormat));
		
		String bdStr = "3,6";
		String bdFormat = "#.#";
		BigDecimal bd = formatters.parseFromString(BigDecimal.class, bdStr, bdFormat);
		Assert.assertEquals(bdStr, formatters.makeString(bd, bdFormat));
		
		Assert.assertEquals(MluvnickyPad.GENITIV.name(), formatters.makeString(MluvnickyPad.GENITIV));
		Assert.assertEquals(null, formatters.makeString(null));
	}
	
	@Test
	public void testBasicStringMakersDualToBasicStringParsersForLocale() {
		final Locale locale = new Locale("cs", "CZ");
		final BasicFormatters formatters = new BasicFormatters(locale);
		
		Date currentDate = new Date();
		Assert.assertEquals(currentDate, formatters.parseFromString(Date.class, formatters.makeString(currentDate)));
		Assert.assertEquals(Boolean.TRUE, formatters.parseFromString(Boolean.class, formatters.makeString(Boolean.TRUE)));
		Assert.assertEquals(Boolean.FALSE, formatters.parseFromString(Boolean.class, formatters.makeString(Boolean.FALSE)));
		Assert.assertEquals(Byte.valueOf((byte)1), formatters.parseFromString(Byte.class, formatters.makeString(Byte.valueOf((byte)1))));
		Assert.assertEquals(Short.valueOf((short)1), formatters.parseFromString(Short.class, formatters.makeString(Short.valueOf((short)1))));
		Assert.assertEquals(Integer.valueOf(1), formatters.parseFromString(Integer.class, formatters.makeString(Integer.valueOf(1))));
		Assert.assertEquals(Long.valueOf(1L), formatters.parseFromString(Long.class, formatters.makeString(Long.valueOf(1L))));
		Assert.assertEquals(new BigInteger("3"), formatters.parseFromString(BigInteger.class, formatters.makeString(new BigInteger("3"))));
		Assert.assertEquals("hello, dolly", formatters.parseFromString(String.class, formatters.makeString("hello, dolly")));
		String dStr = "123456789.0123456789";
		String dToStr = formatters.makeString(Double.valueOf(dStr));
		// System.out.println(dToStr);
		Assert.assertEquals(Double.valueOf(dStr), formatters.parseFromString(Double.class, dToStr));
		String bdStr = "123456789123456789876543210.012345678987654321987654321";
		String bdToStr = formatters.makeString(new BigDecimal(bdStr));
		// System.out.println(bdToStr);
		Assert.assertEquals(new BigDecimal(bdStr), formatters.parseFromString(BigDecimal.class, bdToStr));
		Assert.assertEquals(MluvnickyPad.GENITIV, formatters.parseFromString(MluvnickyPad.class, formatters.makeString(MluvnickyPad.GENITIV)));
	}
	
	@Test(expected=FormatterNotFoundException.class)
	public void testNonRegisteredParser() {
		final BasicFormatters stringParsers = new BasicFormatters(new Locale("cs", "CZ"));
		
		stringParsers.parseFromString(Day.class, "12.3.2014 16:30", "dd.MM.yyyy HH:mm");
	}
	
	@Test(expected=StringParseException.class)
	public void testIncorrectFormat() {
		final BasicFormatters stringParsers = new BasicFormatters(new Locale("cs", "CZ"));
		
		stringParsers.parseFromString(Date.class, "12.3.2014 16:30", "jj.ee.yyyy HH:mm");
	}
	
	@Test(expected=StringParseException.class)
	public void testTooSpecificFormat() {
		final BasicFormatters stringParsers = new BasicFormatters(new Locale("cs", "CZ"));
		// But hours and minutes are not present in the string
		Date date2 = stringParsers.parseFromString(Date.class, "12.3.2014", "dd.MM.yyyy HH:mm");
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
		Day date = stringParsers.parseFromString(Day.class, "12.3.2014", "dd.MM.yyyy");
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
		
		public ExtendedFormatters() {
			super();
		}
		
		@Override
		protected Map<Class<?>, Formatter<?>> registerFormatters(Locale locale) {
			Map<Class<?>, Formatter<?>> formatters = new HashMap<Class<?>, BasicFormatters.Formatter<?>>();
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
