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
package net.formio.servlet;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

/**
 * Immutable typesafe facade for storage of Serializable object in HTTP session.
 * @author Radek Beran
 */
public class SessionStorage<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = 8144508294489706687L;
	private final String sessionAttribute;
	
	public SessionStorage(String sessionAttribute) {
		this.sessionAttribute = sessionAttribute;
	}
	
	public T findData(final HttpSession session) {
		return (T)session.getAttribute(sessionAttribute);
	}
	
	public boolean isStored(final HttpSession session) {
		return findData(session) != null;
	}
	
	public void storeData(final HttpSession session, T data) {
		session.setAttribute(sessionAttribute, data);
	}
	
	public void removeData(final HttpSession session) {
		session.removeAttribute(sessionAttribute);
	}
}
