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

import net.formio.EncodingException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

/**
 * Preprocesses multipart/form-data request to {@link RequestUploadedFile}(s) and
 * string request parameters.
 * @author Radek Beran
 */
public class MultipartRequestPreprocessor {

	/** Max. size per single file */
	public static final int SINGLE_FILE_SIZE_MAX = 5242880; // 5 MB
	/** Max. for total size of request. */
	public static final int TOTAL_SIZE_MAX = 10485760; // 10 MB
	/** Max. size of file that is stored only in memory. */
	public static final int SIZE_THRESHOLD = 10240; // 10 KB
	public static final String DEFAULT_ENCODING = "utf-8";
	public static File getDefaultTempDir() { return new File(System.getProperty("java.io.tmpdir")); }
	
	private final String defaultEncoding;
	private final RequestProcessingError error;

	/**
	 * Wrapper which preprocesses multipart request.
	 * @param parser multipart request parser
	 * @param defaultEncoding header and request parameter encoding 
	 * @param tempDir temporary directory to store files bigger than specified size threshold
	 * @param sizeThreshold max size of file (in bytes) that is loaded into the memory and not temporarily stored to disk
	 * @param totalSizeMax maximum allowed size of the whole request in bytes
	 * @param singleFileSizeMax maximum allowed size of a single uploaded file
	 */
	public MultipartRequestPreprocessor(MultipartRequestParser parser, String defaultEncoding, File tempDir, int sizeThreshold, long totalSizeMax, long singleFileSizeMax) {
		this.defaultEncoding = defaultEncoding;
		final DiskFileItemFactory fif = new DiskFileItemFactory();
		if (tempDir != null) { 
			fif.setRepository(tempDir);
		}
		if (sizeThreshold > 0) { 
			fif.setSizeThreshold(sizeThreshold);
		}
		try {
			List<FileItem> fileItems = parser.parseFileItems(fif, singleFileSizeMax, totalSizeMax, defaultEncoding);
			convertToMaps(fileItems);
		} finally {
			this.error = parser.getError();
		}
	}

	/**
	 * Return all request parameter names, for both regular form fields and file
	 * upload fields.
	 */
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
	 * Returns error from processing the request if there was one, or {@code null}.
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
