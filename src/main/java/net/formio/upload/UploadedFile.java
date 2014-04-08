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

import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.ReadableByteChannel;

/**
 * Data of uploaded file.
 * @author Radek Beran
 */
public interface UploadedFile extends Serializable {

	/**
	 * Returns data of the file.
	 * @return
	 * @throws IOException
	 */
	ReadableByteChannel getContent() throws IOException;
	
	/**
	 * Deletes uploaded file from temporary directory
	 * (should be called after the file is processed/stored
	 * permanently).
	 */
	void deleteTempFile();
	
	/**
	 * Returns the file name.
	 * @return String
	 */
	String getFileName();

	/**
	 * Returns the MIME content type of underlying data.
	 * @return String
	 */
	String getContentType();

	/**
	 * Returns the size of underlying data in bytes.
	 * @return long
	 */
	long getSize();
}
