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
	private final List<InterpolatedMessage> messages;
	
	public Validated(ValidationContext<T> ctx, boolean valid, String messageKey, Map<String, Serializable> messageParameters) {
		if (!valid && (messageKey == null || messageKey.isEmpty())) {
			throw new IllegalArgumentException("Message key must be specified when the value is not valid.");
		}
		InterpolatedMessage msg = null;
		if (!valid) {
			msg = new DefaultInterpolatedMessage(ctx.getElementName(), Severity.ERROR, messageKey, messageParameters);
		}
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (msg != null) {
			msgs.add(msg);
		}
		this.messages = msgs;
		this.validatedValue = ctx.getValidatedValue();
	}
	
	public Validated(ValidationContext<T> ctx, boolean valid, String messageKey) {
		this(ctx, valid, messageKey, new LinkedHashMap<String, Serializable>());
	}
	
	public Validated(ValidationContext<T> ctx, List<InterpolatedMessage> messages) {
		this.validatedValue = ctx.getValidatedValue();
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (messages != null) {
			for (InterpolatedMessage m : messages) {
				if (m != null) {
					msgs.add(m);
				}
			}
		}
		this.messages = msgs;
	}
	
	public T getValidatedValue() {
		return validatedValue;
	}

	/**
	 * Returns validation messages from the validation.
	 * @return
	 */
	public List<InterpolatedMessage> getMessages() {
		return messages;
	}
}
