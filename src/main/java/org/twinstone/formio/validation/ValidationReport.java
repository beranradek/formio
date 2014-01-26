/*
 * Created on 8.12.2011
 *
 * Copyright (c) 2011 Et netera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */
package org.twinstone.formio.validation;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Report after validating an object. Immutable.
 *
 * @author Radek Beran
 */
public class ValidationReport implements Serializable {
	
	private static final long serialVersionUID = 5798813572430003173L;
	
	private final Map<String, Set<ConstraintViolationMessage>> fieldMessages;
	private final Set<ConstraintViolationMessage> globalMessages;
	
	public ValidationReport(Map<String, Set<ConstraintViolationMessage>> fieldMessages, Set<ConstraintViolationMessage> globalMessages) {
		if (fieldMessages == null) throw new IllegalArgumentException("field messages cannot be null");
		if (globalMessages == null) throw new IllegalArgumentException("global messages cannot be null");
		Map<String, Set<ConstraintViolationMessage>> fieldMsgCopy = new LinkedHashMap<String, Set<ConstraintViolationMessage>>();
		for (Map.Entry<String, Set<ConstraintViolationMessage>> entry : fieldMessages.entrySet()) {
			fieldMsgCopy.put(entry.getKey(), new LinkedHashSet<ConstraintViolationMessage>(entry.getValue()));	
		}
		
		this.fieldMessages = Collections.unmodifiableMap(fieldMsgCopy);
		this.globalMessages = Collections.unmodifiableSet(new LinkedHashSet<ConstraintViolationMessage>(globalMessages));
	}

	/**
	 * Validation was successful, without validation errors (warnings and infos can be present).
	 * @return
	 */
	public boolean isSuccess() {
		boolean errorFound = false;
		if (fieldMessages != null && !fieldMessages.isEmpty()) {
			for (Set<ConstraintViolationMessage> fieldMsgs : fieldMessages.values()) {
				for (ConstraintViolationMessage msg : fieldMsgs) {
					if (msg.getSeverity() == Severity.ERROR) {
						errorFound = true;
						break;
					}
				}
				if (errorFound) break;
			}
		}
		if (globalMessages != null && !globalMessages.isEmpty()) {
			for (ConstraintViolationMessage msg : globalMessages) {
				if (msg.getSeverity() == Severity.ERROR) {
					errorFound = true;
					break;
				}
			}
		}
		return !errorFound;
	}
	
	/**
	 * Returns lists of messages for individual fields.
	 * @return
	 */
	public Map<String, Set<ConstraintViolationMessage>> getFieldMessages() {
		return fieldMessages;
	}
	
	/**
	 * Returns list of global validation messages (not related to one field).
	 * @return
	 */
	public Set<ConstraintViolationMessage> getGlobalMessages() {
		return globalMessages;
	}
	
}
