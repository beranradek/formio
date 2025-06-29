 # Introduction to Formio

## Content
- [Important classes/interfaces of the library](#important)
- [Data binding](#binding)
- [Form definition](#definition)
- [Filling the form with data](#fill)
- [Data for templates](#templatevars)
- [Binding data from the request back to an object](#bind)

## Important classes/interfaces of the library

These basic interfaces of the library will be described in detail in later chapters.

- **Forms** – entry point for the usage of the library
  - **FormElement** - an element of form - form field or group of fields (form mapping)
  - **FormMapping** - a mapping corresponding to one edited class, there are nested mappings for nested classes
  - **FormField** - mappings consist of form fields (and possible nested mappings)
  - **FieldProps** - custom definition can be provided for some chosen form field (using FieldProps)
- **RequestParams** - preprocessor of request parameters (ServletRequestParams extracts parameters from HttpServletRequest, PortletRequestParams from javax.portlet.ActionRequest)
- **ValidationResult** - contains validation messages for violated constraints
  - **ConstraintViolationMessage** - object holding information about violation of some constraint
- **Config** - configuration of the form mapping
  - Formatters, Formatter
  - CollectionBuilders, CollectionBuilder
  - BeanExtractor
  - Instantiator
  - Binder
  - ArgumentNameResolver
  - BeanValidator
  - PropertyMethodRegex
- **UploadedFile** - type into which uploaded files can be bound
- **FormRenderer** - used for automatic rendering of form markup. Can be extended/customized.

## Data binding

- Supports immutable objects via `Instantiator` interface.
- Setter and "construction methods" binding:
  - Default or non-default constructors
  - Static factory methods
- Supports:
  - Collections and arrays
  - Nested complex objects and lists
  - Primitives and wrappers
  - String, Enum, BigDecimal, BigInteger, Date
- Custom formatters are supported.

## Form definition

### Basic mapping

Can define:
- **Form fields** (editing one property)
- **Nested mappings** (complex nested objects)
- **List mappings** (collections/arrays of complex objects)

Mappings can be reused and stored in static variables.

#### Example: Registration form

```java
private static final FormMapping<RegDate> regDateMapping = 
  Forms.basic(RegDate.class, "regDate").fields("month", "year").build();

private static final FormMapping<Registration> registrationForm =
  Forms.basic(Registration.class, "registration")
  .fields("attendanceReasons", "cv", "interests", "email")
  .nested(Forms.basic(Address.class, "contactAddress", 
    Forms.factoryMethod(Address.class, "getInstance"))
    .fields("street", "city", "zipCode").build())
  .nested(Forms.basic(Collegue.class, "collegues", MappingType.LIST)
    .fields("name", "email")
    .nested(regDateMapping)
    .build())
  .build();
```

### Automatic mapping

- Uses introspection for field definitions.
- Exclude using `@Ignored`.
- Can combine with basic mappings.

#### Example

```java
private static final FormMapping<Registration> registrationForm =
  Forms.automatic(Registration.class, "registration")
    .nested(Forms.automatic(Address.class, "contactAddress", 
      Forms.factoryMethod(Address.class, "getInstance")).build())
  .build();
```

Or simpler:

```java
private static final FormMapping<Registration> registrationForm =
  Forms.automatic(Registration.class, "registration").build();
```

### Custom form field specification

Use `FieldProps`:

- `type`, `pattern`, `formatter`

Example:

```java
private static final FormMapping<Person> PERSON_FORM = Forms.basic(Person.class, "person")
  .fields("personId", "firstName", "lastName", "salary", "phone", "male", "nation")
  .field(Forms.<Date>field("birthDate").type("text").formatter(CUSTOM_DATE_FORMATTER).build())
  .build();
```

## Filling the form with data

```java
FormData<Person> formData = new FormData<Person>(
  person, ValidationResult.empty);
FormMapping<Person> filledForm = personForm.fill(formData);
// Push filledForm to template
```

## Data for templates

Push `FormMapping` to template. Main properties:

- `validationResult`
  - `success`, `fieldMessages`, `globalMessages`
- `elements` – list of FormElement
- `fields` – map of form fields
- `name`, `labelKey`, `filledObject`, `required`
- `nested` – nested mappings
- `list` – if MappingType.LIST

**FormField properties:**

- `name`, `labelKey`, `filledObject`, `visible`, `enabled`, `readonly`, `required`
- `validationMessages`, `validators`

`FormElement.getProperties()` gives access to custom properties like help, flags, sizes etc.

## Binding data from the request back to an object

```java
FormData<Person> formData = personForm.bind(
  new ServletRequestParams(request));
if (formData.isValid()) {
  // save formData.getData()
} else {
  // show form again with errors
}
```
