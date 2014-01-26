package org.twinstone.formio.binding;

import java.util.Locale;

/**
 * Information about value that should be bound to an object instance.
 * @author Radek Beran
 */
public final class BoundValuesInfo {
	private final Object[] values;
	private final String pattern;
	private final Locale locale;

	public static BoundValuesInfo getInstance(Object[] values, String pattern, Locale locale) {
		return new BoundValuesInfo(values, pattern, locale);
	}
	
	public static BoundValuesInfo getInstance(Object[] values, String pattern) {
		return getInstance(values, pattern, Locale.getDefault());
	}
	
	public static BoundValuesInfo getInstance(Object[] values) {
		return getInstance(values, null, Locale.getDefault());
	}
	
	private BoundValuesInfo(Object[] values, String pattern, Locale locale) {
		this.values = values;
		this.pattern = pattern;
		this.locale = locale;
	}

	public Object[] getValues() {
		return values != null ? values.clone() : null;
	}

	/**
	 * Pattern for parsing a String value to target value. 
	 * @return
	 */
	public String getPattern() {
		return pattern;
	}

	public Locale getLocale() {
		return locale;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (values != null) {
			for (Object v : values) {
				sb.append(v + "; ");
			}
		}
		return sb.toString();
	}

}
