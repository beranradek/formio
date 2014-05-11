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

/**
 * Implementation of {@link TokenAuthorizer} using hash-based tokens. Immutable.
 * @author Radek Beran
 */
public class HashTokenAuthorizer extends AbstractTokenAuthorizer {
	
	private static final String TOKEN_PART_SEPARATOR = "_";
	
	@Override
	public String generateToken(String secret) {
		if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("secret cannot be empty");
		long time = System.currentTimeMillis();
		String token = tokenFromSecretAndTime(secret, time);
		return token;
	}

	@Override
	public boolean isValidToken(String token, String secret) {
		if (token == null || token.isEmpty()) return false;
		if (secret == null || secret.isEmpty()) return false;
		long tokenTime = getTimeFromToken(token);
		String reconstructedToken = tokenFromSecretAndTime(secret, tokenTime);
		if (!token.equals(reconstructedToken)) {
			// input token was not constructed using the same secret
			return false;
		}
		long currentTime = System.currentTimeMillis();
		if (Math.abs(currentTime - tokenTime) > getMaxAllowedTimeDifference()) {
			// validity of input token has expired
			return false;
		}
		return true;
	}
    
    protected String getHashAlgorithm() {
    	return "SHA-256";
    }
    
    /**
     * Returns maximum allowed difference between time of token generation
     * and time of token validation in milliseconds.
     * @return
     */
    protected long getMaxAllowedTimeDifference() {
    	return 6 * 60 * 60 * 1000; // 6 h
    }
    
    String tokenFromSecretAndTime(String secret, long time) {
		String payload = secret + time;
		String token = SecurityUtils.hash(payload, getHashAlgorithm()) + TOKEN_PART_SEPARATOR + time;
		return token;
	}
    
    private long getTimeFromToken(String token) {
    	long time = 0L;
    	if (token == null || token.isEmpty()) return time;
    	int idxOfSep = token.lastIndexOf(TOKEN_PART_SEPARATOR);
    	if (idxOfSep >= 0) {
    		String timeStr = token.substring(idxOfSep + TOKEN_PART_SEPARATOR.length());
    		if (timeStr != null && !timeStr.isEmpty()) {
    			try {
    				time = Long.valueOf(timeStr).longValue();
    			} catch (NumberFormatException ignored) {
    				// ignored
    			}
    		}
    	}
    	return time;
    }
}
