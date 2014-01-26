package org.twinstone.formio.binding;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FilledData<T> {

	private final T data;
	private final Map<String, List<ParseError>> propertyBindErrors;

	public FilledData(T data,
			Map<String, List<ParseError>> propertyBindErrors) {
		this.data = data;
		this.propertyBindErrors = propertyBindErrors != null ? propertyBindErrors : Collections.<String, List<ParseError>>emptyMap();
	}

	public T getData() {
		return data;
	}

	public Map<String, List<ParseError>> getPropertyBindErrors() {
		return propertyBindErrors;
	}
	
	public boolean isSuccessfullyBound() {
		return propertyBindErrors.isEmpty();
	}

}
