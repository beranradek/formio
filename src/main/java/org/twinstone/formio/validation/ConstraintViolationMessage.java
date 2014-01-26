/*
 * Created on 16.2.2011
 *
 * Copyright (c) 2011 Et netera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package org.twinstone.formio.validation;

import java.io.Serializable;

/**
 * Message carrying result from bean validation. Immutable.
 * 
 * @author Radek Beran
 */
public class ConstraintViolationMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Severity severity;
	private final String text;

	public ConstraintViolationMessage(Severity severity, String text) {
		this.severity = severity;
		this.text = text;
	}

	public Severity getSeverity() {
		return severity;
	}

	public String getText() {
		return text;
	}

	public ConstraintViolationMessage copy() {
		return new ConstraintViolationMessage(getSeverity(), getText());
	}
}
