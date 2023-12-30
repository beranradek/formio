# Formio [![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.formio/formio/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cnet.formio)


#### Form definition and binding library for Java platform:
* Easy-to-use configurable handy tool.
* Automatic binding, even to immutable objects, collections and arrays, nested objects and lists of them.
* Support for (non)default constructors, static factory methods.
* Primitives can be used everywhere.
* Validation of form data (both bean validation API annotations and net.formio.validation.Validator can be used).
* Seamless support for file uploads and configurable max. request/file size.
* Form definitions are immutable, composable, self-contained, can be easily shared and cached.
* Automatic generating of form markup (or its parts) can be optionally used.
* One simple entry point to API: Forms class.
* Non-invasive, easy integration with frameworks, minimum dependencies.
* Usable with various template frameworks, in environments with or without servlets, portlets, also in desktop applications.
* Simply unit testable forms.
* Protection of forms against CSRF attacks.
* Inspired mainly by well-designed Play framework.

## Available in Maven Central

https://central.sonatype.com/search?q=net.formio

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
// Push the filled form into a template, use its properties to render it; 
// or use BasicFormRenderer to generate form markup automatically
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

## Maintenance of library

### Gradle Build

* Build artifacts (jar, sources, javadoc): ./gradlew clean assemble
* Run tests: gradlew test

Updating gradle wrapper: gradlew wrapper --gradle-version X.Y.Z

### Release

* Remove -SNAPSHOT suffix from version in gradle.properties
* Just run: ./gradlew clean test assemble to see all is ok and ready for release.
* Run: ./gradlew clean publish --warning-mode all --stacktrace
* Login to https://oss.sonatype.org/, "Close" the Staging repository for library, "Refresh" it and "Release" it.
* Create tag with released version (in format X.Y.Z)
* Set new version with -SNAPSHOT suffix in gradle.properties
* Push new version preparation to GitHub

See https://medium.com/viascom/publishing-to-maven-central-with-gradle-a-step-by-step-guide-f3f50724648f
and https://central.sonatype.org/publish/publish-gradle/ for details.  

### Troubleshooting

* Deleting tag in remote repository:

```
git tag -d formio-x.y.z
git push master :refs/tags/formio-x.y.z
```

