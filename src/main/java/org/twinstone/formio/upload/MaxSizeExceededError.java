package org.twinstone.formio.upload;

import java.util.HashMap;
import java.util.Map;

/**
 * Some maximum allowed size was exceeded.
 * @author Radek Beran
 */
public class MaxSizeExceededError extends RequestProcessingError {
	private static final long serialVersionUID = -887092648112789389L;
	private final long currentSize;
	private final long maxSize;

	public MaxSizeExceededError(String message, Throwable cause, long currentSize, long maxSize) {
		super(message, cause);
		this.currentSize = currentSize;
		this.maxSize = maxSize;
	}
	
	/**
	 * Message parameters for translation file.
	 * @return
	 */
	@Override
	public Map<String, Object> getMessageParameters() {
		Map<String, Object> params = new HashMap<String, Object>();
		// TODO: Human representation of sizes (bytes are not suitable in all cases):
		params.put("currentSize", getCurrentSize() + " B");
		params.put("maxSize", getMaxSize() + " B");
		return params;
	}

	/**
	 * Actual size.
	 * @return
	 */
	public long getCurrentSize() {
		return currentSize;
	}

	/**
	 * Maximum allowed size.
	 * @return
	 */
	public long getMaxSize() {
		return maxSize;
	}

}