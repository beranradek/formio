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
package net.formio.servlet;

import java.io.File;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import net.formio.ParamsProvider;
import net.formio.data.RequestContext;
import net.formio.upload.FileUploadWrapper;
import net.formio.upload.RequestProcessingError;
import net.formio.upload.UploadedFile;

/**
 * Implementation of {@link ParamsProvider} for servlet request that
 * uses commons-fileupload library for uploading files. If this implementation
 * of {@link ParamsProvider} is used, servlet-api and commons-fileupload
 * libraries must be available in the classpath, otherwise they can be omitted.
 * 
 * @author Radek Beran
 */
public class HttpServletRequestParams implements ParamsProvider {

	/** Max. size per single file */
	private static final int SINGLE_FILE_SIZE_MAX = 5242880; // 5 MB
	/** Max. for total size of request. */
	private static final int TOTAL_SIZE_MAX = 10485760; // 10 MB
	/** Max. size of file that is stored only in memory. */
	private static final int SIZE_THRESHOLD = 10240; // 10 KB
	private static final String DEFAULT_ENCODING = "utf-8";
	private final HttpServletRequest request;
	private final RequestProcessingError error;
	
	/**
	 * Creates request params extractor.
	 * @param request request
	 * @param defaultEncoding header and request parameter encoding 
	 * @param tempDir temporary directory to store files bigger than specified size threshold
	 * @param sizeThreshold max size of file (in bytes) that is loaded into the memory and not temporarily stored to disk
	 * @param totalSizeMax maximum allowed size of the whole request in bytes
	 * @param singleFileSizeMax maximum allowed size of a single uploaded file
	 */
	public HttpServletRequestParams(HttpServletRequest request, String defaultEncoding, File tempDir, int sizeThreshold, long totalSizeMax, long singleFileSizeMax) {
		if (request == null) throw new IllegalArgumentException("request cannot be null");
		String ctype = request.getHeader("Content-Type");
		HttpServletRequest r = null;
		// ctype can be for e.g.: multipart/form-data; boundary=---------------------------27073038615365
		if (ctype != null && ctype.toLowerCase().contains("multipart/form-data")) {
			FileUploadWrapper wr = new FileUploadWrapper(request, defaultEncoding, tempDir, sizeThreshold, totalSizeMax, singleFileSizeMax);
        	this.error = wr.getError();
            r = wr;
		} else { 
			r = request;
			this.error = null;
		}
		this.request = r;
	}
	
	public HttpServletRequestParams(HttpServletRequest request, String defaultEncoding, File tempDir, int sizeThreshold, long totalSizeMax) {
		this(request, defaultEncoding, tempDir, sizeThreshold, totalSizeMax, SINGLE_FILE_SIZE_MAX);
	}
	
	public HttpServletRequestParams(HttpServletRequest request, String defaultEncoding, File tempDir, int sizeThreshold) {
		this(request, defaultEncoding, tempDir, sizeThreshold, TOTAL_SIZE_MAX);
	}
	
	public HttpServletRequestParams(HttpServletRequest request, String defaultEncoding, File tempDir) {
		this(request, defaultEncoding, tempDir, SIZE_THRESHOLD);
	}
	
	public HttpServletRequestParams(HttpServletRequest request) {
		this(request, DEFAULT_ENCODING, new File(System.getProperty("java.io.tmpdir")));
	}
	
	// request.getParameterNames() returns only elements of type String
	@SuppressWarnings("unchecked")
	@Override
	public Iterable<String> getParamNames() {
		// FileUploadWrapper has overriden method getParameterNames that returns also names
		// of params with uploaded files
		return Collections.<String>list(request.getParameterNames());
	}
	
	@Override
	public String[] getParamValues(String paramName) {
		return request.getParameterValues(paramName);
	}
	
	@Override
	public String getParamValue(String paramName) {
		String value = null;
		String[] values = this.getParamValues(paramName);
		if (values != null && values.length > 0) {
			value = values[0];
		}
		return value;
	}

	@Override
	public UploadedFile[] getUploadedFiles(String paramName) {
		if (request instanceof FileUploadWrapper) {
			FileUploadWrapper w = (FileUploadWrapper)request;
			return w.getUploadedFiles(paramName);
		}
		return new UploadedFile[0];
	}
	
	@Override
	public UploadedFile getUploadedFile(String paramName) {
		UploadedFile file = null;
		UploadedFile[] files = this.getUploadedFiles(paramName);
		if (files != null && files.length > 0) {
			file = files[0];
		}
		return file;
	}
	
	@Override
	public RequestProcessingError getRequestError() {
		return error;
	}
	
	/**
	 * Convenience method for constructing a {@link RequestContext}.
	 * @return request context
	 */
	public RequestContext getRequestContext() {
		return new RequestCtx(this.request);
	}
	
}
