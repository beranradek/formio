package org.twinstone.formio.binding;

import java.lang.annotation.Annotation;

/**
 * Resolves name of argument from given argument type and its annotations.
 * @author Radek Beran
 */
public interface ArgumentNameResolver {
	String getArgumentName(Class<?> argType, Annotation[] argAnnotations);
}
