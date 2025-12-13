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

import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * Creates mock HTTP servlet requests.
 * @author Radek Beran
 */
public final class MockServletRequests {

	/**
	 * Creates new servlet request that contains given resource as multi part.
	 * @param paramName
	 * @param resourceName
	 * @return
	 */
	public static MockHttpServletRequest newRequest(String paramName, String resourceName, String mimeType) {
		try {
			MockHttpServletRequest request = new MockHttpServletRequest();
		    // Load resource being uploaded
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			MockServletRequests.class.getResourceAsStream(resourceName).transferTo(bos);
		    byte[] fileContent = bos.toByteArray();
		    
		    // Create multipart entity from resource using HttpClient 5
		    HttpEntity multipartEntity = MultipartEntityBuilder.create()
		        .addBinaryBody(paramName, fileContent, ContentType.parse(mimeType), resourceName)
		        .build();
		    
		    ByteArrayOutputStream requestContent = new ByteArrayOutputStream();
		    multipartEntity.writeTo(requestContent);
		    request.setContent(requestContent.toByteArray());
		    // Set content type of request (important, includes MIME boundary string)
		    String contentType = multipartEntity.getContentType();
		    request.setContentType(contentType);
		    request.setMethod("POST");
		    return request;
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	private MockServletRequests() {
	}
			
}
