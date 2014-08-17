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

import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;

/**
 * Parser of multipart request.
 * @author Radek Beran
 */
public interface MultipartRequestParser {
	List<FileItem> parseFileItems(FileItemFactory fif, long singleFileSizeMax, long totalSizeMax, String defaultEncoding);
	
	/**
	 * Returns error from parsing multipart request, or {@code null} in case of success.
	 * @return
	 */
	RequestProcessingError getError();
}
