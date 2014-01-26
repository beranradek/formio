package org.twinstone.formio.binding;

import java.util.Collections;
import java.util.List;

class ParsedValue {

	private final Object value; // can be also collection of values
	private final List<ParseError> parseErrors;
	
	public ParsedValue(Object value, List<ParseError> parseErrors) {
		this.value = value;
		this.parseErrors = parseErrors != null ? parseErrors : Collections.<ParseError>emptyList();
	}

	public Object getValue() {
		return value;
	}

	public List<ParseError> getParseErrors() {
		return parseErrors;
	}
	
	public boolean isSuccessfullyParsed() {
		return parseErrors.isEmpty();
	}
}
