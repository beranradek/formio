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

/**
 * Maximum size of one uploaded file was exceeded.
 * @author Radek Beran
 */
public class MaxFileSizeExceededError extends MaxSizeExceededError {
	private static final long serialVersionUID = -8869521443566237030L;
	private final String fieldName;

	public MaxFileSizeExceededError(String message, Throwable cause, long currentSize, long maxSize, String fieldName) {
		super(message, cause, currentSize, maxSize);
		this.fieldName = fieldName;
	}

	/**
	 * Name of form field that can be used as the key for validation messages.
	 * @return
	 */
	public String getFieldName() {
		return fieldName;
	}

}
