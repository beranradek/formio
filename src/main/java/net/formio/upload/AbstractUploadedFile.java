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

/**
 * Basic abstract implementation of {@link UploadedFile}.
 * @author Radek Beran
 */
public abstract class AbstractUploadedFile implements UploadedFile {
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
