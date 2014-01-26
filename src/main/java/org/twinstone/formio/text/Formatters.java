package org.twinstone.formio.text;

import java.util.Locale;

/**
 * Object capable to convert value to a String
 * according to given localization parameters,
 * and parse the string back to value of given type.
 * 
 * @author Radek Beran
 */
public interface Formatters {

	/**
	 * Parse value from a String.
	 * @param cls class of resulting parsed value
	 * @param str string to parse
	 * @param formatPattern format of value in the string
	 * @param locale locale of value in the string
	 * @return
	 */
	<T> T parseFromString(Class<T> cls, String str, String formatPattern, Locale locale);
	
	/**
	 * Creates String from the given value.
	 * @param value value to convert
	 * @param formatPattern format of value in the string
	 * @param locale locale of value in the string
	 * @return
	 */
	<T> String makeString(T value, String formatPattern, Locale locale);
}
