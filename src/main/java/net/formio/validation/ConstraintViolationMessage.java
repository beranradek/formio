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
/*
 * Created on 16.2.2011
 *
 * Copyright (c) 2011 Et netera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package net.formio.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolation;

/**
 * Message carrying result from bean validation. Immutable.
 * 
 * @author Radek Beran
 */
public class ConstraintViolationMessage implements Serializable {
	private static final long serialVersionUID = 7184782770795690364L;
	private final Severity severity;
	private final String text;
	private final String msgKey;
	private final Map<String, Serializable> msgArgs;
	
	public ConstraintViolationMessage(ConstraintViolation<?> cv) {
		this(Severity.fromViolation(cv), 
		ValidationUtils.getMsgText(cv), 
		cv.getMessageTemplate(),
		toSortedSerializableArgs(cv.getConstraintDescriptor().getAttributes()));
	}

	public ConstraintViolationMessage(Severity severity, String text, String msgKey, Map<String, Serializable> msgArgs) {
		this.severity = severity;
		this.text = text;
		this.msgKey = ValidationUtils.removeBraces(msgKey);
		Map<String, Serializable> args = new LinkedHashMap<String, Serializable>();
		if (msgArgs != null) {
			args.putAll(toSortedSerializableArgs(msgArgs));
		}
		this.msgArgs = args; 
	}

	public Severity getSeverity() {
		return severity;
	}

	public String getText() {
		return text;
	}

	public String getMsgKey() {
		return msgKey;
	}

	public Map<String, Serializable> getMsgArgs() {
		return msgArgs;
	}

	public ConstraintViolationMessage copy() {
		return new ConstraintViolationMessage(getSeverity(), getText(), getMsgKey(), getMsgArgs());
	}
	
	@Override
	public String toString() {
		String resultTxt = null;
		String txt = getText();
		if (txt != null && !txt.isEmpty()) {
			resultTxt = txt;
		} else {
			resultTxt = getMsgKey();
		}
		return severity + ": " + resultTxt;
	}
	
	static Map<String, Serializable> toSortedSerializableArgs(
		Map<String, ? extends Object> attributes) {
		Map<String, Serializable> args = new LinkedHashMap<String, Serializable>();
		if (attributes != null) {
			// Ensuring deterministic order: Implementation of bean validation API (like Hibernate validator)
			// can return attributes as HashMap.
			final List<String> attKeys = new ArrayList<String>(attributes.keySet());
			Collections.sort(attKeys);
			for (String attKey : attKeys) {
				Object value = attributes.get(attKey);
				if (value != null && !(value instanceof Serializable)) {
					throw new IllegalArgumentException("Message argument value for key '" + attKey + 
						"' is not serializable so the " + ConstraintViolationMessage.class.getSimpleName() + 
						" cannot be serializable.");
				}
				args.put(attKey, (Serializable)value);
			}
		}
		return args;
	}
}
