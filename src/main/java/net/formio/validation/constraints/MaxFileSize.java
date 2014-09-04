package net.formio.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Asserts that the file size is less than or equal to max file size.
 *
 * @author Karel Stefan
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = MaxFileSizeConstraintValidator.class)
public @interface MaxFileSize {
	/**
	 * @return The error message template.
	 */
	String message() default "{constraints.MaxFileSize.message}";

	/**
	 * @return The groups the constraint belongs to.
	 */
	Class<?>[] groups() default {};

	/**
	 * @return The payload associated to the constraint
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * @return Max file size e.g. "1B", "1.2GB"
	 */
	String value();

	/**
	 * Defines several {@link MaxFileSize} annotations on the same element.
	 *
	 * @see MaxFileSize
	 */
	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
	@Retention(RUNTIME)
	@Documented
	@interface List {

		MaxFileSize[] value();
	}

}