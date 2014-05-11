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

import java.security.MessageDigest;

/**
 * Security utilities.
 * @author Radek Beran
 */
public final class SecurityUtils {
	
	private static final char[] HEX_CHARS = {
		'0', '1', '2', '3',
		'4', '5', '6', '7',
		'8', '9', 'a', 'b',
		'c', 'd', 'e', 'f',};

	/**
	 * Computes hash using the given algorithm. 
	 * Hash in bytes is converted to hex format and returned.
	 * @param str hashed string
	 * @param algorithmName name of hash algorithm - for e.g.: SHA-512, MD5 
	 * @return
	 */
    public static String hash(String str, String algorithmName) throws SecurityException {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithmName);
            md.update(str.getBytes("UTF-8"));
            byte[] bytes = md.digest();
            return asHex(bytes);
        } catch (Exception ex) {
            throw new SecurityException(ex.getMessage(), ex);
        }
    }
    
    /**
     * Turns array of bytes into string representing each byte as
     * unsigned hex number.
     * 
     * @param hash array of bytes to convert to hex-string
     * @return generated hex string
     */
    private static String asHex(byte hash[]) {
        char buf[] = new char[hash.length * 2];
        for (int i = 0, x = 0; i < hash.length; i++) {
            buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
            buf[x++] = HEX_CHARS[hash[i] & 0xf];
        }
        return String.valueOf(buf);
    }
	
	private SecurityUtils() {
	}
}
