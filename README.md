# Formio

#### Form definition and binding framework for Java platform:
* Easy-to-use configurable handy tool.
* Automatic binding, even to immutable objects, collections and arrays, complex nested objects and lists of complex objects.
* (Non)default constructors and static factory methods are supported. Primitives are supported everywhere.
* Validation of form data (using bean validation with javax.validation annotations).
* Seamless support for file uploads and configurable max. request/file size.
* Form definitions are immutable, composable, self-contained, can be easily shared and cached.
* One simple entry point to API: Forms class.
* Easy integration with existing frameworks.
* Minimum dependencies.
* Usable with various template frameworks, in environments with or without servlets.
* Simply unit testable forms.
* Inspired mainly by well-designed Play framework.

## Available in Maven Central

http://search.maven.org/#search|ga|1|formio / groupId: net.formio, artifactId: formio, version: 1.0.1

## Demo

http://formio.net

## First Touch

**1) Prepare form definition** (optional automatic mapping of properties):
```java
private static final FormMapping<Person> personForm =
  Forms.automatic(Person.class, "person").build();
```

**2) Fill it with data:**
```java
FormData<Person> formData = new FormData<Person>(person, null /* initial messages */);
FormMapping<Person> filledForm = personForm.fill(formData);
// Push the filled form into a template, use its properties...
```

**3) Bind data edited by user back into an object:**
 ```java
FormData<Person> formData = personForm.bind(new HttpServletRequestParams(request));
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
