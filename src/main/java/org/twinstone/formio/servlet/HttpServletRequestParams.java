package org.twinstone.formio.servlet;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.twinstone.formio.ParamsProvider;
import org.twinstone.formio.upload.FileUploadWrapper;
import org.twinstone.formio.upload.RequestProcessingError;
import org.twinstone.formio.upload.UploadedFile;

/**
 * Implementation of {@link ParamsProvider} for servlet request that
 * uses commons-fileupload library for uploading files. If this implementation
 * of {@link ParamsProvider} is used, servlet-api and commons-fileupload
 * libraries must be available in the classpath, otherwise they can be omitted.
 * 
 * @author Radek Beran
 */
public class HttpServletRequestParams implements ParamsProvider {

	private final HttpServletRequest request;
	private final RequestProcessingError error;
	
	/**
	 * Creates request params extractor.
	 * @param req request
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
		this(request, defaultEncoding, tempDir, sizeThreshold, totalSizeMax, 10485760); // 10 MB max. per single file
	}
	
	public HttpServletRequestParams(HttpServletRequest request, String defaultEncoding, File tempDir, int sizeThreshold) {
		this(request, defaultEncoding, tempDir, sizeThreshold, 20971520); // 20 MB max. for total size of request
	}
	
	public HttpServletRequestParams(HttpServletRequest request, String defaultEncoding, File tempDir) {
		this(request, defaultEncoding, tempDir, 10240); // 10 KB max. size of file that is stored only in memory
	}
	
	@Override
	public String[] getParamValues(String paramName) {
		return request.getParameterValues(paramName);
	}

	@Override
	public UploadedFile[] getUploadedFiles(String paramName) {
		if (request instanceof FileUploadWrapper) {
			FileUploadWrapper w = (FileUploadWrapper)request;
			return w.getUploadedFiles(paramName);
		}
		return null;
	}
	
	@Override
	public RequestProcessingError getRequestError() {
		return error;
	}
	
}
