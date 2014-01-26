package org.twinstone.formio;

import java.util.Locale;

import javax.validation.Validation;

import org.twinstone.formio.binding.ArgumentNameResolver;
import org.twinstone.formio.binding.BeanExtractor;
import org.twinstone.formio.binding.Binder;
import org.twinstone.formio.binding.DefaultAnnotationArgumentNameResolver;
import org.twinstone.formio.binding.DefaultBeanExtractor;
import org.twinstone.formio.binding.DefaultBinder;
import org.twinstone.formio.binding.PropertyMethodRegex;
import org.twinstone.formio.binding.collection.BasicCollectionBuilders;
import org.twinstone.formio.binding.collection.CollectionBuilders;
import org.twinstone.formio.text.BasicFormatters;
import org.twinstone.formio.text.Formatters;
import org.twinstone.formio.validation.BeanValidator;
import org.twinstone.formio.validation.ValidationApiBeanValidator;

/**
 * Configuration of form. Immutable.
 * @author Radek Beran
 */
public class Config {
	
	private final Locale locale;
	private final String messageBundleName;
	private final Formatters formatters;
	private final CollectionBuilders collectionBuilders;
	private final ArgumentNameResolver argumentNameResolver;
	private final BeanExtractor beanExtractor;
	private final Binder binder;
	private final BeanValidator beanValidator;
	private final boolean inputTrimmed;
	
	Config(Builder builder) {
		this.locale = builder.locale;
		this.messageBundleName = builder.messageBundleName;
		this.formatters = builder.formatters;
		this.collectionBuilders = builder.collectionBuilders;
		this.argumentNameResolver = builder.argumentNameResolver;
		this.beanExtractor = builder.beanExtractor;
		this.binder = builder.binder;
		this.beanValidator = builder.beanValidator;
		this.inputTrimmed = builder.inputTrimmed;
	}
	
	public static class Builder {
		// Optional parameters - initialized to default values (these are only here in a single location)
		Locale locale;
		String messageBundleName;
		Formatters formatters;
		CollectionBuilders collectionBuilders;
		ArgumentNameResolver argumentNameResolver;
		PropertyMethodRegex accessorRegex;
		PropertyMethodRegex setterRegex;
		BeanExtractor beanExtractor;
		Binder binder;
		BeanValidator beanValidator;
		boolean extractorSpecified;
		boolean binderSpecified;
		boolean formattersSpecified;
		boolean validatorSpecified;
		boolean inputTrimmed = true;

		Builder() {
			// package-default access so only Forms (and classes in current package) can create the builder
		}
		
		public Builder locale(Locale locale) {
			if (this.formattersSpecified) throw new IllegalStateException("locale must be specified before the formatters.");
			if (this.validatorSpecified) throw new IllegalStateException("locale must be specified before the validator.");
			this.locale = locale;
			return this;
		}
		
		public Builder messageBundleName(String msgBundleName) {
			if (this.validatorSpecified) throw new IllegalStateException("messageBundleName must be specified before the validator.");
			this.messageBundleName = msgBundleName;
			return this;
		}

		public Builder formatters(Formatters formatters) {
			this.formatters = formatters;
			this.formattersSpecified = true;
			return this;
		}
		
		public Builder collectionBuilders(CollectionBuilders collectionBuilders) {
			this.collectionBuilders = collectionBuilders;
			return this;
		}
		
		public Builder accessorRegex(PropertyMethodRegex accessorRegex) {
			if (this.extractorSpecified) throw new IllegalStateException("accessorRegex must be specified before the extractor.");
			this.accessorRegex = accessorRegex;
			return this;
		}
		
		public Builder setterRegex(PropertyMethodRegex setterRegex) {
			if (this.binderSpecified) throw new IllegalStateException("setterRegex must be specified before the binder.");
			this.setterRegex = setterRegex;
			return this;
		}
		
		public Builder beanExtractor(BeanExtractor beanExtractor) {
			this.beanExtractor = beanExtractor;
			this.extractorSpecified = true;
			return this;
		}

