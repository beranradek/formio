package org.twinstone.formio.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Payload;

public enum Severity implements Payload {

	INFO,
	WARNING,
	ERROR;
	
	public static Severity fromViolation(ConstraintViolation<?> violation) {
		// Severity from violation
		// According to bean validation spec http://beanvalidation.org/1.1/spec/,
		// severity can be introduced as custom payload
		Severity sev = Severity.ERROR;
		if (violation.getConstraintDescriptor() != null && violation.getConstraintDescriptor().getPayload() != null) {
			Object payload = violation.getConstraintDescriptor().getPayload();
			if (payload instanceof Set) {
				Set<Object> payloadSet = (Set<Object>)payload;
				if (payloadSet.contains(SeverityPayload.Info.class)) {
					sev = INFO;
				} else if (payloadSet.contains(SeverityPayload.Warning.class)) {
					sev = WARNING;
				}
			}
		}
		return sev;
	}
}
