package net.formio.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Karel Stefan
 */
public class FileExtensionValidationTest {

	@Test
	public void testHasFileExtension() {
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension(null, null));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension("test.jpg", null));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension(null, new String[]{"jpg"}));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension("test.png", new String[]{null}));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension("test", new String[]{"jpg"}));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension("", new String[]{null}));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension("", new String[]{"jpg"}));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension("", new String[]{""}));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension("test.png", new String[]{""}));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension("test.png", new String[0]));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension("test.png", new String[]{"jpg"}));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension("test.png", new String[]{"jpg", "txt"}));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension("test.jpg.png", new String[]{"jpg", "txt"}));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension("test.PNG", new String[]{"jpg", "txt"}, false));
		assertFalse("should not be valid", FileExtensionValidation.hasFileExtension("test.png", new String[]{"JPG", "TXT", ""}, false));

		assertTrue("should be valid", FileExtensionValidation.hasFileExtension("test.png", new String[]{"png"}));
		assertTrue("should be valid", FileExtensionValidation.hasFileExtension("test.png", new String[]{"jpg", "png"}));
		assertTrue("should be valid", FileExtensionValidation.hasFileExtension("test.png", new String[]{"JPG", "PNG"}));
		assertTrue("should be valid", FileExtensionValidation.hasFileExtension("test.png", new String[]{"JPG", "PNG"}, true));
		assertTrue("should be valid", FileExtensionValidation.hasFileExtension("test.PNG", new String[]{"jpg", "png"}));
		assertTrue("should be valid", FileExtensionValidation.hasFileExtension("test", new String[]{"", "png"}));
		assertTrue("should be valid", FileExtensionValidation.hasFileExtension(".", new String[]{"", "png"}));
	}

}

