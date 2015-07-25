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
package net.formio.inmemory;

import java.util.HashMap;
import java.util.Map;

import net.formio.data.UserSessionStorage;

/**
 * Implementation of {@link UserSessionStorage} using a {@link Map}.
 * @author Radek Beran
 */
public class MapUserRelatedStorage implements UserSessionStorage {

	private final Map<String, String> map;
	
	public MapUserRelatedStorage() {
		this.map = new HashMap<String, String>();
	}
	
	@Override
	public String set(String key, String value) {
		map.put(key, value);
		return value;
	}

	@Override
	public String get(String key) {
		return map.get(key);
	}

	@Override
	public boolean delete(String key) {
		return map.remove(key) != null;
	}
	
}