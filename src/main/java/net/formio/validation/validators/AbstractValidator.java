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
	
	public InterpolatedMessage error(String elementName, String messageKey, Arg ... args) {
		return message(elementName, Severity.ERROR, messageKey, args);
	}
	
	public InterpolatedMessage warning(String elementName, String messageKey, Arg ... args) {
		return message(elementName, Severity.WARNING, messageKey, args);
	}
	
	public InterpolatedMessage info(String elementName, String messageKey, Arg ... args) {
		return message(elementName, Severity.INFO, messageKey, args);
	}

	private InterpolatedMessage message(String elementName, Severity severity, String messageKey, Arg... args) {
		Map<String, Serializable> argsMap = new LinkedHashMap<String, Serializable>();
		if (args != null) {
			for (Arg arg : args) {
				argsMap.put(arg.getName(), arg.getValue());
			}
		}
		return new DefaultInterpolatedMessage(elementName, severity, messageKey, argsMap);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getClass().getName().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Validator))
			return false;
		AbstractValidator<?> other = (AbstractValidator<?>) obj;
		if (!getClass().getName().equals(other.getClass().getName()))
			return false;
		return true;
	}
}
