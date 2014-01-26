package org.twinstone.formio.binding;

import java.lang.annotation.Annotation;

/**
 * Resolves name of argument using {@link ArgumentName} annotation.
 * @author Radek Beran
 */
public class DefaultAnnotationArgumentNameResolver implements ArgumentNameResolver {

	@Override
	public String getArgumentName(Class<?> argType, Annotation[] argAnnotations) {
		// for each annotation of argument
		String argName = null;
		for (int j = 0; j < argAnnotations.length; j++) {
			Annotation a = argAnnotations[j];
			if (a instanceof ArgumentName) {
				argName = ((ArgumentName) a).value();
				// ArgumentName annotation with argName found
				break;
			}
		}
		return argName;
	}

}
