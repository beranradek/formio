package org.twinstone.formio.validation;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Locates resource bundle by given bundle base name.
 * @author Radek Beran
 */
public class PlatformResBundleLocator implements ResBundleLocator {

	private final String bundleName;
	
	public PlatformResBundleLocator(String bundleName) {
		this.bundleName = bundleName;
	}
	
	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		return ResourceBundle.getBundle(bundleName, locale);
	}

}
