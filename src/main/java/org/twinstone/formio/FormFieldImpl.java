package org.twinstone.formio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.twinstone.formio.text.Formatters;

/**
 * Form field. Immutable.
 * @author Radek Beran
 */
class FormFieldImpl implements FormField, Serializable {
	private static final long serialVersionUID = 1893796732140721632L;
	
	private final String name;
	private final List<Object> values;
	private final String pattern;
	private final String strValue;
	
	// TODO: Configurable custom formatter for the field. With fallback to form formatters
	
	static FormFieldImpl getInstance(String name, String pattern) {
		return new FormFieldImpl(name, pattern, Collections.emptyList(), null);
	}
	
	static FormFieldImpl getFilledInstance(String name, String pattern, List<Object> values, Locale locale, Formatters formatters) {
		String strValue = null;
		if (values.size() > 0) {
			strValue = valueAsString(values.get(0), pattern, locale, formatters);
		}
		return new FormFieldImpl(name, pattern, values, strValue);
	}
	
	private FormFieldImpl(String name, String pattern, List<Object> values, String strValue) {
		if (values == null) throw new IllegalArgumentException("values cannot be null, only empty");
		this.name = name;
		this.pattern = pattern;
		this.values = values;
		this.strValue = strValue;
	}

	/**
	 * Returns copy of given field with new name that has given prefix prepended.
	 * @param namePrefix
	 * @return
	 */
	FormFieldImpl(FormField src, String namePrefix) {
		this.name = namePrefix + Forms.PATH_SEP + src.getName();
		this.values = new ArrayList<Object>(src.getValues());
		this.pattern = src.getPattern();
		this.strValue = src.getValue();
	}

	/**
	 * Name of edited property prefixed with form name (and following dot).
	 */
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public List<Object> getValues() {
		return values;
	}

	@Override
	public String getValue() {
		return strValue;
	}

	@Override
	public String getPattern() {
		return pattern;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FormFieldImpl))
			return false;
		FormFieldImpl other = (FormFieldImpl) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "[ name = " + name + ", pattern = " + pattern + ", values = " + values + "]"; 
	}
	
	private static String valueAsString(Object value, String pattern, Locale locale, Formatters formatters) {
		if (value == null) return null;
		return formatters.makeString(value, pattern, locale);
	}

}
