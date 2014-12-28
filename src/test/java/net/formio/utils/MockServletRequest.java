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
package net.formio.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * Creates mock HTTP servlet requests.
 * @author Radek Beran
 */
public final class MockServletRequest {

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
		    Streams.copy(MockServletRequest.class.getResourceAsStream(resourceName), bos, true);
		    byte[] fileContent = bos.toByteArray();
		    
		    // Create part & entity from resource
		    Part[] parts = new Part[] {
		        new FilePart(paramName, new ByteArrayPartSource(resourceName, fileContent), mimeType, (String)null) 
		    };
		    MultipartRequestEntity multipartRequestEntity =
		        new MultipartRequestEntity(parts, new PostMethod().getParams());
		    
		    ByteArrayOutputStream requestContent = new ByteArrayOutputStream();
		    multipartRequestEntity.writeRequest(requestContent);
		    request.setContent(requestContent.toByteArray());
		    // Set content type of request (important, includes MIME boundary string)
		    String contentType = multipartRequestEntity.getContentType();
		    request.setContentType(contentType);
		    request.setMethod("POST");
		    return request;
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	private MockServletRequest() {
	}
			
}
