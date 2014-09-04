package net.formio.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.formio.upload.UploadedFile;

/**
 * Validator for {@link MaxFileSize}.
 *
 * @author Karel Stefan
 */
public class MaxFileSizeConstraintValidator implements ConstraintValidator<MaxFileSize, UploadedFile> {

	/** Max file size string e.g. "1MB", "1.2GB" */
	private String value;

	@Override
	public void initialize(MaxFileSize annotation) {
		value = annotation.value();
	}

	@Override
	public boolean isValid(UploadedFile input, ConstraintValidatorContext ctx) {
		if (input == null) {
			 return true;
		}
		return MaxFileSizeValidation.isValid(input.getSize(), value);
	}

}
