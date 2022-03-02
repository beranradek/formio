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
package net.formio.validation;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Basic {@link InterpolatedMessage} that has no parameters.
 * @author Radek Beran
 */
public class DefaultInterpolatedMessage implements InterpolatedMessage, Serializable {
	private static final long serialVersionUID = 4956507046430112207L;
	
	private final String elementName;
	private final Severity severity;
	private final String messageKey;
	private final String messageText;
	private final Map<String, Serializable> messageParameters;
	
	public DefaultInterpolatedMessage(String elementName, Severity severity) {
		this(elementName, 
			severity,
			null);
	}
	
	public DefaultInterpolatedMessage(String elementName, Severity severity, String messageKey) {
		this(elementName, 
			severity,
			messageKey,
			new LinkedHashMap<String, Serializable>());
	}

	public DefaultInterpolatedMessage(String elementName, Severity severity, String messageKey, Map<String, Serializable> messageParameters) {
		this(elementName, severity, messageKey, messageParameters, null);
	}
	
	public DefaultInterpolatedMessage(String elementName, Severity severity, String messageKey, Map<String, Serializable> messageParameters, String messageText) {
		this.elementName = elementName;
		if (severity == null) {
			throw new IllegalArgumentException("severity cannot be null");
		}
		this.severity = severity;
		this.messageKey = messageKey;
		if (messageParameters == null) {
			throw new IllegalArgumentException("Message parameters cannot be null, only empty");
		}
		this.messageParameters = messageParameters;
		this.messageText = messageText;
	}
	
	@Override
	public String getElementName() {
		return this.elementName;
	}
	
	@Override
	public Severity getSeverity() {
		return this.severity;
	}

	/**
	 * Message key for translation file.
	 * @return
	 */
	@Override
	public String getMessageKey() {
		if (messageKey == null) {
			// interpolated message key must be enclosed in braces otherwise it will not be translated
			return "{" + getClass().getSimpleName() + ".message}";
		}
		return messageKey;
	}

	/**
	 * Message text for cases when the message comes already translated in human-readable format.
	 * @return
	 */
	@Override
	public String getMessageText() {
		return messageText;
	}

	/**
	 * Message parameters for translation file.
	 * @return
	 */
	@Override
	public Map<String, Serializable> getMessageParameters() {
		return messageParameters;
	}
}
