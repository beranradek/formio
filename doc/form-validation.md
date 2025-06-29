# Form validation

Forms can be validated using two methods that can be freely combined:

- Using bean validation API (JSR-303) with constraints defined by annotations on domain classes.
- Using validators implementing net.formio.validation.Validator interface. Validators can be added to definitions of form fields or form mappings by calling the validator() method.

DefaultBeanValidator class, which is default implementation of net.formio.validation.BeanValidator interface, supports both described methods. In form configuration (Forms.config), you can also provide your own implementation of BeanValidator interface that can support different validation approaches.

And how can you run the form validation? Bind method called on form definition provides you with the resulting object constructed from request parameters and with results of validation - ValidationResult object:

```java
RequestParams reqParams = new ServletRequestParams(request);
FormData<Registration> formData = registrationForm.bind(reqParams, new Locale("en"));
if (formData.isValid()) {
	// save registration and redirect to success page
} else {
	// show form with validation errors
	// formData.getValidationResult() contains both global and field validation messages
}
```

ValidationResult contains ConstraintViolationMessage(s):

- Global validation messages (messages for root form mapping).
- Messages for individual fields or nested mappings (nested complex objects). Messages on form field level can include messages:
  - For binding errors (for e.g. string could not be converted to an integer),
  - for file upload errors (for e.g. max. allowed size of uploaded file was exceeded). Exceeded maximum size of the whole request is reported amongst the global messages.

## Message bundle for localization of validation messages

- You can create properties file for class of edited form data (for e.g. Person.properties in the same package as edited Person class, in resources of your project).
- If such properties file is not found, message translations are searched in ValidationMessages.properties (in the root of the source files). This is embedded fallback mechanism that allows defining translations for commonly used constraints across multiple forms.
- Custom message bundle can also be configured if the previous two options are not what you are searching for.
- Finally, message bundle need not to be used at all.
  - ConstraintViolationMessage carries possible translation but it contains also unresolved key with arguments that can be used with custom storage of localized messages.

## Validation constraints

You can use javax.validation constraints like NotNull, Null, Min, Max, Size, DecimalMax, DecimalMin, Digits, Pattern, AssertFalse, AssertTrue, Future, Past, ... Formio library adds its own build-in constraints: NotEmpty, Email, Phone, URL, IPv4Address, IPv6Address, MaxFileSize, FileExtension... (all available constraints reside in package net.formio.validation.constraints). [Custom constraints can be implemented](http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html/validator-customconstraints.html) to validate one or more properties of edited object, or perform complex global validations on the object.

DefaultBeanValidator class is implementing bean validation API and searches the message bundles like described above. It can also be easily used independently on other features of the library.

## Severity of validation messages

All validation messages carry the severity. INFO, WARNING and ERROR severity is supported in net.formio.validation.Severity. ValidationResult reports that the validation was successfull if there was not any validation message with the severity bigger than WARNING. ERROR is the default severity, but other level can be specified for a constraint using payload of constraint annotation:

```java
@Min(value=8000, payload=SeverityPayload.Warning.class)
private int salary;
```

Payloads from class SeverityPayload are automatically recognized by Formio library.

## Conditional validation

You might want to validate some group of properties while not triggering validation of other groups of properties. This can be implemented using [validation groups](http://www.jroller.com/eyallupu/entry/jsr_303_beans_validation_using) of bean validation API.

In short, you can define your own group, specify the constraints for this group and enable only specific validation group by passing group class to the bind method when binding data from the request back to the edited object:

```java
public interface NewGroup { /** marker interface */ }
...

@NotEmpty(groups = NewGroup.class)
private String name;
...

registrationForm.bind(reqParams, NewCollegue.NewGroup.class)
```

Groups can make up a hierarchy and more or less concrete groups can be specified, probably according to action (button) fired within the form. Also more groups can be entered when calling the bind method.

You could read some request parameter from RequestParams (e.g. ServletRequestParams) in advance and pass groups of constraints to the bind method accordingly, but in general, validation that is dependent on state of more form fields should be encapsulated and handled in one validator on more global level where all necessary data is available.

## Required flag

Both FormField and FormMapping have method isRequired that returns true if the field or complex object corresponding to the mapping is required. Required flag is automatically filled from NotNull or NotEmpty constraints placed on the fields of edited object or it can be set using the required(true) method on the field or mapping definition.

## Validation without annotations

Alternatively, validators implementing net.formio.validation.Validator can be used and specified on form field or form mapping level. This can be freely combined with bean validation API annotations. Formio offers both variants of validators: Constraints for bean validation API and implementations of Formio's Validator interface. For e.g. mapping or field can be defined with provided validator:

```java
Forms.<Integer>field("cylinderCount")
  .required(true) // this will add RequiredValidator
  .validator(WholeNumberValidator.<Integer>range(2, 8))
  .build();
```

Of course, validator can be attached also to the root mapping - such a validator has the whole bound object at its disposal and can validate data from all form fields. You should place the validator to the most nested form element that has all the necessary validated data available. For e.g. validator on nested mapping for Address class has the whole address object at disposal.

### Example of Validator implementation

You can implement net.formio.validation.Validator directly, but the recommended approach is to inherit AbstractValidator abstract class which is common parent of all validators, with convenient methods (e.g. for constructing error or warning messages which represent the output of validator).

```java
public class DigitsValidator<T extends Number> extends AbstractValidator<T> {
	
	protected static final String INTEGER_ARG = "integer";
	protected static final String FRACTION_ARG = "fraction";
	
	private final int maxIntegerLength;
	private final int maxFractionLength;
	
	public static <T extends Number> DigitsValidator<T> getInstance(int maxIntegerLength, int maxFractionLength) {
		return new DigitsValidator<T>(maxIntegerLength, maxFractionLength);
	}
	
	private DigitsValidator(int maxIntegerLength, int maxFractionLength) {
		this.maxIntegerLength = maxIntegerLength;
		this.maxFractionLength = maxFractionLength;
	}

	@Override
	public <U extends T> List<InterpolatedMessage> validate(ValidationContext<U> ctx) {
		List<InterpolatedMessage> msgs = new ArrayList<InterpolatedMessage>();
		if (ctx.getValidatedValue() != null) {
			if (!DigitsValidation.isValid(ctx.getValidatedValue(), maxIntegerLength, maxFractionLength)) {
				msgs.add(error(ctx.getElementName(), "{" + Digits.class.getName() + ".message}",
					new Arg(CURRENT_VALUE_ARG, ctx.getValidatedValue()), 
					new Arg(INTEGER_ARG, Integer.valueOf(maxIntegerLength)), 
					new Arg(FRACTION_ARG, Integer.valueOf(maxFractionLength))));
			}
		}
		return msgs;
	}
	
	public int getMaxIntegerLength() {
		return maxIntegerLength;
	}
	
	public int getMaxFractionLength() {
		return maxFractionLength;
	}
}
```