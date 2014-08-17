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

import java.io.File;
import java.util.Collections;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;

import net.formio.AbstractRequestParams;
import net.formio.RequestParams;
import net.formio.data.RequestContext;
import net.formio.upload.MultipartRequestPreprocessor;
import net.formio.upload.RequestProcessingError;
import net.formio.upload.UploadedFile;

import org.apache.commons.fileupload.portlet.PortletFileUpload;

/**
 * Implementation of {@link RequestParams} for portlet request that
 * uses commons-fileupload library for uploading files. If this implementation
 * of {@link RequestParams} is used, portlet-api and commons-fileupload
 * libraries must be available in the classpath, otherwise they can be omitted.
 * 
 * @author Radek Beran
 */
public class PortletRequestParams extends AbstractRequestParams {
	private final PortletRequest request;
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
	public PortletRequestParams(ActionRequest request, String defaultEncoding, File tempDir, int sizeThreshold, long totalSizeMax, long singleFileSizeMax) {
		if (request == null) throw new IllegalArgumentException("request cannot be null");
		PortletRequest r = null;
		if (PortletFileUpload.isMultipartContent(request)) {
			PortletFileUploadWrapper wr = new PortletFileUploadWrapper(request, defaultEncoding, tempDir, sizeThreshold, totalSizeMax, singleFileSizeMax);
        	this.error = wr.getRequestProcessingError();
            r = wr;
		} else { 
			r = request;
			this.error = null;
		}
		this.request = r;
	}
	
	public PortletRequestParams(ActionRequest request, String defaultEncoding, File tempDir, int sizeThreshold, long totalSizeMax) {
		this(request, defaultEncoding, tempDir, sizeThreshold, totalSizeMax, MultipartRequestPreprocessor.SINGLE_FILE_SIZE_MAX);
	}
	
	public PortletRequestParams(ActionRequest request, String defaultEncoding, File tempDir, int sizeThreshold) {
		this(request, defaultEncoding, tempDir, sizeThreshold, MultipartRequestPreprocessor.TOTAL_SIZE_MAX);
	}
	
	public PortletRequestParams(ActionRequest request, String defaultEncoding, File tempDir) {
		this(request, defaultEncoding, tempDir, MultipartRequestPreprocessor.SIZE_THRESHOLD);
	}
	
	public PortletRequestParams(ActionRequest request) {
		this(request, MultipartRequestPreprocessor.DEFAULT_ENCODING, MultipartRequestPreprocessor.getDefaultTempDir());
	}
	
	// request.getParameterNames() returns only elements of type String
	@Override
	public Iterable<String> getParamNames() {
		// PortletFileUploadWrapper has overriden method getParameterNames that returns also names
		// of params with uploaded files
		return Collections.<String>list(request.getParameterNames());
	}
	
	@Override
	public String[] getParamValues(String paramName) {
		return request.getParameterValues(paramName);
	}

	@Override
	public UploadedFile[] getUploadedFiles(String paramName) {
		if (request instanceof PortletFileUploadWrapper) {
			PortletFileUploadWrapper w = (PortletFileUploadWrapper)request;
			return w.getUploadedFiles(paramName);
		}
		return new UploadedFile[0];
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
		return new PortletRequestContext(this.request);
	}
	
}
