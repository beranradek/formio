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
package net.formio.ajax.action;

import net.formio.ajax.AjaxResponse;
import net.formio.ajax.TdiAjaxRequestParams;
import net.formio.data.FormStateHandler;

/**
 * Action that handles AJAX request and generates AJAX response.
 * Form state is loaded and made available to this
 * action. After this action completes, form state is automatically stored.
 * @author Radek Beran
 * @param <T> type of form state object
 * @author Radek Beran
 */
public abstract class FormStateAjaxAction<T> implements AjaxAction<T> {
	
	private final FormStateHandler<T> formStateHandler;
	
	public FormStateAjaxAction(FormStateHandler<T> formStateHandler) {
		if (formStateHandler == null) {
			throw new IllegalArgumentException("formStateHandler cannot be null");
		}
		this.formStateHandler = formStateHandler;
	}
	
	/**
	 * Generates AJAX response based on given AJAX request. Form state is automatically 
	 * stored after this action completes.
	 * @param requestParams
	 * @return
	 */
	@Override
	public AjaxResponse<T> apply(TdiAjaxRequestParams requestParams) {
		T formState = formStateHandler.findFormState(requestParams);
		AjaxResponse<T> res = applyToState(requestParams, formState);
		formStateHandler.saveFormState(requestParams, res.getUpdatedFormState());
		return res;
	}
	
	/**
	 * Generates AJAX response based on given AJAX request and given current form state.
	 * Form state from the result is automatically stored after this action completes.
	 * @param requestParams
	 * @param formState
	 * @return
	 */
	public abstract AjaxResponse<T> applyToState(TdiAjaxRequestParams requestParams, T formState);
}
