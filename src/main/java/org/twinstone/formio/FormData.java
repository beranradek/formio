package org.twinstone.formio;

import org.twinstone.formio.validation.ValidationReport;

/**
 * Edited data and validation report for this data.
 * @author Radek Beran
 *
 * @param <T>
 */
public class FormData<T> {

	private final T data;
	private final ValidationReport validationReport;
	
	public FormData(T data, ValidationReport validationReport) {
		this.data = data;
		this.validationReport = validationReport;
	}

	public T getData() {
		return data;
	}

	public ValidationReport getValidationReport() {
		return validationReport;
	}
	
	/**
	 * Validation was successful, without validation errors.
	 * @return
	 */
	public boolean isValid() {
		return validationReport != null && validationReport.isSuccess();
	}
}
