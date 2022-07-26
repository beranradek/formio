/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.validation.validators;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import net.formio.validation.Arg;
import net.formio.validation.DefaultInterpolatedMessage;
import net.formio.validation.InterpolatedMessage;
import net.formio.validation.Severity;
import net.formio.validation.Validator;

/**
 * Common parent for all validators.
 * @author Radek Beran
 *
 * @param <T>
 */
public abstract class AbstractValidator<T> implements Validator<T> {
	
	public static final String VALUE_ARG = "value";
	public static final String CURRENT_VALUE_ARG = "currentValue";
	
	public InterpolatedMessage error(String elementName, String messageKey, Arg ... args) {
		return message(elementName, Severity.ERROR, messageKey, null, args);
	}

	public InterpolatedMessage warning(String elementName, String messageKey, Arg ... args) {
		return message(elementName, Severity.WARNING, messageKey, null, args);
	}
	
	public InterpolatedMessage info(String elementName, String messageKey, Arg ... args) {
		return message(elementName, Severity.INFO, messageKey, null, args);
	}

	public InterpolatedMessage error(String elementName, String messageKey, String messageText, Arg ... args) {
		return message(elementName, Severity.ERROR, messageKey, messageText, args);
	}

	public InterpolatedMessage warning(String elementName, String messageKey, String messageText, Arg ... args) {
		return message(elementName, Severity.WARNING, messageKey, messageText, args);
	}

	public InterpolatedMessage info(String elementName, String messageKey, String messageText, Arg ... args) {
		return message(elementName, Severity.INFO, messageKey, messageText, args);
	}
	
	public InterpolatedMessage localizedError(String elementName, String messageText) {
		return message(elementName, Severity.ERROR, null, messageText);
	}
	
	public InterpolatedMessage localizedWarning(String elementName, String messageText) {
		return message(elementName, Severity.WARNING, null, messageText);
	}
	
	public InterpolatedMessage localizedInfo(String elementName, String messageText) {
		return message(elementName, Severity.INFO, null, messageText);
	}
	
	private InterpolatedMessage message(String elementName, Severity severity, String messageKey, String messageText, Arg... args) {
		Map<String, Serializable> argsMap = new LinkedHashMap<String, Serializable>();
		if (args != null) {
			for (Arg arg : args) {
				argsMap.put(arg.getName(), arg.getValue());
			}
		}
		return new DefaultInterpolatedMessage(elementName, severity, messageKey, argsMap, messageText);
	}
}
