package org.twinstone.formio.text;

public class StringParseException extends RuntimeException {
	private static final long serialVersionUID = 1407704812024515683L;
	private final Class<?> targetTypeClass;
	private final String parsedString;

	public StringParseException(Class<?> targetTypeClass,
			String parsedString, Throwable cause) {
		super("Error while parsing " + targetTypeClass.getName()
				+ " from String '" + parsedString + "'", cause);
		this.targetTypeClass = targetTypeClass;
		this.parsedString = parsedString;
	}

	public Class<?> getTargetTypeClass() {
		return targetTypeClass;
	}

	public String getParsedString() {
		return parsedString;
	}

}