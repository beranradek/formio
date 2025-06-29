# Configuration

The library was designed to be simple to use but also to be very configurable. You can override default configuration by passing your own Config object to the build method that constructs form mapping:

```java
private static final FormMapping<Registration> registrationForm =
  Forms.automatic(Registration.class, "registration")
    .build(Forms.config()/* custom settings */.build());
```

Config object is immutable like the form mappings. Configuration is automatically propagated to nested mappings until a nested mapping has its own specific configuration that is propagated further instead. You can override these settings:

## Formatters

Set of formatters available for converting the values of properties to/from string. For example you can specify your own extended set of registered formatters that includes specific formatter for your Day class:

```java
static class ExtendedFormatters extends BasicFormatters {
		
  @Override
  protected Map<Class<?>, Formatter<?>> registerFormatters() {
    Map<Class<?>, Formatter<?>> formatters = new HashMap<Class<?>, Formatter<?>>();
    formatters.putAll(super.registerFormatters());
    formatters.put(Day.class, new Formatter<Day>() {
    
      @Override
      public Day parseFromString(String str, Class<Day> destClass, String formatPattern, Locale locale) {
        try {
	      Date date = FormatsCache.getOrCreateDateFormat(formatPattern, locale).parse(str);
	      return date != null ? Day.valueOf(date) : null;
        } catch (Exception ex) {
          throw new StringParseException(Day.class, str, ex);
        }
      }
				
      @Override
      public String makeString(Day value, String formatPattern, Locale locale) {
        return FormatsCache.getOrCreateDateFormat(formatPattern, locale).format(value);
      }
    });
    return Collections.unmodifiableMap(formatters);
  }
}
```

## Name of message bundle

Custom message bundle for localization of validation messages can be specified using messageBundleName property of the configuration. This message bundle will automatically fallback to ValidationMessages.properties if the message is not found in it. Default message bundle name is derived from the class of edited object. If com.example.Registration class is edited, com/example/Registration bundle (Registration.properties in package com.example) is searched.

## Collection builders

Custom implementation of CollectionBuilders can be specified via collectionBuilders property of configuration. This implementation is responsible to build suitable instance of collection from given items (binding to collections).

## Argument name resolver

Custom implementation of ArgumentNameResolver can be specified via argumentNameResolver property of configuration. ArgumentNameResolver retrieves name of the argument at given index of given "construction method" (constructor, static factory method). Default implementation uses ArgumentName annotation that is used for configuration of argument names.

## Bean extractor

Custom implementation of BeanExtractor can be specified via beanExtractor property of configuration. BeanExtractor extracts map of properties and their values from given object.

## Binder

Custom implementation of Binder can be specified via binder property of configuration. Binder constructs new instance of given class using given instantiator and binds given values to constructed instance (using setters and also the instantiator).

## Regular expression describing accessor (getter)

Custom pattern for names of accessors can be specified using accessorRegex property of configuration. This is particularly useful for JVM languages that have different conventions for naming the properties than Java (e.g. Scala). Default regular expression matches Java-style accessors.

## Regular expression describing setter

Custom pattern for names of setters can be specified using setterRegex property of configuration. Default regular expression matches Java-style setters.

## Bean validator

Custom implementation of BeanValidator can be specified via beanValidator property of configuration. BeanValidator validates an object according to specified constraints and returns result with validation errors. It is responsible also for construction of validation messages for errors from file uploads and for errors from parsing the strings. Default implementation uses bean validation API (some implementation of this API is required in the classpath).

## Trimming input values

Property inputTrimmed of the configuration specifies if the form input values are trimmed before the binding. Default is true - input strings are trimmed.

## File upload parameters

Parameters of file uploads can be configured via ServletRequestParams or PortletRequestParams class that is used to preprocess request parameters, not via Config class - see the File uploads chapter.
