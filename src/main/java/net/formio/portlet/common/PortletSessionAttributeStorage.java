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
package net.formio.portlet.common;

import java.io.Serializable;

import javax.portlet.PortletSession;

/**
 * Immutable typesafe facade for storage of Serializable object in portlet
 * session.
 * 
 * @author Radek Beran
 */
public class PortletSessionAttributeStorage<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = 4724366383019732139L;
	private final String sessionAttribute;
	private final int sessionScope;

	public PortletSessionAttributeStorage(String sessionAttribute, int scope) {
		this.sessionAttribute = sessionAttribute;
		this.sessionScope = scope;
	}
	
	public PortletSessionAttributeStorage(String sessionAttribute) {
		this(sessionAttribute, PortletSession.PORTLET_SCOPE);
	}

	public T findData(final PortletSession session) {
		return (T)session.getAttribute(sessionAttribute, sessionScope);
	}

	public boolean isStored(final PortletSession session) {
		return findData(session) != null;
	}

	public void storeData(final PortletSession session, T data) {
		session.setAttribute(sessionAttribute, data, sessionScope);
	}

	public void removeData(final PortletSession session) {
		session.removeAttribute(sessionAttribute, sessionScope);
	}
}
