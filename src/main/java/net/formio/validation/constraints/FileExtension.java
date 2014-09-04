package net.formio.validation.constraints;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.validation.Constraint;
import javax.validation.Payload;

import net.formio.upload.UploadedFile;

/**
 * Asserts that the file name has allowed extension.
 * Annotation is applicable to {@link String}, {@link UploadedFile}.
 *
 * @author Karel Stefan
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = FileExtensionConstraintValidator.class)
public @interface FileExtension {

	/**
	 * @return The error message template.
	 */
	String message() default "{constraints.FileExtension.message}";

	/**
	 * @return The groups the constraint belongs to.
	 */
	Class<?>[] groups() default { };

	/**
	 * @return The payload associated to the constraint
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * @return The allowed file extensions.
	 */
	String[] extensions() default {};

	/**
	 * @return True if case is ignored.
	 */
	boolean ignoreCase() default true;

	/**
	 * Defines several {@link FileExtension} annotations on the same element.
	 *
	 * @see DecimalMin
	 */
	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
	@Retention(RUNTIME)
	@Documented
	@interface List {

		FileExtension[] extensions();
	}
}
