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
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.formio.upload.MultipartRequestPreprocessor;
import net.formio.upload.RequestProcessingError;
import net.formio.upload.RequestUploadedFile;

import org.apache.commons.fileupload.FileItem;

/**
 * Wrapper for a file upload servlet request.
 * <p>
 * This class uses the Apache Commons <a
 * href='http://commons.apache.org/fileupload/'>File Upload tool</a>.
 * </p>
 */
class ServletFileUploadWrapper extends HttpServletRequestWrapper {
	private final MultipartRequestPreprocessor reqPreprocessor;

	/**
	 * Wrapper which preprocesses multipart request.
	 * @param req request
	 * @param defaultEncoding header and request parameter encoding 
	 * @param tempDir temporary directory to store files bigger than specified size threshold
	 * @param sizeThreshold max size of file (in bytes) that is loaded into the memory and not temporarily stored to disk
	 * @param totalSizeMax maximum allowed size of the whole request in bytes
	 * @param singleFileSizeMax maximum allowed size of a single uploaded file
	 */
	public ServletFileUploadWrapper(HttpServletRequest req, String defaultEncoding, File tempDir, int sizeThreshold, long totalSizeMax, long singleFileSizeMax) {
		super(req);
		final MultipartRequestPreprocessor reqPreprocessor = new MultipartRequestPreprocessor(
			new ServletMultipartRequestParser(req),
			defaultEncoding, 
			tempDir,
			sizeThreshold,
			totalSizeMax,
			singleFileSizeMax
		);
		this.reqPreprocessor = reqPreprocessor;
	}

	/**
	 * Return all request parameter names, for both regular form fields and file
	 * upload fields.
	 */
	@Override
	public Enumeration<String> getParameterNames() {
		return reqPreprocessor.getParameterNames();
	}

	/**
	 * Return the parameter value. Applies only to regular parameters, not to
	 * file upload parameters.
	 * 
	 * <p>
	 * If the parameter is not present in the underlying request, then
	 * <tt>null</tt> is returned.
	 * <p>
	 * If the parameter is present, but has no associated value, then an empty
	 * string is returned.
	 * <p>
	 * If the parameter is multivalued, the first value that appears in the
	 * request is returned.
	 */
	@Override
	public String getParameter(String name) {
		return reqPreprocessor.getParameter(name);
	}

	/**
	 * Return the parameter values. Applies only to regular parameters, not to
	 * file upload parameters.
	 */
	@Override
	public String[] getParameterValues(String name) {
		return reqPreprocessor.getParameterValues(name);
	}

	/**
	 * Return a {@code Map<String, String[]>} for all regular parameters. Does
	 * not return any file upload parameters at all.
	 */
	@Override
	public Map<String, String[]> getParameterMap() {
		return reqPreprocessor.getParameterMap();
	}

	/**
	 * Return the {@link FileItem} of the given name.
	 * <p>
	 * If the name is unknown, then return <tt>null</tt>.
	 */
	public RequestUploadedFile[] getUploadedFiles(String paramName) {
		return reqPreprocessor.getUploadedFiles(paramName);		
	}
	
	/**
	 * Returns error from processing the request if there was one, or {@code null}.
	 * @return
	 */
	public RequestProcessingError getRequestProcessingError() {
		return reqPreprocessor.getError();
	}
}
