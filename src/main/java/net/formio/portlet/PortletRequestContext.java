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
package net.formio.portlet;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import net.formio.data.RequestContext;
import net.formio.data.UserRelatedStorage;

/**
 * Implementation of {@link RequestContext} for portlet API.
 * @author Radek Beran
 */
public class PortletRequestContext implements RequestContext {
	public static final String SEPARATOR = "_";
	private final PortletRequest request;
	private final int sessionScope;
	
	public PortletRequestContext(PortletRequest request, int sessionScope) {
		if (request == null) throw new IllegalArgumentException("request cannot be null");
		this.request = request;
		this.sessionScope = sessionScope;
	}
	
	public PortletRequestContext(PortletRequest request) {
		this(request, PortletSession.PORTLET_SCOPE);
	}

	@Override
	public UserRelatedStorage getUserRelatedStorage() {
		return new PortletSessionUserRelatedStorage(this.request.getPortletSession(), this.sessionScope);
	}
	
	@Override
	public String getRequestSecret(String generatedSecret) {
		return generatedSecret + SEPARATOR + getUserRequestIdentification();
	}
	
	protected String getUserRequestIdentification() {
		return "" + request.getWindowID() + request.getRequestedSessionId();
	}
}
