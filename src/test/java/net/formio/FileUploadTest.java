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
package net.formio;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Locale;

import net.formio.data.TestForms;
import net.formio.domain.Registration;
import net.formio.servlet.ServletRequestParams;
import net.formio.upload.UploadedFile;
import net.formio.utils.MockServletRequest;
import net.formio.utils.TestUtils;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * File upload tests.
 * @author Radek Beran
 */
public class FileUploadTest {
	
	@Test
	public void testFileUpload() throws IOException {
		FormMapping<Registration> form = TestForms.REG_FORM;
		Locale locale = new Locale("cz");
				
		String fileName = "test.pdf";
		String mimeType = "application/pdf";
		MockHttpServletRequest request = MockServletRequest.newRequest("registration" + Forms.PATH_SEP + "cv", "/" + fileName, mimeType);
		FormData<Registration> regData = form.bind(new ServletRequestParams(request, "UTF-8", TestUtils.getTempDir()), locale);
		final Registration registration = regData.getData();
				
		assertNotNull(registration);
		UploadedFile cv = registration.getCv();
		assertNotNull("Uploaded file should not be null", cv);
		assertEquals(fileName, cv.getFileName());
		assertTrue(cv.getContentType().startsWith(mimeType));
		assertEquals(13390L, cv.getSize());
		assertNotNull(cv.getContent());
		cv.deleteTempFile();
	}
}
