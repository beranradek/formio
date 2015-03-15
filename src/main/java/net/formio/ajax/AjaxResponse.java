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
package net.formio.ajax;

import net.formio.render.TdiResponseBuilder;

/**
 * Data for generating AJAX response including the resulting form state.
 * @author Radek Beran
 *
 * @param <T> type of form state object
 */
public class AjaxResponse<T> {
	private final TdiResponseBuilder responseBuilder;
	private final T updatedFormState;
	
	public AjaxResponse(TdiResponseBuilder responseBuilder, T updatedFormState) {
		this.responseBuilder = responseBuilder;
		this.updatedFormState = updatedFormState;
	}

	/**
	 * AJAX response builder that contains instructions to generate AJAX response.
	 * @return
	 */
	public TdiResponseBuilder getResponseBuilder() {
		return responseBuilder;
	}

	/**
	 * Resulting form state after processing the AJAX request.
	 * @return
	 */
	public T getUpdatedFormState() {
		return updatedFormState;
	}
}
