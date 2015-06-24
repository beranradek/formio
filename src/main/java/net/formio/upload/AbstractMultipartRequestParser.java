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

import java.util.ArrayList;
import java.util.List;

import net.formio.internal.FormUtils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

/**
 * Common implementation of multipart request parsers.
 * @author Radek Beran
 */
public abstract class AbstractMultipartRequestParser implements MultipartRequestParser {
	private RequestProcessingError error;

	public AbstractMultipartRequestParser() {
	}

	@Override
	public List<FileItem> parseFileItems(FileItemFactory fif, long singleFileSizeMax, long totalSizeMax, String defaultEncoding) {
		List<FileItem> fileItems = new ArrayList<FileItem>();
		RequestProcessingError err = null;
		try {
			fileItems = parseRequest(fif, singleFileSizeMax, totalSizeMax, defaultEncoding);
		} catch (FileSizeLimitExceededException ex) {
			err = new MaxFileSizeExceededError(ex.getMessage(), ex, ex.getActualSize(), ex.getPermittedSize(),
				FormUtils.removeTrailingBrackets(ex.getFieldName()));
		} catch (SizeLimitExceededException ex) {
			err = new MaxRequestSizeExceededError(ex.getMessage(), ex, ex.getActualSize(), ex.getPermittedSize());
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
	 * @param defaultEncoding
	 * @return
	 * @throws FileUploadException
	 */
	protected abstract List<FileItem> parseRequest(FileItemFactory fif, long singleFileSizeMax, long totalSizeMax, String defaultEncoding) throws FileUploadException;
	
	/**
	 * Convenience method for common configuration of {@link FileUpload}.
	 * Can be called from {@link #parseRequest(FileItemFactory, long, long, String)} method
	 * that must be implemented by subclasses.
	 * @param upload
	 * @param singleFileSizeMax
	 * @param totalSizeMax
	 * @param defaultEncoding
	 */
	protected void configureUpload(final FileUpload upload, long singleFileSizeMax, long totalSizeMax, String defaultEncoding) {
		// set overall request size constraint
		// maximum allowed size of a single uploaded file
		upload.setFileSizeMax(singleFileSizeMax);
		// maximum allowed size of the whole request
		upload.setSizeMax(totalSizeMax);
		if (defaultEncoding != null) {
			upload.setHeaderEncoding(defaultEncoding);
		}
	}
}
