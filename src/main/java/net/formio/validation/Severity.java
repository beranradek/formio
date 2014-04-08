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

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Payload;

public enum Severity implements Payload {

	// Must be ordered from the least to most severe errors
	INFO("info"),
	WARNING("warning"),
	ERROR("error");
	
	private final String styleClass;
	
	private Severity(String styleClass) {
		this.styleClass = styleClass;
	}
	
	public String getStyleClass() {
		return styleClass;
	}

	public static Severity fromViolation(ConstraintViolation<?> violation) {
		// Severity from violation
		// According to bean validation spec http://beanvalidation.org/1.1/spec/,
		// severity can be introduced as custom payload
		Severity sev = Severity.ERROR;
		if (violation.getConstraintDescriptor() != null && violation.getConstraintDescriptor().getPayload() != null) {
			Set<Class<? extends Payload>> payload = violation.getConstraintDescriptor().getPayload();
			if (payload != null) {
				if (payload.contains(SeverityPayload.Info.class)) {
					sev = INFO;
				} else if (payload.contains(SeverityPayload.Warning.class)) {
					sev = WARNING;
				}
			}
		}
		return sev;
	}
}
