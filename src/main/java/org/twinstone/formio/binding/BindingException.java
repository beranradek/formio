package org.twinstone.formio.binding;

/**
 * Exception when binding values to new instance of a class.
 * @author Radek Beran
 */
public class BindingException extends RuntimeException {
	private static final long serialVersionUID = 6087854106024183950L;

	public BindingException(String message) {
		super(message);
	}

	public BindingException(String message, Throwable cause) {
		super(message, cause);
	}
}
