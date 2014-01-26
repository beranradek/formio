package org.twinstone.formio.upload;

/**
 * Maximum size of the whole request (of all uploaded files) was exceeded.
 * @author Radek Beran
 */
public class MaxRequestSizeExceededError extends MaxSizeExceededError {
	private static final long serialVersionUID = -8869521443566237030L;

	public MaxRequestSizeExceededError(String message, Throwable cause, long currentSize, long maxSize) {
		super(message, cause, currentSize, maxSize);
	}
}