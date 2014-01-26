package org.twinstone.formio.upload;

/**
 * Maximum size of one uploaded file was exceeded.
 * @author Radek Beran
 */
public class MaxFileSizeExceededError extends MaxSizeExceededError {
	private static final long serialVersionUID = -8869521443566237030L;
	private final String fieldName;

	public MaxFileSizeExceededError(String message, Throwable cause, long currentSize, long maxSize, String fieldName) {
		super(message, cause, currentSize, maxSize);
		this.fieldName = fieldName;
	}

	/**
	 * Name of form field that can be used as the key for validation messages.
	 * @return
	 */
	public String getFieldName() {
		return fieldName;
	}

}
