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
package net.formio.validation.constraints;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/** 
 * Validation whether string, collection, map or array is NOT {@code null} and NOT empty.
 * 
 * @author Radek Beran
 */
public final class NotEmptyValidation {
	
	public static boolean isNotEmpty(Object input) {
		if (input == null) { 
			return false;
		}
		if (input instanceof String) {
			if (((String)input).isEmpty()) {
				return false;
			}
		} else if (input instanceof Collection) {
			if (((Collection<?>)input).isEmpty()) {
				return false;
			}
		} else if (input instanceof Map) {
			if (((Map<?, ?>)input).isEmpty()) {
				return false;
			}
		} else if (input.getClass().isArray()) {
			if (Array.getLength(input) == 0) {
				return false;
			}
		}
        return true;
	}
	
	private NotEmptyValidation() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
