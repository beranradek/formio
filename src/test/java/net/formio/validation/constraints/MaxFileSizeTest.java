package net.formio.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Karel Stefan
 */
public class MaxFileSizeTest {

	@Test
	public void testMaxFileSize() {
		long b = 1024L;
		assertFalse("should not be valid", MaxFileSizeValidation.isValid(30, "11B"));
		assertFalse("should not be valid", MaxFileSizeValidation.isValid(11 * b + 1, "11KB"));
		assertFalse("should not be valid", MaxFileSizeValidation.isValid(11 * b * b + 1, "11MB"));
		assertFalse("should not be valid", MaxFileSizeValidation.isValid(11 * b * b * b + 1, "11GB"));

		assertTrue("should be valid", MaxFileSizeValidation.isValid(10, "11.2B"));
		assertTrue("should be valid", MaxFileSizeValidation.isValid(10 * b, "11.23KB"));
		assertTrue("should be valid", MaxFileSizeValidation.isValid(15, "11.23MB"));
		assertTrue("should be valid", MaxFileSizeValidation.isValid(0, "11GB"));
		assertTrue("should be valid", MaxFileSizeValidation.isValid(5 * b, "5KB"));
		assertTrue("should be valid", MaxFileSizeValidation.isValid(1, "1B"));

		assertParseException(213, "");
		assertParseException(213, null);
		assertParseException(213, "fdf");
		assertParseException(213, "1");
		assertParseException(213, "1.2");
		assertParseException(213, "1.2GBx");
	}

	private void assertParseException(long fileSize, String maxFileSize) {
		try {
			MaxFileSizeValidation.isValid(fileSize, maxFileSize);
			fail("should throw parse exception");
		} catch (IllegalArgumentException e) {
			// ignored
		}
	}
}
