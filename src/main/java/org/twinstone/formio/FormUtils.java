package org.twinstone.formio;

/**
 * Utility methods for manipulating with forms or customization of forms.
 * @author Radek Beran
 */
public class FormUtils {

	/**
	 * Extracts property name located at the end of full field name (field
	 * name is the whole path, with possible terminating brackets).
	 * @param fieldName
	 * @return
	 */
	public static String fieldNameToPropertyName(String fieldName) {
		if (fieldName == null) return null;
		int lastDot = fieldName.lastIndexOf(Forms.PATH_SEP);
		String propName = fieldName;
		if (lastDot >= 0) {
			propName = fieldName.substring(lastDot + 1);
		}
		return removeTrailingBrackets(propName);
	}
	
	/**
	 * Removes possible brackets at the end of given string that
	 * do not contain any index (for e.g. name[] will be transformed to name).
	 * @param str
	 * @return
	 */
	public static String removeTrailingBrackets(String str) {
		if (str == null) return null;
		String res = str;
		final String bracketsStr = "[]";
		if (res.endsWith(bracketsStr)) {
			res = res.substring(0, res.length() - bracketsStr.length());
		}
		return res;
	}
	
	private FormUtils() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
