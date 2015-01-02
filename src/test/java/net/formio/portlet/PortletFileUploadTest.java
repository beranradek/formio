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
package net.formio.portlet;

import java.io.IOException;

import net.formio.RequestParams;
import net.formio.internal.FormUtils;
import net.formio.servlet.FileUploadTest;
import net.formio.upload.MultipartRequestPreprocessor;

import org.junit.Test;
import org.springframework.mock.web.portlet.MockMultipartActionRequest;

/**
 * File upload tests for portlets.
 * @author Radek Beran
 */
public class PortletFileUploadTest extends FileUploadTest {
	
	@Test
	public void testFileUpload() throws IOException {
		MockMultipartActionRequest request = MockPortletRequests.newRequest(CV_PARAM_NAME, "/" + PDF_FILE_NAME, PDF_MIME_TYPE);
		RequestParams requestParams = new PortletRequestParams(request, "UTF-8", FormUtils.getTempDir());
		testFileUpload(requestParams);
	}
	
	@Test
	public void testMaxFileSizeExceededUpload() {
		MockMultipartActionRequest request = MockPortletRequests.newRequest(CV_PARAM_NAME, "/" + PDF_FILE_NAME, PDF_MIME_TYPE);
		RequestParams requestParams = new PortletRequestParams(request, "UTF-8", FormUtils.getTempDir(), 
			MultipartRequestPreprocessor.SIZE_THRESHOLD, MultipartRequestPreprocessor.TOTAL_SIZE_MAX, 10L); // 10 bytes per file only
		testMaxFileSizeExceededUpload(requestParams);
	}
	
	@Test
	public void testMaxRequestSizeExceededUpload() {
		MockMultipartActionRequest request = MockPortletRequests.newRequest(CV_PARAM_NAME, "/" + PDF_FILE_NAME, PDF_MIME_TYPE);
		RequestParams requestParams = new PortletRequestParams(request, "UTF-8", FormUtils.getTempDir(), 
			MultipartRequestPreprocessor.SIZE_THRESHOLD, 20L); // 20 bytes for the whole request only
		testMaxRequestSizeExceededUpload(requestParams);
	}
}
