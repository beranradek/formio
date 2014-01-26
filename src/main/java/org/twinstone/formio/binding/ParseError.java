package org.twinstone.formio.binding;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.twinstone.formio.validation.DefaultInterpolatedMessage;

/**
 * Error while parsing a string value and converting it to target type of property.
 * @author Radek Beran
 */
public final class ParseError extends DefaultInterpolatedMessage implements Serializable {
	private static final long serialVersionUID = -667660744330342475L;
	private final String propertyName;
	private final Class<?> targetTypeClass;
	private final String valueAsString;
	
	public ParseError(String propertyName, Class<?> targetTypeClass, String valueAsString) {
		if (propertyName == null) throw new IllegalArgumentException("propertyName cannot be null");
		this.propertyName = propertyName;
		this.targetTypeClass = targetTypeClass;
		this.valueAsString = valueAsString;
	}
	
	/**
	 * Name of property for which the value is parsed.
	 * @return
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Target type to which a string should be converted.
	 * @return
	 */
	public Class<?> getTargetTypeClass() {
		return targetTypeClass;
	}

	/**
	 * String that should be converted.
	 * @return
	 */
	public String getValueAsString() {
		return valueAsString;
	}
	
	/**
	 * Message parameters for translation file.
	 * @return
	 */
	@Override
	public Map<String, Object> getMessageParameters() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("valueAsString", getValueAsString());
		// TODO: Human representation of type name:
		params.put("targetType", getTargetTypeClass().getSimpleName());
		return params;
	}
}
