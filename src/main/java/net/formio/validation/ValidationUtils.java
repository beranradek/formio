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
package net.formio.validation;

import javax.validation.ConstraintViolation;

/**
 * Auxiliary common methods for validation.
 * @author Radek Beran
 */
final class ValidationUtils {
	
	static String getMsgText(ConstraintViolation<?> v) {
		String msgText = null;
		if (v.getMessage() != null && !v.getMessage().isEmpty()) {
			msgText = v.getMessage();
		} else {
			msgText = v.getMessageTemplate();
		}
		return removeBraces(msgText);
	}
	
	static String removeBraces(String msgTemplate) {
		if (msgTemplate == null) return null;
		if (msgTemplate.startsWith("{") && msgTemplate.endsWith("}")) {
			return msgTemplate.substring(1, msgTemplate.length() - 1);
		}
		return msgTemplate;
	}

	static boolean isTopLevelMapping(String propPrefix, String pathSep) {
		return propPrefix == null || propPrefix.isEmpty() || !propPrefix.contains(pathSep);
	}

	private ValidationUtils() {
	}
}
