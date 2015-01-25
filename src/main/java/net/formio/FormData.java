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
package net.formio;

import java.io.Serializable;

import net.formio.validation.ValidationResult;

/**
 * Edited data and validation result for this data (state of form editing).
 * @author Radek Beran
 *
 * @param <T>
 */
public class FormData<T> implements Serializable {
	private static final long serialVersionUID = 4044625858341878280L;
	
	private final T data; // T is not required to be Serializable for every use cases
	private final ValidationResult validationResult;
	
	public FormData(final T data, final ValidationResult validationResult) {
		this.data = data;
		ValidationResult res = validationResult;
		if (res == null) {
			res = ValidationResult.empty;
		}
		if (res == null) throw new IllegalArgumentException("validation result cannot be null");
		this.validationResult = res;
	}
	
	public FormData(final T data) {
		this(data, ValidationResult.empty);
	}

	public T getData() {
		return this.data;
	}

	public ValidationResult getValidationResult() {
		return this.validationResult;
	}
	
	/**
	 * Validation was successful, without validation errors.
	 * @return
	 */
	public boolean isValid() {
		return this.validationResult != null && this.validationResult.isSuccess();
	}
}
