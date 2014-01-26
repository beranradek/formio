package org.twinstone.formio.text;

public class FormatterNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -6698070763229313513L;

	private final Class<?> cls;

	public FormatterNotFoundException(Class<?> cls) {
		super("Formatter not found for class " + cls);
		this.cls = cls;
	}

	public Class<?> getCls() {
		return cls;
	}

}