package org.twinstone.formio;

/**
 * API for form definition and processing.
 * @author Radek Beran
 */
public class Forms {
	
	/**
	 * Separator of parts in the path (used in fully qualified field name).
	 */
	public static final String PATH_SEP = ".";

	/**
	 * Starts building basic form providing simple processing and value binding.
	 * @return
	 */
	public static <T> BasicFormMappingBuilder<T> basic(Class<T> editedObjectClass, String formName) {
		return new BasicFormMappingBuilder<T>(editedObjectClass, formName);
	}
	
	/**
	 * Creates configuration for form processing.
	 * @return
	 */
	public static Config.Builder config() {
		return new Config.Builder();
	}
	
	private Forms() {
		throw new AssertionError("Not instantiable, use static members.");
	}
}
