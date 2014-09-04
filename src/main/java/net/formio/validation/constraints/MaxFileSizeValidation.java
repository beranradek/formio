package net.formio.validation.constraints;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Max file size validation
 *
 * @author stefan
 */
public class MaxFileSizeValidation {

	private static final Pattern FILE_SIZE_PATTERN = Pattern.compile("^((\\d+)|(\\d+\\.\\d+))([GMK]?B)$", Pattern.CASE_INSENSITIVE);

	private static enum Unit {
		B(0), KB(1), MB(2), GB(3);

		public final int power;

		private Unit(int power) {
			this.power = power;
		}

	}

	/**
	 * @param fileSize File size in Bytes
	 * @param maxFileSizeStr Max file size e.g. "2MB", "1.2GB"
	 * @return
	 */
	public static boolean isValid(long fileSize, String maxFileSizeStr) {
		long maxFileSize = parseFileSize(maxFileSizeStr);
		return fileSize <= maxFileSize;
	}

	private static long parseFileSize(String value) {
		if (value == null) {
			throw new IllegalArgumentException("Value could not be null.");
		}
		Matcher matcher = FILE_SIZE_PATTERN.matcher(value);
		if (matcher.find()) {
			BigDecimal number = new BigDecimal(matcher.group(1));
			Unit unit = Unit.valueOf(matcher.group(4).toUpperCase());
			return number.multiply(new BigDecimal("1024").pow(unit.power)).longValue();
		}
		throw new IllegalArgumentException("Could not parse max file size.");
	}

}
