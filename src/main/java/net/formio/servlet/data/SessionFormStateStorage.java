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
package net.formio.servlet.data;

import java.io.Serializable;

import net.formio.RequestParams;
import net.formio.data.FormStateStorage;
import net.formio.servlet.ServletRequestParams;
import net.formio.servlet.common.SessionAttributeStorage;

/**
 * {@link FormStateStorage} that stores and loads form state from HTTP session attribute.
 * @author Radek Beran
 *
 * @param <T>
 */
public abstract class SessionFormStateStorage<T extends Serializable> implements FormStateStorage<T> {

	private final String sessionAttrName;
	private final SessionAttributeStorage<T> formStateStorage;
	
	public SessionFormStateStorage(String sessionAttrName) {
		this.sessionAttrName = sessionAttrName;
		this.formStateStorage = new SessionAttributeStorage<T>(sessionAttrName);
	}
	
	/**
	 * Creates new object for the form.
	 * @return
	 */
	public abstract T createNewFormState();
	
	@Override
	public T findFormState(RequestParams requestParams) {
		ServletRequestParams params = (ServletRequestParams)requestParams;
		T formState = formStateStorage.findData(params.getRequest().getSession());
		if (formState == null) {
			// Form state should be the same object as the one used to fill the form with data,
			// so the form state corresponds to filled data (e.g. state of checkbox that
			// influence visibility of other form part).
			// So there we will create new domain object for the form.
			formState = createNewFormState();
		}
		return formState;
	}

	@Override
	public void saveFormState(RequestParams requestParams, T formState) {
		ServletRequestParams params = (ServletRequestParams)requestParams;
		formStateStorage.storeData(params.getRequest().getSession(), formState); // save updated form state to session
	}

	@Override
	public void deleteFormState(RequestParams requestParams) {
		ServletRequestParams params = (ServletRequestParams)requestParams;
		formStateStorage.removeData(params.getRequest().getSession());
	}
	
	public String getSessionAttrName() {
		return sessionAttrName;
	}
	
}
