/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;

import net.formio.binding.AnnotationArgumentNameResolver;
import net.formio.binding.ArgumentNameResolver;
import net.formio.binding.BeanExtractor;
import net.formio.binding.Binder;
import net.formio.binding.ConstructorInstantiator;
import net.formio.binding.DefaultBeanExtractor;
import net.formio.binding.DefaultBinder;
import net.formio.binding.Instantiator;
import net.formio.binding.PropertyMethodRegex;
import net.formio.binding.collection.BasicCollectionBuilders;
import net.formio.binding.collection.CollectionBuilders;
import net.formio.binding.collection.CollectionSpec;
import net.formio.binding.collection.ItemsOrder;
import net.formio.format.Location;
import net.formio.format.BasicFormatters;
import net.formio.format.Formatters;
import net.formio.security.HashTokenAuthorizer;
import net.formio.security.TokenAuthorizer;
import net.formio.validation.BeanValidator;
import net.formio.validation.DefaultBeanValidator;

/**
 * Configuration of form. Immutable.
 * @author Radek Beran
 */
public class Config {
	
	/**
	 * Separator of parts in the path (used in fully qualified field name).
	 */
	public static final String DEFAULT_PATH_SEP = "-";
	public static final ValidatorFactory DEFAULT_VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
	
	private final Location location;
	private final String messageBundleName;
	private final Formatters formatters;
	private final CollectionBuilders collectionBuilders;
	private final ArgumentNameResolver argumentNameResolver;
	private final BeanExtractor beanExtractor;
	private final Binder binder;
	private final BeanValidator beanValidator;
	private final TokenAuthorizer tokenAuthorizer;
	private final boolean inputTrimmed;
	private final PropertyMethodRegex accessorRegex;
	private final PropertyMethodRegex setterRegex;
	private final String urlBase;
	private final int colFormWidth;
	private final int colLabelWidth;
	private final int colInputWidth;
	private final Instantiator defaultInstantiator;
	private final CollectionSpec<?> listMappingCollection;
	private final String pathSeparator;
	
	Config(Builder builder) {
		this.location = builder.location;
		this.messageBundleName = builder.messageBundleName;
		this.formatters = builder.formatters;
		this.collectionBuilders = builder.collectionBuilders;
		this.argumentNameResolver = builder.argumentNameResolver;
		this.beanExtractor = builder.beanExtractor;
		this.binder = builder.binder;
		this.beanValidator = builder.beanValidator;
		this.tokenAuthorizer = builder.tokenAuthorizer;
		this.inputTrimmed = builder.inputTrimmed;
		this.accessorRegex = builder.accessorRegex;
		this.setterRegex = builder.setterRegex;
		this.urlBase = builder.urlBase;
		this.colFormWidth = builder.colFormWidth;
		this.colLabelWidth = builder.colLabelWidth;
		this.colInputWidth = builder.colInputWidth;
		this.defaultInstantiator = builder.defaultInstantiator;
		this.listMappingCollection = builder.listMappingCollection;
		this.pathSeparator = builder.pathSeparator;
	}
	
	public static class Builder {
		// Optional parameters - initialized to default values (these are only here in a single location)
		Location location;
		String messageBundleName;
		Formatters formatters;
		CollectionBuilders collectionBuilders;
		ArgumentNameResolver argumentNameResolver;
		PropertyMethodRegex accessorRegex;
		PropertyMethodRegex setterRegex;
		BeanExtractor beanExtractor;
		Binder binder;
		BeanValidator beanValidator;
		TokenAuthorizer tokenAuthorizer;
		boolean extractorSpecified;
		boolean binderSpecified;
		boolean validatorSpecified;
		boolean inputTrimmed = true;
		int colFormWidth = 12;
		int colLabelWidth = 2;
		int colInputWidth = 4;
		String urlBase;
		Instantiator defaultInstantiator = new ConstructorInstantiator();
		CollectionSpec<?> listMappingCollection = CollectionSpec.getInstance(List.class, ItemsOrder.LINEAR);
		String pathSeparator = DEFAULT_PATH_SEP;

		Builder() {
			// package-default access so only Forms (and classes in current package) can create the builder
		}
		
		public Builder location(Location location) {
			this.location = location;
			return this;
		}
		
