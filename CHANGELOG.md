# Changelog

## [1.8.1] - 2025-12-13

### Updated Dependencies

- Migration to `org.apache.commons:commons-fileupload2-jakarta-servlet6:2.0.0-M4`

- **JUnit**: Updated from `junit:junit:4.8.2` to `org.junit.jupiter:junit-jupiter-api:6.1.0-M1`
  - Added `org.junit.jupiter:junit-jupiter-engine:6.1.0-M1` for JUnit 5 test execution
  - Added `org.junit.vintage:junit-vintage-engine:6.1.0-M1` for backward compatibility with existing JUnit 4 tests
  
- **Spring Test Framework**: Updated from version `6.1.2` to `7.0.2`
  - Updated `spring-core`, `spring-web`, and `spring-test` dependencies

- **Hibernate Validator**: Updated from `org.hibernate:hibernate-validator:8.0.1.Final` to `org.hibernate.validator:hibernate-validator:9.1.0.Final`

- **Jakarta EL**: Migrated from deprecated Glassfish implementation
  - Updated to `jakarta.el:jakarta.el-api:6.0.1` (API)
  - Updated to `org.glassfish:jakarta.el:5.0.0-M1` (implementation)

- **Apache HttpClient**: Migrated from deprecated commons-httpclient
  - Updated from `commons-httpclient:commons-httpclient:3.1` to `org.apache.httpcomponents.client5:httpclient5:5.5.1`
  - Updated test code in `MockServletRequests.java` to use HttpClient 5 API

### Technical Changes

- **Test Compatibility**: Maintained backward compatibility for existing JUnit 4 tests while enabling JUnit 5 features
- **API Migration**: Updated multipart request handling in tests to use modern HttpClient 5 APIs

## [1.7.0] - 2023-12-30

* Upgrade to Jakarta Servlet API 6, Gradle 8.5, Java 17, Jakarta Validation API 3, Commons File Upload Jakarta 2
* PortletRequestParams (Portlet API) is no longer part of the library after upgrade.

## [1.6.9] - 2022-07-30

* New methods in AbstractValidator: localizedError/Warning/Info for creation of already translated messages.

## [1.6.8] - 2022-06-26

* Removed default messageKey from DefaultInterpolatedMessage ("{" + getClass().getSimpleName() + ".message}" default remains only in concrete subclasses).

## [1.6.7] - 2022-03-14

* Usage of user readable message text in bean validator.

## [1.6.6] - 2022-03-02

* Field for user readable message text in InterpolatedMessage.

## [1.6.5] - 2021-05-30

* Fixed issue #18: Build of form definition with default config is too slow.

## [1.6.4] - 2019-09-03

* Size annotation presence should not be checked within isRequiredByAnnotations. Null elements should be valid for specified Size annotation according to JSR-303.

## [1.6.3] - 2017-03-25

* Fixed longFormatter in BasicFormatters (incorrect byteValue call was used to parse value from a string)

## [1.6.2] - 2016-07-08

* Optimized StringBuilder usage in form renderer (by jklap)
* Added AnyNotEmpty constraint for at least one not-empty field (by Petr Kalivoda)
* Boolean formatter recognizes "on" string.

## [1.6.1] - 2016-04-25

* Fix of undiscovered field of parent class when looking for required flag.
* Looking for required annotation also in getters.

## [1.6.0] - 2016-02-07

* Additional methods for searching form elements: requireElement, requireElements.
* Fix: Handled null configuration when rendering form element and getting location (locale, time zone).
* Refactoring of AJAX API:
  * JsEventToUrl and HandledJsEvent replaced by AjaxHandler.
  * DATA_AJAX_ACTIONS form field property replaced by AJAX_HANDLERS.
  * AbstractFormStateStorage with withUpdatedState method that can be used to load/store form state inside AjaxAction.

## [1.5.1] - 2015-08-07

* Methods isTdiAjaxRequest and getTdiAjaxSrcElementName moved to RequestParams with implementations in AbstractRequestParams.

## [1.5.0] - 2015-08-04

* Configurable type of resulting collection for list mappings (nested collections of complex objects). See Config.listMappingCollection.
* New CollectionBuilders.getItemClass method to allow customization of reflection logic for finding type of collection item.
* Renamed BasicFormRenderer -> FormRenderer
* net.formio.format.Location encapsulating both Locale and TimeZone settings, accepted by fill and bind methods.
* Configurable path separator in form field/mapping names. Removed global Forms.PATH_SEP constant.

