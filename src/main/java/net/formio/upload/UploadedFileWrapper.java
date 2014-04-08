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

import java.io.Serializable;

/**
 * Complex object wrapping up one uploaded file.
 * @author Radek Beran
 */
public class UploadedFileWrapper implements Serializable {
	private static final long serialVersionUID = 7384688877832154811L;

	private UploadedFile file;
	
	public UploadedFileWrapper() {
		this.file = new RequestUploadedFile(null, null, 0L, null);
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}
	
	public boolean isEmpty() {
		return file == null || file.getFileName() == null;
	}
	
	@Override
	public String toString() {
		String str = null;
		if (isEmpty()) {
			str = "empty";
		} else {
			str = this.file.toString();
		}
		return str;
	}

}
