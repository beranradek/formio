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
package net.formio.ajax;

import net.formio.RequestParams;

/**
 * {@link RequestParams} that allow identification of TDI AJAX request
 * and of the source element that ivoked the TDI AJAX request. 
 * @author Radek Beran
 */
public interface TdiAjaxRequestParams extends RequestParams {
	/**
	 * Returns true if given request is TDI AJAX request.
	 * @return
	 */
	boolean isTdiAjaxRequest();
	
	/**
	 * Returns name of the form element that initiated the TDI AJAX request,
	 * {@code null} if this is not an TDI AJAX request. 
	 * @return
	 */
	String getTdiAjaxSrcElementName();
}
