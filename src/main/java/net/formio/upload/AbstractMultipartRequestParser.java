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
package net.formio.upload;

import net.formio.internal.FormUtils;
import org.apache.commons.fileupload2.core.*;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Common implementation of multipart request parsers.
 * See also https://commons.apache.org/proper/commons-fileupload/using.html.
 *
 * @author Radek Beran
 */
public abstract class AbstractMultipartRequestParser implements MultipartRequestParser {
	private RequestProcessingError error;

	public AbstractMultipartRequestParser() {
	}

	@Override
	public List<FileItem> parseFileItems(FileItemFactory fif, long singleFileSizeMax, long totalSizeMax, Charset headerCharset) {
		List<FileItem> fileItems = new ArrayList<>();
		RequestProcessingError err = null;
		try {
			fileItems = parseRequest(fif, singleFileSizeMax, totalSizeMax, headerCharset);
		} catch (FileUploadByteCountLimitException ex) {
			err = new MaxFileSizeExceededError(ex.getMessage(), ex, ex.getActualSize(), ex.getPermitted(),
				FormUtils.removeTrailingBrackets(ex.getFieldName()));
		} catch (FileUploadSizeException ex) {
			err = new MaxRequestSizeExceededError(ex.getMessage(), ex, ex.getActualSize(), ex.getPermitted());
		} catch (FileUploadException ex) {
			err = new RequestProcessingError(null, ex.getMessage(), ex);
		} finally {
			this.error = err;
		}
		return fileItems;
	}
	
	@Override
	public RequestProcessingError getError() {
		return error;
	}
		
	/**
	 * Should be implemented by subclasses. All exceptions should be thrown - they are handled
	 * properly by this abstract class.
	 * @param fif
	 * @param singleFileSizeMax
	 * @param totalSizeMax
	 * @param headerCharset
	 * @return
	 * @throws org.apache.commons.fileupload2.core.FileUploadException
	 */
	protected abstract List<FileItem> parseRequest(FileItemFactory fif, long singleFileSizeMax, long totalSizeMax, Charset headerCharset) throws FileUploadException;
	
	/**
	 * Convenience method for common configuration of file upload.
	 * Can be called from {@link #parseRequest(FileItemFactory, long, long, Charset)} method
	 * that must be implemented by subclasses.
	 * @param upload
	 * @param singleFileSizeMax
	 * @param totalSizeMax
	 * @param headerCharset
	 */
	protected void configureUpload(final JakartaServletFileUpload upload, long singleFileSizeMax, long totalSizeMax, Charset headerCharset) {
		// set overall request size constraint
		// maximum allowed size of a single uploaded file
		upload.setFileSizeMax(singleFileSizeMax);
		// maximum allowed size of the whole request
		upload.setSizeMax(totalSizeMax);
		if (headerCharset != null) {
			upload.setHeaderCharset(headerCharset);
		}
	}
}
