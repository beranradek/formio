package net.formio.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation defining validation so that at least one field
 * from field list must not be empty.
 * 
 * @author Petr Kalivoda
 *
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = AnyNotEmptyValidator.class)
@Documented
public @interface AnyNotEmpty {
	
	public static final String MESSAGE = "{constraints.AnyNotEmpty.message}";
	
	/**
	 * @return The error message template.
	 */
	String message() default MESSAGE;

	/**
	 * @return The groups the constraint belongs to.
	 */
	Class<?>[] groups() default { };

	/**
	 * @return The payload associated to the constraint
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * @return Fields one of which must not be empty.
	 */
	String[] fields();

	/**
	 * Defines several <code>@AnyNotEmpty</code> annotations on the same element
	 * 
	 * @see AnyNotEmpty
	 */
	@Target({ TYPE, ANNOTATION_TYPE })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		AnyNotEmpty[] value();
	}
}
