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
package net.formio.servlet.ajax.action;

import javax.servlet.http.HttpServletRequest;

import net.formio.ajax.AjaxAction;
import net.formio.ajax.AjaxResponse;
import net.formio.servlet.ajax.FormStateHandler;

/**
 * Action that handles AJAX request and generates AJAX response in servlet environment.
 * Form state is loaded using a {@link FormStateHandler} and made available to this
 * action. After this action completes, form state is automatically stored.
 * @author Radek Beran
 * @param <T> type of form state object
 * @author Radek Beran
 */
public interface FormStateAjaxAction<T> extends AjaxAction {
	
	/**
	 * Generates AJAX response based on given AJAX request and given current form state
	 * that can be changed and that is automatically stored after this action completes.
	 * @param req
	 * @param formState
	 * @return
	 */
	AjaxResponse<T> apply(HttpServletRequest req, T formState);
}