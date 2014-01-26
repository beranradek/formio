package org.twinstone.formio.validation;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.MessageInterpolator;

/**
 * Resource bundle backed message interpolator with
 * explicit passing of default locale.
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 * @author Gunnar Morling
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 * @author Radek Beran (explicit passing of default locale, independent on hibernate classes)
 */
public class ResBundleMessageInterpolator implements MessageInterpolator {

	/**
	 * The name of the default message bundle.
	 */
	public static final String DEFAULT_VALIDATION_MESSAGES = "ValidationMessages";

	/**
	 * Regular expression used to do message interpolation.
	 */
	private static final Pattern MESSAGE_PARAMETER_PATTERN = Pattern.compile( "(\\{[^\\}]+?\\})" );

	/**
	 * Default locale.
	 */
	private final Locale defaultLocale;

	/**
	 * Loads user-specified resource bundles.
	 */
	private final ResBundleLocator userResourceBundleLocator;

	/**
	 * Loads built-in resource bundles.
	 */
	private final ResBundleLocator defaultResourceBundleLocator;

	/**
	 * Step 1-3 of message interpolation can be cached. We do this in this map.
	 */
	private final ConcurrentMap<LocalisedMessage, String> resolvedMessages = new ConcurrentHashMap<LocalisedMessage, String>();

	/**
	 * Flag indicating whether this interpolator should chance some of the interpolation steps.
	 */
	private final boolean cacheMessages;

	public ResBundleMessageInterpolator(ResBundleLocator userResourceBundleLocator, Locale defaultLocale) {
		this(userResourceBundleLocator, defaultLocale, true);
	}

	public ResBundleMessageInterpolator(ResBundleLocator userResourceBundleLocator, Locale defaultLocale, boolean cacheMessages) {
		if (userResourceBundleLocator == null) throw new IllegalArgumentException("userResourceBundleLocator cannot be null");
		if (defaultLocale == null) throw new IllegalArgumentException("default locale cannot be null");
		this.defaultLocale = defaultLocale;
		this.userResourceBundleLocator = userResourceBundleLocator;
		this.defaultResourceBundleLocator = new PlatformResBundleLocator( DEFAULT_VALIDATION_MESSAGES );
		this.cacheMessages = cacheMessages;
	}

	@Override
	public String interpolate(String message, Context context) {
		// probably no need for caching, but it could be done by parameters since the map
		// is immutable and uniquely built per Validation definition, the comparison has to be based on == and not equals though
		return interpolateMessage( message, context.getConstraintDescriptor().getAttributes(), defaultLocale );
	}

	@Override
	public String interpolate(String message, Context context, Locale locale) {
		return interpolateMessage( message, context.getConstraintDescriptor().getAttributes(), locale );
	}

	/**
	 * Runs the message interpolation according to algorithm specified in JSR 303.
	 * <br/>
	 * Note:
	 * <br/>
	 * Look-ups in user bundles is recursive whereas look-ups in default bundle are not!
	 *
	 * @param message the message to interpolate
	 * @param annotationParameters the parameters of the annotation for which to interpolate this message
	 * @param locale the {@code Locale} to use for the resource bundle.
	 *
	 * @return the interpolated message or given message unresolved.
	 */
	protected String interpolateMessage(String message, Map<String, Object> annotationParameters, Locale locale) {
		LocalisedMessage localisedMessage = new LocalisedMessage( message, locale );
		String resolvedMessage = null;

		if ( cacheMessages ) {
			resolvedMessage = resolvedMessages.get( localisedMessage );
		}

		// if the message is not already in the cache we have to run step 1-3 of the message resolution 
		if ( resolvedMessage == null ) {
			ResourceBundle userResourceBundle = tryGetResourceBundle(this.userResourceBundleLocator, locale);
			ResourceBundle defaultResourceBundle = tryGetResourceBundle(this.defaultResourceBundleLocator, locale);

			String userBundleResolvedMessage;
			resolvedMessage = message;
			boolean evaluatedDefaultBundleOnce = false;
			do {
				// search the user bundle recursive (step1)
				userBundleResolvedMessage = replaceVariables(
						resolvedMessage, userResourceBundle, locale, true
				);

				// exit condition - we have at least tried to validate against the default bundle and there was no
				// further replacements
				if ( evaluatedDefaultBundleOnce
						&& !hasReplacementTakenPlace( userBundleResolvedMessage, resolvedMessage ) ) {
					break;
				}

				// search the default bundle non recursive (step2)
				resolvedMessage = replaceVariables( userBundleResolvedMessage, defaultResourceBundle, locale, false );
				evaluatedDefaultBundleOnce = true;
			} while ( true );
		}

		// cache resolved message
		if (cacheMessages) {
			String cachedResolvedMessage = resolvedMessages.putIfAbsent( localisedMessage, resolvedMessage );
			if ( cachedResolvedMessage != null ) {
				resolvedMessage = cachedResolvedMessage;
			}
		}

		// resolve annotation attributes (step 4)
		resolvedMessage = replaceAnnotationAttributes( resolvedMessage, annotationParameters );

		// last but not least we have to take care of escaped literals
		resolvedMessage = resolvedMessage.replace( "\\{", "{" );
		resolvedMessage = resolvedMessage.replace( "\\}", "}" );
		resolvedMessage = resolvedMessage.replace( "\\\\", "\\" );
		
		return resolvedMessage;
	}
	
