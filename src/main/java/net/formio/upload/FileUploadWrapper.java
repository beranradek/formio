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
package net.formio.upload;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.formio.EncodingException;
import net.formio.common.FormUtils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Wrapper for a file upload request (before Servlet 3.0).
 * 
 * <p>
 * This class uses the Apache Commons <a
 * href='http://commons.apache.org/fileupload/'>File Upload tool</a>.
 * </p>
 */
public class FileUploadWrapper extends HttpServletRequestWrapper {

	private final String defaultEncoding;
	private final RequestProcessingError error;

	/**
	 * Wrapper which preprocesses multipart request.
	 * @param req request
	 * @param defaultEncoding header and request parameter encoding 
	 * @param tempDir temporary directory to store files bigger than specified size threshold
	 * @param sizeThreshold max size of file (in bytes) that is loaded into the memory and not temporarily stored to disk
	 * @param totalSizeMax maximum allowed size of the whole request in bytes
	 * @param singleFileSizeMax maximum allowed size of a single uploaded file
	 */
	public FileUploadWrapper(HttpServletRequest req, String defaultEncoding, File tempDir, int sizeThreshold, long totalSizeMax, long singleFileSizeMax) {
		super(req);
		this.defaultEncoding = defaultEncoding;
		final DiskFileItemFactory fif = new DiskFileItemFactory();
		if (tempDir != null) fif.setRepository(tempDir);
		if (sizeThreshold > 0) fif.setSizeThreshold(sizeThreshold);
		final ServletFileUpload upload = new ServletFileUpload(fif);
		// set overall request size constraint
		// maximum allowed size of a single uploaded file
		upload.setFileSizeMax(singleFileSizeMax);
		// maximum allowed size of the whole request
		upload.setSizeMax(totalSizeMax);
		if (defaultEncoding != null) upload.setHeaderEncoding(defaultEncoding);
		RequestProcessingError err = null;
		try {
			// processing multipart/form-data stream:
			List<FileItem> fileItems = upload.parseRequest(req);
			convertToMaps(fileItems);
		} catch (FileSizeLimitExceededException ex) {
			err = new MaxFileSizeExceededError(ex.getMessage(), ex, 
				ex.getActualSize(), ex.getPermittedSize(), FormUtils.removeTrailingBrackets(ex.getFieldName()));
		} catch (SizeLimitExceededException ex) {
			err = new MaxRequestSizeExceededError(ex.getMessage(), ex, ex.getActualSize(), ex.getPermittedSize());
		} catch (FileUploadException ex) {
			err = new RequestProcessingError(ex.getMessage(), ex);
		} finally {
			this.error = err;
		}
	}

	/**
	 * Return all request parameter names, for both regular form fields and file
	 * upload fields.
	 */
	@Override
	public Enumeration<String> getParameterNames() {
		Set<String> allNames = new LinkedHashSet<String>();
		allNames.addAll(regularParams.keySet());
		allNames.addAll(fileParams.keySet());
		return Collections.enumeration(allNames);
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
		String result = null;
		List<String> values = regularParams.get(name);
		if (values == null) {
			// you might try the wrappee, to see if it has a value
		} else if (values.isEmpty()) {
			// param name known, but no values present
			result = "";
		} else {
			// return first value in list
			result = values.get(FIRST_VALUE);
		}
		return result;
	}

	/**
	 * Return the parameter values. Applies only to regular parameters, not to
	 * file upload parameters.
	 */
	@Override
	public String[] getParameterValues(String name) {
		String[] result = null;
		List<String> values = regularParams.get(name);
		if (values != null) {
			result = values.toArray(new String[values.size()]);
		}
		return result;
	}