		public Builder messageBundleName(String msgBundleName) {
			if (this.validatorSpecified) throw new IllegalStateException("messageBundleName must be specified before the validator.");
			this.messageBundleName = msgBundleName;
			return this;
		}

		public Builder formatters(Formatters formatters) {
			this.formatters = formatters;
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
		
		public Builder tokenAuthorizer(TokenAuthorizer tokenAuthorizer) {
			this.tokenAuthorizer = tokenAuthorizer;
			return this;
		}
		
		public Builder inputTrimmed(boolean inputTrimmed) {
			this.inputTrimmed = inputTrimmed;
			return this;
		}
		
		/**
		 * Base URL for handling AJAX requests.
		 * @param urlBase
		 * @return
		 */
		public Builder urlBase(String urlBase) {
			this.urlBase = urlBase;
			return this;
		}
		
		/**
		 * Maximum width of form in number of columns.
		 * @param max
		 * @return
		 */
		public Builder colFormWidth(int max) {
			this.colFormWidth = max;
			return this;
		}
		
		/**
		 * Default width of label in number of columns.
		 * @param width
		 * @return
		 */
		public Builder colLabelWidth(int width) {
			this.colLabelWidth = width;
			return this;
		}
		
		/**
		 * Default width of input in number of columns.
		 * @param width
		 * @return
		 */
		public Builder colInputWidth(int width) {
			this.colInputWidth = width;
			return this;
		}
		
		/**
		 * Default instantiator of classes.
		 * @param inst
		 * @return
		 */
		public Builder defaultInstantiator(Instantiator inst) {
			this.defaultInstantiator = inst;
			return this;
		}
		
		/**
		 * Specification of collection which should be constructed for nested collections
		 * of complex types used in {@link MappingType#LIST} mappings. By providing this configuration,
		 * default {@link List} collection can be changed to some different type of collection for list mappings.
		 * Given specification should be supported by used {@link CollectionBuilders}.
		 * @param collSpec
		 * @return collection specification
		 */
		public Builder listMappingCollection(CollectionSpec<?> collSpec) {
			this.listMappingCollection = collSpec;
			return this;
		}
		
		/**
		 * Separator of parts in the path (used in fully qualified field name).
		 * @param pathSeparator path separator
		 * @return this builder for chaining calls
		 */
		public Builder pathSeparator(String pathSeparator) {
			this.pathSeparator = pathSeparator;
			return this;
		}
		
		public Config build() {
			// final long startTime = System.nanoTime();
			if (this.location == null) this.location = DEFAULT_LOCATION;
			if (this.messageBundleName == null) this.messageBundleName = DEFAULT_MESSAGE_BUNDLE_NAME;
			if (this.formatters == null) this.formatters = DEFAULT_FORMATTERS;
			if (this.collectionBuilders == null) this.collectionBuilders = DEFAULT_COLLECTION_BUILDERS;
			if (this.argumentNameResolver == null) this.argumentNameResolver = DEFAULT_ARGUMENT_NAME_RESOLVER;
			if (this.accessorRegex == null) this.accessorRegex = DefaultBeanExtractor.DEFAULT_ACCESSOR_REGEX;
			if (this.setterRegex == null) this.setterRegex = DefaultBinder.DEFAULT_SETTER_REGEX;
			if (this.beanExtractor == null) this.beanExtractor = defaultBeanExtractor(this.accessorRegex);
			if (this.binder == null) this.binder = new DefaultBinder(this.formatters, this.collectionBuilders, this.argumentNameResolver, this.setterRegex);
			if (this.beanValidator == null) this.beanValidator = new DefaultBeanValidator(DEFAULT_VALIDATOR_FACTORY, this.beanExtractor, this.messageBundleName);
			if (this.tokenAuthorizer == null) this.tokenAuthorizer = DEFAULT_TOKEN_AUTHORIZER;
			if (this.pathSeparator == null) this.pathSeparator = DEFAULT_PATH_SEP;
			
			Config cfg = new Config(this);
			if (cfg.getPathSeparator() == null) throw new IllegalStateException("path separator cannot be null");
			if (cfg.getLocation() == null) throw new IllegalStateException("location cannot be null");
			if (cfg.getMessageBundleName() == null) throw new IllegalStateException("message bundle name cannot be null");
			if (cfg.getFormatters() == null) throw new IllegalStateException("formatters cannot be null");
			if (cfg.getCollectionBuilders() == null) throw new IllegalStateException("collectionBuilders cannot be null");
			if (cfg.getArgumentNameResolver() == null) throw new IllegalStateException("argumentNameResolver cannot be null");
			if (cfg.getBeanExtractor() == null) throw new IllegalStateException("beanExtractor cannot be null");
			if (cfg.getBinder() == null) throw new IllegalStateException("binder cannot be null");
			if (cfg.getBeanValidator() == null) throw new IllegalStateException("beanValidator cannot be null");
			if (cfg.getTokenAuthorizer() == null) throw new IllegalStateException("tokenAuthorizer cannot be null");
			if (cfg.getAccessorRegex() == null) throw new IllegalStateException("accessorRegex cannot be null");
			if (cfg.getColLabelWidth() > cfg.getColFormWidth()) {
				throw new IllegalStateException("width of label cannot be bigger than width of form");
			}
			if (cfg.getColInputWidth() > cfg.getColFormWidth()) {
				throw new IllegalStateException("width of input cannot be bigger than width of form");
			}
			if (cfg.getDefaultInstantiator() == null) throw new IllegalStateException("Default instantiator cannot be null");
			if (cfg.getListMappingCollection() == null) throw new IllegalStateException("List Mapping collection specification cannot be null");
			if (!cfg.getCollectionBuilders().canHandle(cfg.getListMappingCollection())) {
				throw new IllegalStateException("List Mapping collection specification should be supported by used collection builders.");
			}
			// final long millis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
			// System.out.println("Default form definition built within " + millis + " ms");
			return cfg;
		}
	}