	private ResourceBundle tryGetResourceBundle(ResBundleLocator locator, Locale locale) {
		ResourceBundle rb = null;
		try {
			rb = locator.getResourceBundle(locale);
		} catch (MissingResourceException ex) {
			// ignored
			// TODO: Log missing resource bundle
		}
		return rb;
	}

	private boolean hasReplacementTakenPlace(String origMessage, String newMessage) {
		if (origMessage == null) return false;
		return !origMessage.equals( newMessage );
	}

	private String replaceVariables(String message, ResourceBundle bundle, Locale locale, boolean recurse) {
		if (bundle == null) return message;
		Matcher matcher = MESSAGE_PARAMETER_PATTERN.matcher( message );
		StringBuffer sb = new StringBuffer();
		String resolvedParameterValue;
		while ( matcher.find() ) {
			String parameter = matcher.group( 1 );
			resolvedParameterValue = resolveParameter(
					parameter, bundle, locale, recurse
			);

			matcher.appendReplacement( sb, Matcher.quoteReplacement( resolvedParameterValue ) );
		}
		matcher.appendTail( sb );
		return sb.toString();
	}

	private String replaceAnnotationAttributes(String message, Map<String, Object> annotationParameters) {
		if (message == null) return null;
		Matcher matcher = MESSAGE_PARAMETER_PATTERN.matcher( message );
		StringBuffer sb = new StringBuffer();
		while ( matcher.find() ) {
			String resolvedParameterValue;
			String parameter = matcher.group( 1 );
			Object variable = annotationParameters.get( removeCurlyBrace( parameter ) );
			if ( variable != null ) {
				if ( variable.getClass().isArray() ) {
					resolvedParameterValue = Arrays.toString( (Object[]) variable );
				}
				else {
					resolvedParameterValue = variable.toString();
				}
			}
			else {
				resolvedParameterValue = parameter;
			}
			resolvedParameterValue = Matcher.quoteReplacement( resolvedParameterValue );
			matcher.appendReplacement( sb, resolvedParameterValue );
		}
		matcher.appendTail( sb );
		return sb.toString();
	}

	private String resolveParameter(String parameterName, ResourceBundle bundle, Locale locale, boolean recurse) {
		String parameterValue;
		try {
			if ( bundle != null ) {
				parameterValue = bundle.getString( removeCurlyBrace( parameterName ) );
				if ( recurse ) {
					parameterValue = replaceVariables( parameterValue, bundle, locale, recurse );
				}
			}
			else {
				parameterValue = parameterName;
			}
		}
		catch ( MissingResourceException e ) {
			// return parameter itself
			parameterValue = parameterName;
		}
		return parameterValue;
	}

	private String removeCurlyBrace(String parameter) {
		return parameter.substring( 1, parameter.length() - 1 );
	}

	private static class LocalisedMessage {
		private final String message;
		private final Locale locale;

		LocalisedMessage(String message, Locale locale) {
			this.message = message;
			this.locale = locale;
		}

		@Override
		public boolean equals(Object o) {
			if ( this == o ) {
				return true;
			}
			if ( o == null || getClass() != o.getClass() ) {
				return false;
			}

			LocalisedMessage that = (LocalisedMessage) o;

			if ( locale != null ? !locale.equals( that.locale ) : that.locale != null ) {
				return false;
			}
			if ( message != null ? !message.equals( that.message ) : that.message != null ) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = message != null ? message.hashCode() : 0;
			result = 31 * result + ( locale != null ? locale.hashCode() : 0 );
			return result;
		}
	}
}