	/**
	 * Return a {@code Map<String, String[]>} for all regular parameters. Does
	 * not return any file upload parameters at all.
	 */
	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> res = new LinkedHashMap<String, String[]>();
		for (Map.Entry<String, List<String>> entry : regularParams.entrySet()) {
			res.put(entry.getKey(), entry.getValue().toArray(new String[0]));
		}
		return Collections.unmodifiableMap(res);
	}

	/**
	 * Return the {@link FileItem} of the given name.
	 * <p>
	 * If the name is unknown, then return <tt>null</tt>.
	 */
	public RequestUploadedFile[] getUploadedFiles(String paramName) {
		List<RequestUploadedFile> files = fileParams.get(paramName);
		if (files == null) return null;
		return files.toArray(new RequestUploadedFile[files.size()]);
	}
	
	/**
	 * Returns error from processing the request if there was one.
	 * @return
	 */
	public RequestProcessingError getError() {
		return this.error;
	}

	// PRIVATE

	/** Store regular params only. May be multivalued (hence the List). */
	private final Map<String, List<String>> regularParams = new LinkedHashMap<String, List<String>>();

	/** Store file params only. */
	private final Map<String, List<RequestUploadedFile>> fileParams = new LinkedHashMap<String, List<RequestUploadedFile>>();
	private static final int FIRST_VALUE = 0;
	private static final Pattern EXTRACT_LASTPART_PATTERN = Pattern.compile(".*[\\\\/]([^\\\\/]+)");

	private void convertToMaps(List<FileItem> fileItems) {
		for (FileItem item : fileItems) {
			String cts = item.getContentType();

			if (!item.isFormField()) {
				// not simple form field
				String filename = item.getName();
				long size = item.getSize(); // size in bytes
				// some browsers are sending "files" even if none is selected?
				if (size == 0 && ((cts == null || cts.isEmpty()) 
					|| "application/octet-stream".equalsIgnoreCase(cts)) && (filename == null || filename.isEmpty()))
					continue;

				String fileNameLastPart = filename;
				if (filename != null) {
					Matcher m = EXTRACT_LASTPART_PATTERN.matcher(filename);
					if (m.matches()) {
						fileNameLastPart = m.group(1);
					}
				}
				String mime = cts;
				if (mime == null) {
					mime = guessContentType(filename);
				}

				RequestUploadedFile file = new RequestUploadedFile(fileNameLastPart, mime, size, item);
				if (fileParams.get(item.getFieldName()) != null) {
					addMultivaluedFile(file, item);
				} else {
					addSingleValueFile(file, item);
				}
			} else {
				if (regularParams.get(item.getFieldName()) != null) {
					addMultivaluedItem(item);
				} else {
					addSingleValueItem(item);
				}
			}
		}
	}

	private void addSingleValueItem(FileItem item) {
		List<String> list = new ArrayList<String>();
		addItemValue(list, item);
		regularParams.put(item.getFieldName(), list);
	}

	private void addMultivaluedItem(FileItem item) {
		List<String> values = regularParams.get(item.getFieldName());
		addItemValue(values, item);
	}
	
	private void addSingleValueFile(RequestUploadedFile file, FileItem item) {
		List<RequestUploadedFile> list = new ArrayList<RequestUploadedFile>();
		list.add(file);
		fileParams.put(item.getFieldName(), list);
	}

	private void addMultivaluedFile(RequestUploadedFile file, FileItem item) {
		List<RequestUploadedFile> values = fileParams.get(item.getFieldName());
		values.add(file);
	}

	private void addItemValue(List<String> list, FileItem item) {
		try {
			list.add(item.getString(defaultEncoding));
		} catch (UnsupportedEncodingException ex) {
			throw new EncodingException(ex.getMessage(), ex);
		}
	}

	private String guessContentType(String filename) {
		if (filename != null) {
			int d = filename.lastIndexOf('.');
			if (d >= 0) {
				String ext = filename.substring(d);
				if (ext.equalsIgnoreCase("txt"))
					return "text/plain";
				if (ext.equalsIgnoreCase("html"))
					return "text/html";
				if (ext.equalsIgnoreCase("htm"))
					return "text/htm";
				if (ext.equalsIgnoreCase("jpg"))
					return "image/jpeg";
				if (ext.equalsIgnoreCase("jpeg"))
					return "image/jpeg";
				if (ext.equalsIgnoreCase("gif"))
					return "image/gif";
				if (ext.equalsIgnoreCase("png"))
					return "image/png";
				if (ext.equalsIgnoreCase("bmp"))
					return "image/bmp";
			}
		}
		return "application/octet-stream";
	}
}
