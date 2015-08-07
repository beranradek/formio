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
package net.formio;

import net.formio.ajax.AjaxParams;
import net.formio.upload.RequestProcessingError;
import net.formio.upload.UploadedFile;

/**
 * Provides values for request parameters.
 * @author Radek Beran
 */
public interface RequestParams {
	
	/**
	 * Returns names of all params available in the request (also params with uploaded files).
	 * @return names of request parameters
	 */
	Iterable<String> getParamNames();
	
	/**
	 * Returns {@code null} if parameter with given name does not exist;
	 * empty array, if the parameter is known but no values are specified;
	 * array of values otherwise.
	 * @param paramName
	 * @return values for request parameter
	 */
	String[] getParamValues(String paramName);
	
	/**
	 * Returns first/the only one value of parameter with given name, 
	 * {@code null} if parameter with given name does not exist.
	 * @param paramName
	 * @return first/the only one value of request parameter
	 */
	String getParamValue(String paramName);
	
	/**
	 * Returns {@code null} if parameter with given name does not exist or no file was uploaded;
	 * uploaded file otherwise.
	 * @param paramName
	 * @return uploaded files for given request parameter
	 */
	UploadedFile[] getUploadedFiles(String paramName);
	
	/**
	 * Returns first/the only one uploaded file for parameter with given name, 
	 * {@code null} if parameter with given name does not exist.
	 * @param paramName
	 * @return first/the only one uploaded file for request parameter
	 */
	UploadedFile getUploadedFile(String paramName);
	
	/**
	 * Returns serious error that was cought when processing the request, or {@code null} if there was no error.
	 * @return request processing error
	 */
	RequestProcessingError getRequestError();
	
	/**
	 * Returns true if given request is TDI AJAX request.
	 * TDI AJAX request is indicated by presence of {@link AjaxParams#INFUSE} request parameter.
	 * @return true if this is TDI AJAX request
	 */
	boolean isTdiAjaxRequest();
	
	/**
	 * Returns name of the form element that initiated the TDI AJAX request,
	 * {@code null} if this is not an TDI AJAX request.
	 * Name of such AJAX event source element is transfered in {@link AjaxParams#SRC_ELEMENT_NAME} request parameter.
	 * @return name of the form element that initiated the TDI AJAX request
	 */
	String getTdiAjaxSrcElementName();
}
