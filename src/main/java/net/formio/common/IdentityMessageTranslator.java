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
package net.formio.common;

import java.util.Locale;

/**
 * <p>Returns translation keys back without any translation.</p>
 * <ul>
 * 	<li>Thread-safe: Immutable
 * </ul>
 * @author Radek Beran
 */
public class IdentityMessageTranslator implements MessageTranslator {
	
	/**
	 * Returns translation of the message for given message key, locale and arguments.
	 * @param msgKey
	 * @param locale
	 * @param args
	 * @return
	 */
	@Override
	public String getMessage(String msgKey, Locale locale, Object ... args) {
		return msgKey;
	}
	
	/**
	 * Returns translation of the message for given message key and arguments.
	 * @param msgKey
	 * @param args
	 * @return
	 */
	@Override
	public String getMessage(String msgKey, Object ... args) {
		return msgKey;
	}
}