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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.formio.upload.AbstractMultipartRequestParser;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Parser of multipart request for servlet API.
 * @author Radek Beran
 */
class ServletMultipartRequestParser extends AbstractMultipartRequestParser {
	private final HttpServletRequest request;

	public ServletMultipartRequestParser(HttpServletRequest request) {
		if (request == null) throw new IllegalArgumentException("request cannot be null");
		this.request = request;
	}

	@Override
	protected List<FileItem> parseRequest(FileItemFactory fif, long singleFileSizeMax, long totalSizeMax, String defaultEncoding) throws FileUploadException {
		final ServletFileUpload upload = new ServletFileUpload(fif);
		configureUpload(upload, singleFileSizeMax, totalSizeMax, defaultEncoding);
		return upload.parseRequest(request);
	}
}
