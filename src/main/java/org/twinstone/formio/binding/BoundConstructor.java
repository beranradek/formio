package org.twinstone.formio.binding;

import java.lang.reflect.Constructor;
import java.util.List;

final class BoundConstructor<T> {

	private final Constructor<T> constructor;
	private final List<String> argNames;
	private final Class<?>[] argTypes;

	BoundConstructor(final Constructor<T> constructor, final List<String> argNames, final Class<?>[] argTypes) {
		if (constructor == null) throw new IllegalArgumentException("constructor cannot be null");
		if (argNames == null) throw new IllegalArgumentException("argNames cannot be null");
		this.constructor = constructor;
		this.argNames = argNames;
		this.argTypes = argTypes;
	}

	public Constructor<T> getConstructor() {
		return constructor;
	}

	public List<String> getArgNames() {
		return argNames;
	}
	
	public Class<?>[] getArgTypes() {
		return argTypes;
	}

}
