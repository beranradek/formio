package org.twinstone.formio.upload;

import java.io.Serializable;

public abstract class AbstractUploadedFile implements UploadedFile, Serializable {
	private static final long serialVersionUID = -3395961809415775852L;
	private final String fileName;
	private final String contentType;
	private final long size;

	public AbstractUploadedFile(String fileName, String contentType, long size) {
		this.fileName = fileName;
		this.contentType = contentType;
		this.size = size;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public long getSize() {
		return size;
	}
	
	@Override
	public String toString() {
		return "File " + getFileName() + ", size=" + getSize()+", type=" + getContentType();
	}

}