	public Location getLocation() {
		return location;
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
	
	public TokenAuthorizer getTokenAuthorizer() {
		return tokenAuthorizer;
	}

	/**
	 * Returns true if text from the form inputs should be trimmed before binding to form data.
	 * @return
	 */
	public boolean isInputTrimmed() {
		return inputTrimmed;
	}

	public PropertyMethodRegex getAccessorRegex() {
		return accessorRegex;
	}

	public PropertyMethodRegex getSetterRegex() {
		return setterRegex;
	}
	
	/**
	 * Base URL for handling AJAX requests.
	 * @return
	 */
	public String getUrlBase() {
		return urlBase;
	}
	
	/**
	 * Maximum width of form in number of columns.
	 * @return
	 */
	public int getColFormWidth() {
		return colFormWidth;
	}
	
	/**
	 * Default width of label in number of columns.
	 * @return
	 */
	public int getColLabelWidth() {
		return colLabelWidth;
	}
	
	/**
	 * Default width of input in number of columns.
	 * @return
	 */
	public int getColInputWidth() {
		return colInputWidth;
	}
	
	/**
	 * Default instantiator of classes.
	 * @return
	 */
	public Instantiator getDefaultInstantiator() {
		return defaultInstantiator;
	}

	/**
	 * Specification of collection which should be constructed for nested collections
	 * of complex types used in {@link MappingType#LIST} mappings.
	 * @return
	 */
	public CollectionSpec<?> getListMappingCollection() {
		return listMappingCollection;
	}
	
	/**
	 * Separator of parts in the path (used in fully qualified field name).
	 * @return
	 */
	public String getPathSeparator() {
		return pathSeparator;
	}

	private static final Formatters DEFAULT_FORMATTERS = new BasicFormatters();
	private static final Location DEFAULT_LOCATION = Location.DEFAULT;
	private static final String DEFAULT_MESSAGE_BUNDLE_NAME = "ValidationMessages";
	private static final CollectionBuilders DEFAULT_COLLECTION_BUILDERS = new BasicCollectionBuilders();
	private static final ArgumentNameResolver DEFAULT_ARGUMENT_NAME_RESOLVER = new AnnotationArgumentNameResolver();
	private static final TokenAuthorizer DEFAULT_TOKEN_AUTHORIZER = new HashTokenAuthorizer();
	private static BeanExtractor defaultBeanExtractor(PropertyMethodRegex accessorRegex) {
		return new DefaultBeanExtractor(accessorRegex);
	}
}
