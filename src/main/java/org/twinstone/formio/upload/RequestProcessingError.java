package org.twinstone.formio.upload;

import java.io.Serializable;

import org.twinstone.formio.validation.DefaultInterpolatedMessage;

/**
 * Failure while processing the request.
 * 
 * @author Radek Beran
 */
public class RequestProcessingError extends DefaultInterpolatedMessage implements Serializable {
	private static final long serialVersionUID = -4738039542326084798L;

	private final String causeMessage;
	private final Throwable cause;

	public RequestProcessingError(String causeMessage, Throwable cause) {
		this.causeMessage = causeMessage;
		this.cause = cause;
	}

	public RequestProcessingError(String message) {
		this(message, null);
	}

	/**
	 * Original error message.
	 * @return
	 */
	public String getCauseMessage() {
		return causeMessage;
	}

	/**
	 * Original cause.
	 * @return
	 */
	public Throwable getCause() {
		return cause;
	}

}
