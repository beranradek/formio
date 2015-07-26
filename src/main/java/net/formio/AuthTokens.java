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

import net.formio.data.RequestContext;
import net.formio.security.PasswordGenerator;
import net.formio.security.TokenAuthorizer;
import net.formio.security.TokenMissingException;

/**
 * Operations with authorization tokens.
 * @author Radek Beran
 */
final class AuthTokens {
	
	/** Prefix of key under which the secret is stored. */
	static final String SECRET_KEY_PREFIX = "formio_secret_";
	static final String ALLOWED_TOKEN_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_@#$%^&*";
	
	/**
	 * Generates authorization token and stores it in "user related" storage (on the server)
	 * for later verification.
	 * @param ctx
	 * @param tokenAuthorizer
	 * @param rootMappingPath
	 * @return generated token
	 */
	static String generateAuthToken(RequestContext ctx, TokenAuthorizer tokenAuthorizer, String rootMappingPath) {
		if (ctx == null) {
			throw new IllegalStateException(RequestContext.class.getSimpleName() + " is required when the form is " + 
				"defined as secured. Please specify not null context in fill method.");
		}
		String genSecret = generateSecret();
		if (ctx.getSessionStorage() == null) {
			throw new IllegalStateException("User related storage must exist to store CSRF token.");
		}
		ctx.getSessionStorage().set(getRootMappingSecretKey(rootMappingPath), genSecret);
		String reqSecret = ctx.secretWithUserIdentification(genSecret);
		return tokenAuthorizer.generateToken(reqSecret);
	}
	
	/**
	 * Verification of authorization token. Must be called after the verification is done on nested
	 * mappings.
	 * @param ctx
	 * @param tokenAuthorizer
	 * @param rootMappingPath
	 * @param requestParams
	 * @param rootMapping true if this method is called from root mapping
	 * @throws InvalidTokenException if token is invalid
	 */
	static void verifyAuthToken(RequestContext ctx, TokenAuthorizer tokenAuthorizer, String rootMappingPath, RequestParams requestParams, boolean rootMapping) {
		String secretKey = AuthTokens.getRootMappingSecretKey(rootMappingPath);
		try {
			if (ctx == null) {
				throw new IllegalStateException(RequestContext.class.getSimpleName() + " is required when the form is " + 
					"defined as secured. Please specify not null context in bind method.");
			}
			String token = getAuthTokenFromRequest(requestParams, rootMappingPath);
			if ("".equals(token)) {
				throw new TokenMissingException("Unauthorized attempt. Authorization token is missing! It should be posted as " + Forms.AUTH_TOKEN_FIELD_NAME + 
					" field. Maybe this is blocked CSRF attempt or the required field with token is not rendered in the form correctly.");
			}
			if (ctx.getSessionStorage() == null) {
				throw new IllegalStateException("User related storage must exist to verify CSRF token.");
			}
			String genSecret = ctx.getSessionStorage().get(secretKey);
			String reqSecret = ctx.secretWithUserIdentification(genSecret);
			// InvalidTokenException is thrown for invalid token
			tokenAuthorizer.validateToken(token, reqSecret);
		} finally {
			if (rootMapping) {
				// At the end, when the whole form is submitted and data bind,
				// secret for token validation held on the server side is deleted
				if (ctx != null) {
					ctx.getSessionStorage().delete(secretKey);
				}
			}
		}
	}
	
	private static String getRootMappingSecretKey(String rootMappingPath) {
		return SECRET_KEY_PREFIX + rootMappingPath;
	}
	
	private static String getAuthTokenFromRequest(RequestParams params, String rootMappingPath) {
		String token = params.getParamValue(rootMappingPath + Forms.PATH_SEP + Forms.AUTH_TOKEN_FIELD_NAME);
		if (token == null) {
			token = "";
		}
		return token;
	}
	
	private static String generateSecret() {
		return PasswordGenerator.generatePassword(20, ALLOWED_TOKEN_CHARS);
	}
	
	private AuthTokens() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
