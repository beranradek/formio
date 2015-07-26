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
package net.formio.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import net.formio.FormData;
import net.formio.Forms;
import net.formio.RequestParams;
import net.formio.data.TestForms;
import net.formio.domain.Registration;
import net.formio.format.Location;
import net.formio.upload.MaxFileSizeExceededError;
import net.formio.upload.MaxRequestSizeExceededError;
import net.formio.upload.UploadedFile;
import net.formio.validation.ConstraintViolationMessage;

public class FileUploadTest {
	protected static final String PDF_MIME_TYPE = "application/pdf";
	protected static final String PDF_FILE_NAME = "test.pdf";
	protected static final String CV_PARAM_NAME = "registration" + Forms.PATH_SEP + "cv";
	
	protected void testFileUpload(RequestParams requestParams) throws IOException {
		assertNull(requestParams.getRequestError());
		assertNotNull(requestParams.getUploadedFile(CV_PARAM_NAME));
		FormData<Registration> regData = TestForms.REG_FORM.bind(requestParams, Location.CZECH);
		final Registration registration = regData.getData();
				
		assertNotNull(registration);
		UploadedFile cv = registration.getCv();
		assertNotNull("Uploaded file should not be null", cv);
		assertEquals(PDF_FILE_NAME, cv.getFileName());
		assertTrue(cv.getContentType().startsWith(PDF_MIME_TYPE));
		assertEquals(13390L, cv.getSize());
		assertNotNull(cv.getContent());
		cv.deleteTempFile();
	}
	
	protected void testMaxFileSizeExceededUpload(RequestParams requestParams) {
		assertTrue("Params should contain request error", requestParams.getRequestError() instanceof MaxFileSizeExceededError);
		FormData<Registration> regData = TestForms.REG_FORM.bind(requestParams, Location.CZECH);
		
		assertTrue(!regData.isValid());
		List<ConstraintViolationMessage> cvErrors = regData.getValidationResult().getFieldMessages().get(CV_PARAM_NAME);
		assertTrue(!cvErrors.isEmpty());
		assertEquals(new MaxFileSizeExceededError("", null, 0, 0, CV_PARAM_NAME).getMessageKey(), "{" + cvErrors.get(0).getMsgKey() + "}");
		
		final Registration registration = regData.getData();
		UploadedFile cv = registration.getCv();
		if (cv != null) {
			cv.deleteTempFile();
		}
	}
	
	protected void testMaxRequestSizeExceededUpload(RequestParams requestParams) {
		assertTrue("Params should contain request error", requestParams.getRequestError() instanceof MaxRequestSizeExceededError);
		FormData<Registration> regData = TestForms.REG_FORM.bind(requestParams, Location.CZECH);
		
		assertTrue(!regData.isValid());
		List<ConstraintViolationMessage> globalErrors = regData.getValidationResult().getGlobalMessages();
		assertTrue(!globalErrors.isEmpty());
		assertEquals(new MaxRequestSizeExceededError("", null, 0, 0).getMessageKey(), "{" + globalErrors.get(0).getMsgKey() + "}");
		
		final Registration registration = regData.getData();
		UploadedFile cv = registration.getCv();
		if (cv != null) {
			cv.deleteTempFile();
		}
	}
}
