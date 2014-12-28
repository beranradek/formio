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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.formio.data.MockRequestContext;
import net.formio.data.RequestContext;
import net.formio.data.TestForms;
import net.formio.security.HashTokenAuthorizer;
import net.formio.security.InvalidTokenException;
import net.formio.security.TokenAuthorizer;
import net.formio.security.TokenMissingException;

import org.junit.Test;

/**
 * Tests for {@link AuthTokens}.
 * @author Radek Beran
 */
public class AuthTokensTest {

	@Test
	public void testGenerateAndVerifyAuthToken() {
		RequestContext ctx = new MockRequestContext();
		TokenAuthorizer tokenAuthorizer = new HashTokenAuthorizer();
		String rootMappingPath = TestForms.PERSON_FORM.getName();
		String authToken = AuthTokens.generateAuthToken(ctx, tokenAuthorizer, rootMappingPath);
		assertTrue("Generated auth token should not be null or empty", authToken != null && !authToken.isEmpty());
		
		try {
			MapParams params = new MapParams();
			params.put(rootMappingPath + Forms.PATH_SEP + Forms.AUTH_TOKEN_FIELD_NAME, authToken);
			AuthTokens.verifyAuthToken(ctx, tokenAuthorizer, rootMappingPath, params, true);
		} catch (InvalidTokenException ex) {
			fail("Token is not valid: " + ex.getMessage());
		}
	}
	
	@Test(expected=TokenMissingException.class)
	public void testMissingAuthToken() {
		RequestContext ctx = new MockRequestContext();
		TokenAuthorizer tokenAuthorizer = new HashTokenAuthorizer();
		String rootMappingPath = TestForms.PERSON_FORM.getName();
		
		MapParams params = new MapParams(); // without auth token parameter
		AuthTokens.verifyAuthToken(ctx, tokenAuthorizer, rootMappingPath, params, true);
	}
	
	@Test(expected=InvalidTokenException.class)
	public void testInvalidAuthToken() {
		RequestContext ctx = new MockRequestContext();
		TokenAuthorizer tokenAuthorizer = new HashTokenAuthorizer();
		String rootMappingPath = TestForms.PERSON_FORM.getName();
		
		MapParams params = new MapParams(); // without auth token parameter
		params.put(rootMappingPath + Forms.PATH_SEP + Forms.AUTH_TOKEN_FIELD_NAME, "some_invalid_token_value");
		AuthTokens.verifyAuthToken(ctx, tokenAuthorizer, rootMappingPath, params, true);
	}
}
