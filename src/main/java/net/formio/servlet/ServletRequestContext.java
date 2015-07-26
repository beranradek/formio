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

import javax.servlet.http.HttpServletRequest;

import net.formio.data.RequestContext;
import net.formio.data.SessionStorage;

/**
 * Implementation of {@link RequestContext} for servlet API.
 * @author Radek Beran
 */
public class ServletRequestContext implements RequestContext {
	
	public static final String SEPARATOR = "_";
	private final HttpServletRequest request;
	
	public ServletRequestContext(HttpServletRequest request) {
		if (request == null) throw new IllegalArgumentException("request cannot be null");
		this.request = request;
	}

	@Override
	public SessionStorage getSessionStorage() {
		return new HttpSessionStorage(this.request.getSession());
	}
	
	@Override
	public String secretWithUserIdentification(String secret) {
		return secret + SEPARATOR + getUserRequestIdentification();
	}
	
	protected String getUserRequestIdentification() {
		StringBuilder sb = new StringBuilder();
		String ua = request.getHeader("User-Agent");
		if (ua != null && !ua.isEmpty()) {
			sb.append(ua);
		}
		String ipAddress = getClientIpAddr(request);
		if (ipAddress != null && !ipAddress.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(SEPARATOR);
			}
			sb.append(ipAddress);
		}
		return sb.toString();
	}
	
	private String getClientIpAddr(HttpServletRequest request) {  
        String ip = request.getHeader("X-Forwarded-For");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  
        return ip;  
    }

}
