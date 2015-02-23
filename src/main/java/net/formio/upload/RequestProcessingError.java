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
package net.formio.upload;

import net.formio.validation.DefaultInterpolatedMessage;
import net.formio.validation.Severity;

/**
 * Failure while processing the request.
 * 
 * @author Radek Beran
 */
public class RequestProcessingError extends DefaultInterpolatedMessage {
	private static final long serialVersionUID = -4738039542326084798L;

	private final String causeMessage;
	private final Throwable cause;

	public RequestProcessingError(String elementName, String causeMessage, Throwable cause) {
		super(elementName, Severity.ERROR);
		this.causeMessage = causeMessage;
		this.cause = cause;
	}

	public RequestProcessingError(String elementName, String message) {
		this(elementName, message, null);
	}

	/**
	 * Original error message.
	 * @return
	 */
	public String getCauseMessage() {
		return causeMessage;
	}

	/**
	 * Original cause.
	 * @return
	 */
	public Throwable getCause() {
		return cause;
	}

}
