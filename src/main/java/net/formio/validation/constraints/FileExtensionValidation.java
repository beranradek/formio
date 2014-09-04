package net.formio.validation.constraints;

import java.util.Arrays;

/**
 * Validation of file extension.
 *
 * @author Karel Stefan
 */
public class FileExtensionValidation {

	private FileExtensionValidation() {
		throw new AssertionError("Not instantiable, use static members");
	}

	public static boolean hasFileExtension(String fileName, String[] allowedExtensions, boolean ignoreCase) {
		if (fileName == null || fileName.isEmpty() || allowedExtensions == null) {
			return false;
		}
		String extension = getFileExtension(fileName);
		if (ignoreCase) {
			extension = extension.toLowerCase();
			allowedExtensions = convertExtensionsToLowerCase(allowedExtensions);
		}
		return Arrays.asList(allowedExtensions).contains(extension);
	}

	public static boolean hasFileExtension(String fileName, String[] allowedExtensions) {
		return hasFileExtension(fileName, allowedExtensions, true);
	}

	private static String getFileExtension(String fileName) {
		int index = fileName.lastIndexOf('.') + 1;
		if (index == 0 || index == fileName.length()) {
			// File without extension e.g. "test" or "."
			return "";
		}
		return fileName.substring(index);
	}

	private static String[] convertExtensionsToLowerCase(String[] extensions) {
		String[] converted = new String[extensions.length];
		for (int i = 0; i < extensions.length; i++) {
			converted[i] = extensions[i] != null ? extensions[i].toLowerCase() : null;
		}
		return converted;
	}

}
