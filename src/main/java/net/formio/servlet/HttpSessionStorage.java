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

import jakarta.servlet.http.HttpSession;

import net.formio.data.SessionStorage;

/**
 * Implementation of {@link SessionStorage} using HTTP session.
 * @author Radek Beran
 */
public class HttpSessionStorage implements SessionStorage {
	
	private final HttpSession session;
	
	public HttpSessionStorage(HttpSession session) {
		if (session == null) throw new IllegalArgumentException("session cannot be null");
		this.session = session;
	}

	@Override
	public String set(String key, String value) {
		session.setAttribute(key, value);
		return value;
	}

	@Override
	public String get(String key) {
		return (String)session.getAttribute(key);
	}

	@Override
	public boolean delete(String key) {
		String value = get(key);
		session.removeAttribute(key);
		return value != null;
	}

}
