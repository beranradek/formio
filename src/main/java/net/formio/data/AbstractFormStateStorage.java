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
package net.formio.data;

import net.formio.RequestParams;
import net.formio.ajax.AjaxResponse;
import net.formio.ajax.action.FormStateAction;

/**
 * Implementation of {@link FormStateStorage} with basic implemented state manipulation methods.
 * @author Radek Beran
 *
 * @param <T>
 */
public abstract class AbstractFormStateStorage<T> implements FormStateStorage<T> {

	/**
	 * Loads current form state from storage and applies given action to it. The action returns updated form state.
	 * Updated form state is saved back to the storage.  
	 * @param requestParams
	 * @param action
	 * @return
	 */
	public AjaxResponse<T> withUpdatedState(final RequestParams requestParams, final FormStateAction<T> action) {
		T currentState = findFormState(requestParams);
		AjaxResponse<T> response = action.apply(currentState);
		saveFormState(requestParams, response.getUpdatedFormState());
		return response;
	}
}