## [1.4.2] - 2015-07-06

* Using Netflix gradle-extra-configurations-plugin for optional dependencies in pom.xml generated from Maven Gradle plugin
* Fixed Gradle build - no fat jar including dependencies as in previous version 1.4.1

## [1.4.1] - 2015-07-06

* Build and upload to Maven central migrated to Gradle.
* More protected methods in DefaultBinder for easier customizations.
* RequiredValidator identifying also scala.None

## [1.4.0] - 2015-06-24

* Fixed formatting of BigDecimal values.
* Serving AJAX actions in server side code. Invoking AJAX requests and rendering AJAX responses using TDI JS library.
* Fixed Integer.compare for Open JDK.
* Refactored BasicFormRenderer's API.
* Separator specific for given locale in number formatters.
* Renamed FieldProperty -> FormElementProperty.
* New properties: chooseOptionDisplayed, chooseOptionTitle, labelVisible, accept, maxlength.
* Fixed usage of required property in isRequired method.
* Common passing of request errors and file size errors to validation.
* Validator interface for additional non-bean-validation API validators. Can be combined with bean validation constraint annotations.
* Validators extending Validator interface.
* Properties classes moved to props package.
* Introduced detached fields and mappings.
* Configurable inline fields and width of form and its fields in Bootstrap's grid columns.
* Definable custom labelKey for mappings and fields.
* ConstructionDescription as interface.
* Instantiator taking class of constructed type as a method parameter instead of class type parameter.
* Configurable default instantiator.

## [1.3.0] - 2015-01-04

* getElements method of mapping returning form elements in order as declared in form definition.
* Increased test coverage (including file uploads for servlets and portlets).
* Simplified internal construction using builders, refactored internal construction of paths.
* ChoiceProvider and ChoiceRenderer definable for form fields (can be used in form rendering as a support for codebooks).
* fillAndValidate method that validates filled data so the validation errors can be shown when the form is rendered for the first time.
* Forms.previewForm for automatically rendered form preview.
* getValidationMessages method on form element.
* Accessibility methods respecting accessibility of parent.

## [1.2.1] - 2014-11-13

* Definable properties also for form mappings (in addition to fields).
* Public implementation classes passed to the templates due to instrospection required by some template frameworks (constructors remain with restricted accessibility).

## [1.2.0] - 2014-10-05

* ValidationResult contains List of ConstraintViolationMessages instead of a Set.
* Ensuring deterministic order of ConstraintViolationMessages and their arguments (the order coming from bean validation API implementation can be non-deterministic).

## [1.1.3] - 2014-09-04

* FileExtension, MaxFileSize constraints by Karel Stefan. 
* visible, enabled, readonly, required, help convenience methods in FieldProps.

## [1.1.2] - 2014-09-02

* Generic FormField
* Convenience isVisible(), isEnabled(), isReadonly(), isRequired(), getHelp() methods in FormField.
* MapParams with another convenience constructors.
* getField(Class<U> dataClass, String propertyName) method in FormMapping.

## [1.1.1] - 2014-08-22

* Fixed parsing of invalid BigDecimal (StringParseException should be thrown).
* Filling field with original string value in case of parse error.

## [1.1.0] - 2014-08-17

* AbstractRequestParams for more convenient implementation of RequestParams 
* Validators for phone, IPv4/6 address, URL
* SessionAttributeStorage
* Support for portlet API (PortletRequest): PortletRequestParams including multipart request processing
* Field properties (see FieldProperty), definable via FieldProps (in form of heterogeneous map)
* Constants for content types (ContentTypes).

## [1.0.4] - 2014-05-11

* basicSecured, automaticSecured mappings for protection against CSRF in POST submits.

## [1.0.3] - 2014-05-02

* Dynamic specification of locale in fill/bind methods, instead of locale in configuration.

## [1.0.2] - 2014-04-21

* Binding to provided instance of edited object.
* Only optional Maven dependency on servlet API.
* New version of fileupload dependency.
* Fixed propagation of field definitions to the filled form for nested objects filled with "null" data.
* Builder for FieldProps.
* Type of form field in FieldProps and FormField (e.g. "textarea", "select" can be specified directly in server side if desired).

## [1.0.1] - 2014-04-09

* First released version.