		public Builder binder(Binder binder) {
			this.binder = binder;
			this.binderSpecified = true;
			return this;
		}

		public Builder argumentNameResolver(ArgumentNameResolver argumentNameResolver) {
			this.argumentNameResolver = argumentNameResolver;
			return this;
		}
		
		public Builder beanValidator(BeanValidator beanValidator) {
			this.beanValidator = beanValidator;
			this.validatorSpecified = true;
			return this;
		}
		
		public Builder inputTrimmed(boolean inputTrimmed) {
			this.inputTrimmed = inputTrimmed;
			return this;
		}
		
		public Config build() {
			if (this.locale == null) this.locale = defaultLocale;
			if (this.messageBundleName == null) this.messageBundleName = defaultMessageBundleName;
			if (this.formatters == null) this.formatters = defaultFormatters(this.locale);
			if (this.collectionBuilders == null) this.collectionBuilders = defaultCollectionBuilders;
			if (this.argumentNameResolver == null) this.argumentNameResolver = defaultArgumentNameResolver;
			if (this.accessorRegex == null) this.accessorRegex = DefaultBeanExtractor.defaultAccessorRegex;
			if (this.setterRegex == null) this.setterRegex = DefaultBinder.defaultSetterRegex;
			if (this.beanExtractor == null) this.beanExtractor = defaultBeanExtractor(this.accessorRegex);
			if (this.binder == null) this.binder = new DefaultBinder(this.formatters, this.collectionBuilders, this.argumentNameResolver, this.setterRegex);
			if (this.beanValidator == null) this.beanValidator = new ValidationApiBeanValidator(Validation.buildDefaultValidatorFactory(), this.messageBundleName, this.locale);
			
			Config cfg = new Config(this);
			if (cfg.getLocale() == null) throw new IllegalStateException("locale cannot be null");
			if (cfg.getMessageBundleName() == null) throw new IllegalStateException("message bundle name cannot be null");
			if (cfg.getFormatters() == null) throw new IllegalStateException("formatters cannot be null");
			if (cfg.getCollectionBuilders() == null) throw new IllegalStateException("collectionBuilders cannot be null");
			if (cfg.getArgumentNameResolver() == null) throw new IllegalStateException("argumentNameResolver cannot be null");
			if (cfg.getBeanExtractor() == null) throw new IllegalStateException("beanExtractor cannot be null");
			if (cfg.getBinder() == null) throw new IllegalStateException("binder cannot be null");
			if (cfg.getBeanValidator() == null) throw new IllegalStateException("beanValidator cannot be null");
			return cfg;
		}
		
		private static Formatters defaultFormatters(Locale locale) {
			return new BasicFormatters(locale);
		}
		private static final Locale defaultLocale = Locale.getDefault();
		private static final String defaultMessageBundleName = "ValidationMessages";
		private static final CollectionBuilders defaultCollectionBuilders = new BasicCollectionBuilders();
		private static final ArgumentNameResolver defaultArgumentNameResolver = new DefaultAnnotationArgumentNameResolver();
		private static BeanExtractor defaultBeanExtractor(PropertyMethodRegex accessorRegex) {
			return new DefaultBeanExtractor(accessorRegex);
		}
	}

	public Locale getLocale() {
		return locale;
	}

	public String getMessageBundleName() {
		return messageBundleName;
	}

	public Formatters getFormatters() {
		return formatters;
	}

	public CollectionBuilders getCollectionBuilders() {
		return collectionBuilders;
	}

	public ArgumentNameResolver getArgumentNameResolver() {
		return argumentNameResolver;
	}

	public BeanExtractor getBeanExtractor() {
		return beanExtractor;
	}

	public Binder getBinder() {
		return binder;
	}

	public BeanValidator getBeanValidator() {
		return beanValidator;
	}

	/**
	 * Returns true if text from the form inputs is trimmed before binding to form data.
	 * @return
	 */
	public boolean isInputTrimmed() {
		return inputTrimmed;
	}
	
}
