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

import org.apache.commons.fileupload2.core.FileItem;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Implementation of {@link UploadedFile} that uses {@link FileItem} from commons-fileupload library.
 * @author Radek Beran
 */
public class RequestUploadedFile extends AbstractUploadedFile {
	private static final long serialVersionUID = 4928481456790370482L;
	protected FileItem fileItem;

	public RequestUploadedFile(String fileName, String contentType, long size, FileItem fileItem) {
		super(fileName, contentType, size);
		this.fileItem = fileItem;
	}
	
	@Override
	public ReadableByteChannel getContent() throws IOException {
		assertNotCleared();
		return Channels.newChannel(fileItem.getInputStream());
	}

	@Override
	public void deleteTempFile() {
		if (fileItem != null) {
            try {
                fileItem.delete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fileItem = null;
		}
	}
	
	@Override
	public String toString() {
		return "File " + getFileName() + ", size=" + getSize()+", type=" + getContentType();
	}
	
	private void assertNotCleared() {
		if (fileItem == null) throw new IllegalStateException("file item has been already cleared");
	}

}
