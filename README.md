# Formio

#### Form definition and binding library for Java platform:
* Easy-to-use configurable handy tool.
* Automatic binding, even to immutable objects, collections and arrays, nested objects and lists of them.
* Support for (non)default constructors, static factory methods.
* Primitives can be used everywhere.
* Validation of form data (using bean validation API).
* Seamless support for file uploads and configurable max. request/file size.
* Form definitions are immutable, composable, self-contained, can be easily shared and cached.
* One simple entry point to API: Forms class.
* Non-invasive, easy integration with frameworks, minimum dependencies.
* Usable with various template frameworks, in environments with or without servlets, portlets.
* Simply unit testable forms.
* Protection of forms against CSRF attacks.
* Inspired mainly by well-designed Play framework.

## Available in Maven Central

http://search.maven.org/#search|ga|1|net.formio

## Get Started and Documentation

http://www.formio.net

## Demo

http://formio-demo.herokuapp.com/, sources on https://github.com/beranradek/formio-demo

## First Touch

**1) Prepare form definition** (optional automatic mapping of properties):
```java
private static final FormMapping<Person> personForm =
  Forms.automatic(Person.class, "person").build();
```

**2) Fill it with data:**
```java
FormData<Person> formData = new FormData<Person>(person, ValidationResult.empty);
FormMapping<Person> filledForm = personForm.fill(formData);
// Push the filled form into a template, use its properties...
```

**3) Bind data edited by user back into an object:**
 ```java
FormData<Person> formData = personForm.bind(new ServletRequestParams(request));
if (formData.isValid()) {
  // save the person: formData.getData()
} else {
  // show again the invalid form with validation messages
  // personForm.fill(formData) ...
}
 ```

## Maven Build

* Build JAR: mvn clean package
* Import into Eclipse: mvn eclipse:clean eclipse:eclipse
