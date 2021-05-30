# Formio  [![Build Status](https://travis-ci.org/beranradek/formio.svg?branch=master)](https://travis-ci.org/beranradek/formio) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.formio/formio/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cnet.formio)


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

* Build artifacts (jar, sources, javadoc): gradlew clean assemble
* Import into Eclipse: gradlew cleanEclipse eclipse
* Run tests: gradlew test
* Perform release: gradlew clean release
* Installation to local Maven repository (e.g. for testing snapshots): gradlew install

### Release

* Just run: gradlew clean test assemble to see all is ok and ready for release.
* Run: gradlew clean release
  * This automatically executes also uploadArchives (upload to Maven central) after the release version is created
* Push commits from Gradle release plugin to GitHub
* Login to https://oss.sonatype.org/, "Close" the Staging repository for library, "Refresh" it and "Release" it.

See http://central.sonatype.org/pages/ossrh-guide.html#releasing-to-central and http://central.sonatype.org/pages/gradle.html for details.  

### Troubleshooting

* Deleting tag in remote repository:

```
git tag -d formio-x.y.z
git push master :refs/tags/formio-x.y.z
```
