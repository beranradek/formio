package org.twinstone.formio.validation;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Locates a resource bundle for given locale.
 * @author Radek Beran
 */
public interface ResBundleLocator {

	/**
	 * Returns a resource bundle for the given locale.
	 * @param locale A locale, for which a resource bundle shall be retrieved. Must not be null.
	 * @return A resource bundle for the given locale. May be null, if no such bundle exists.
	 */
	ResourceBundle getResourceBundle(Locale locale);
}