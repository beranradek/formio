/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio;

import java.util.LinkedHashMap;
import java.util.Map;

import net.formio.upload.RequestProcessingError;
import net.formio.upload.UploadedFile;

/**
 * Provides "request parameters" from specified mapping. Useful for testing or when all parameters
 * are available simply as a map.
 * @author Radek Beran
 */
public class MapParams extends AbstractRequestParams {
	
	private final Map<String, String[]> params;
	private final Map<String, UploadedFile[]> files;
	
	public MapParams() {
		this(new LinkedHashMap<String, String[]>(), new LinkedHashMap<String, UploadedFile[]>());
	}
	
	public MapParams(Map<String, String[]> params) {
		this(params, new LinkedHashMap<String, UploadedFile[]>());
	}
	
	public MapParams(Map<String, String[]> params, Map<String, UploadedFile[]> fileParams) {
		final Map<String, String[]> p = new LinkedHashMap<String, String[]>();
		p.putAll(params);
		this.params = p;
		final Map<String, UploadedFile[]> files = new LinkedHashMap<String, UploadedFile[]>();
		files.putAll(fileParams);
		this.files = files;
	}
	
	public void put(String key, String value) {
		String[] values = this.params.get(key);
		if (values != null) {
			int prevLength = values.length;
			values = new String[prevLength + 1];
			values[prevLength] = value;
		} else {
			values = new String[] { value };
		}
		this.params.put(key, values);
	}
	
	public void put(String key, String[] values) {
		this.params.put(key, values);
	}
	
	public void put(String key, UploadedFile value) {
		UploadedFile[] values = this.files.get(key);
		if (values != null) {
			int prevLength = values.length;
			values = new UploadedFile[prevLength + 1];
			values[prevLength] = value;
		} else {
			values = new UploadedFile[] { value };
		}
		this.files.put(key, values);
	}
	
	public void put(String key, UploadedFile[] values) {
		this.files.put(key, values);
	}
	
	public boolean containsKey(String key) {
		return this.params.containsKey(key) || this.files.containsKey(key); 
	}

	@Override
	public Iterable<String> getParamNames() {
		return this.params.keySet();
	}

	@Override
	public String[] getParamValues(String paramName) {
		return this.params.get(paramName);
	}

	@Override
	public UploadedFile[] getUploadedFiles(String paramName) {
		return this.files.get(paramName);
	}

	@Override
	public RequestProcessingError getRequestError() {
		return null;
	}

	public int size() {
		return this.params.size() + this.files.size();
	}

	public boolean isEmpty() {
		return this.params.isEmpty() && this.files.isEmpty();
	}

	public void clear() {
		this.params.clear();
		this.files.clear();
	}

	@Override
	public boolean isTdiAjaxRequest() {
		return false;
	}

	@Override
	public String getTdiAjaxSrcElementName() {
		return null;
	}
}
