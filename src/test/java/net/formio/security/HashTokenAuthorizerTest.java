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
package net.formio.security;

import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;

public class HashTokenAuthorizerTest {
	
	private static final Logger LOG = Logger.getLogger(HashTokenAuthorizerTest.class.getName());

	@Test
	public void testValidToken() {
		String secret = "e#2fgTN^f31";
		HashTokenAuthorizer authorizer = new HashTokenAuthorizer();
		String token = authorizer.generateToken(secret);
		LOG.info("Token: " + token);
		assertTrue("Token should be valid", authorizer.isValidToken(token, secret));
	}
	
	@Test
	public void testInvalidToken() {
		String secret = "e#2fgTN^f31";
		String anotherSecret = "e#2fgTN^f323";
		HashTokenAuthorizer authorizer = new HashTokenAuthorizer();
		String token = authorizer.generateToken(anotherSecret);
		LOG.info("Token: " + token);
		assertTrue("Token should be invalid", !authorizer.isValidToken(token, secret));
	}
	
	@Test(expected=InvalidTokenException.class)
	public void testInvalidTokenException() {
		String secret = "e#2fgTN^f31";
		String anotherSecret = "e#2fgTN^f323";
		HashTokenAuthorizer authorizer = new HashTokenAuthorizer();
		String token = authorizer.generateToken(anotherSecret);
		LOG.info("Token: " + token);
		authorizer.validateToken(token, secret);
	}
	
	@Test
	public void testNotExpiredToken() {
		String secret = "e#2fgTN^f31";
		HashTokenAuthorizer authorizer = new HashTokenAuthorizer();
		long currentTime = System.currentTimeMillis();
		long time = currentTime - authorizer.getMaxAllowedTimeDifference() + 10000;
		String token = authorizer.tokenFromSecretAndTime(secret, time);
		LOG.info("Token: " + token);
		assertTrue("Token should be valid", authorizer.isValidToken(token, secret));
	}
	
	@Test
	public void testExpiredToken() {
		String secret = "e#2fgTN^f31";
		HashTokenAuthorizer authorizer = new HashTokenAuthorizer();
		long currentTime = System.currentTimeMillis();
		long time = currentTime - authorizer.getMaxAllowedTimeDifference() - 10000;
		String token = authorizer.tokenFromSecretAndTime(secret, time);
		LOG.info("Token: " + token);
		assertTrue("Token should be invalid", !authorizer.isValidToken(token, secret));
	}

}
