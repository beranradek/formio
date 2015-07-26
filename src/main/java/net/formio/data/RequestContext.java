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
package net.formio.data;

/**
 * Context of the request required for some form processing features.
 * @author Radek Beran
 */
public interface RequestContext {
	
	/**
	 * Returns storage related only to current user.
	 * This storage can be used to preserve data among the requests.
	 * @return
	 */
	SessionStorage getSessionStorage();
	
	/**
	 * Returns secret string that identifies current user plus contains given secret.
	 * @param secret
	 * @return
	 */
	String secretWithUserIdentification(String secret);
	
}
