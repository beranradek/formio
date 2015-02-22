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
package net.formio.binding;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import net.formio.validation.DefaultInterpolatedMessage;
import net.formio.validation.Severity;

/**
 * Error while parsing a string value and converting it to target type of
 * property.
 * 
 * @author Radek Beran
 */
public final class ParseError extends DefaultInterpolatedMessage {
	public static final String MSG_ARG_TARGET_TYPE = "targetType";
	public static final String MSG_ARG_VALUE_AS_STRING = "valueAsString";
	private static final long serialVersionUID = -667660744330342475L;
	private final Class<?> targetTypeClass;
	private final String valueAsString;
	private final HumanReadableType humanReadableTargetType;

	public ParseError(String propertyName, Class<?> targetTypeClass, String valueAsString) {
		super(propertyName, Severity.ERROR);
		this.targetTypeClass = targetTypeClass;
		this.valueAsString = valueAsString;
		this.humanReadableTargetType = humanReadableTypeFromClass(targetTypeClass);
	}

	/**
	 * Target type to which a string should be converted.
	 * 
	 * @return
	 */
	public Class<?> getTargetTypeClass() {
		return targetTypeClass;
	}

	/**
	 * String that should be converted (original value entered by user).
	 * 
	 * @return
	 */
	public String getValueAsString() {
		return valueAsString;
	}

	/**
	 * Message parameters for translation file.
	 * 
	 * @return
	 */
	@Override
	public Map<String, Serializable> getMessageParameters() {
		Map<String, Serializable> params = new LinkedHashMap<String, Serializable>();
		params.put(MSG_ARG_VALUE_AS_STRING, getValueAsString());
		params.put(MSG_ARG_TARGET_TYPE, getTargetTypeClass().getSimpleName());
		return params;
	}

	public HumanReadableType getHumanReadableTargetType() {
		return humanReadableTargetType;
	}
	
	/**
	 * Returns human readable categorization of Java type.
	 * @param cls
	 * @return
	 */
	protected HumanReadableType humanReadableTypeFromClass(Class<?> cls) {
		if (cls == null) return HumanReadableType.OBJECT;
		HumanReadableType hrt = null;
		if (PrimitiveType.byPrimitiveClass(cls) != null) {
			hrt = PrimitiveType.byPrimitiveClass(cls).getHumanReadableType();
		} else if (PrimitiveType.byWrapperClass(cls) != null) {
			hrt = PrimitiveType.byWrapperClass(cls).getHumanReadableType();
		} else if (cls.equals(String.class)) {
			hrt = HumanReadableType.TEXT;
		} else if (BigInteger.class.isAssignableFrom(cls)) {
			hrt = HumanReadableType.NUMBER;
		} else if (BigDecimal.class.isAssignableFrom(cls)) {
			hrt = HumanReadableType.DECIMAL_NUMBER;
		} else if (Date.class.isAssignableFrom(cls)) {
			hrt = HumanReadableType.DATE;
		} else {
			hrt = HumanReadableType.OBJECT;
		}
		return hrt;
	}

}
