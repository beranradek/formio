package net.formio.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.formio.upload.UploadedFile;

/**
 * Validator for {@link FileExtension}.
 *
 * @author Karel Stefan
 */
public class FileExtensionConstraintValidator implements ConstraintValidator<FileExtension, Object> {

	private String[] allowedExtensions;

	private boolean ignoreCase;

	@Override
	public void initialize(FileExtension annotation) {
		allowedExtensions = annotation.value();
		ignoreCase = annotation.ignoreCase();
	}

	@Override
	public boolean isValid(Object input, ConstraintValidatorContext ignored) {
		if (input == null) {
			 return true;
		} else if (input instanceof String) {
			return isValidStr((String) input);
		} else if (input instanceof UploadedFile) {
			return isValidStr(((UploadedFile) input).getFileName());
		}
		throw new IllegalStateException("FileExtensionValidator is not applicable to the field type " + getClass().getName());
	}
	
	protected String[] getAllowedExtensions() {
		return allowedExtensions;
	}

	protected boolean isIgnoreCase() {
		return ignoreCase;
	}

	private boolean isValidStr(String input) {
		if (input.isEmpty()) {
			return true;
		}
		return FileExtensionValidation.hasFileExtension(input, getAllowedExtensions(), isIgnoreCase());
	}

}
