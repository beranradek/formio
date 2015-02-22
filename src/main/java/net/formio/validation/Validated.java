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
package net.formio.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Validated value with validation result. Immutable.
 * @author Radek Beran
 *
 * @param <T>
 */
public class Validated<T> {
	private final T validatedValue;
	private final boolean valid;
	private final List<InterpolatedMessage> messages;
	
	public Validated(ValidationContext<T> ctx, boolean valid, String messageKey, Severity severity, Map<String, Serializable> messageParameters) {
		InterpolatedMessage msg = null;
		if (messageKey != null && !messageKey.isEmpty() && (severity != Severity.ERROR || !valid)) {
			msg = new DefaultInterpolatedMessage(ctx.getPropertyName(), severity, messageKey, messageParameters);
			if (Severity.ERROR == severity && valid) {
				throw new IllegalArgumentException("Result of validation cannot be valid when the message has error severity.");
			}
		}
		if (!valid && (messageKey == null || messageKey.isEmpty())) {
			throw new IllegalArgumentException("Message key must be specified when the value is not valid.");
		}
		this.validatedValue = ctx.getValidatedValue();
		this.valid = valid;
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (msg != null) {
			msgs.add(msg);
		}
		this.messages = msgs;
	}
	
	public Validated(ValidationContext<T> ctx, boolean valid, String messageKey, Severity severity) {
		this(ctx, valid, messageKey, severity, new LinkedHashMap<String, Serializable>());
	}
	
	public Validated(ValidationContext<T> ctx, boolean valid, String messageKey) {
		this(ctx, valid, messageKey, valid ? Severity.INFO : Severity.ERROR);
	}
	
	public Validated(ValidationContext<T> ctx, boolean valid) {
		this(ctx, valid, (String)null);
	}
	
	public Validated(ValidationContext<T> ctx, boolean valid, List<InterpolatedMessage> messages) {
		this.validatedValue = ctx.getValidatedValue();
		this.valid = valid;
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (messages != null) {
			for (InterpolatedMessage m : messages) {
				if (Severity.ERROR == m.getSeverity() && valid) {
					throw new IllegalArgumentException("Result of validation cannot be valid when the message has error severity.");
				}
				if (m.getSeverity() != Severity.ERROR || !valid) {
					msgs.add(m);
				}
			}
		}
		this.messages = msgs;
	}
	
	public T getValidatedValue() {
		return validatedValue;
	}
	
	public boolean isValid() {
		return valid;
	}

	/**
	 * Returns validation messages from the validation.
	 * @return
	 */
	public List<InterpolatedMessage> getMessages() {
		return messages;
	}
}
