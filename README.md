# Formio

Form definition and binding framework for Java platform with these basic features:
* Easy-to-use configurable handy tool rather than complex framework with its own lifecycle, special widgets etc.
* Automatic binding, even to immutable objects, collections and arrays, complex nested objects and lists of complex objects. Also primitives are supported.
* Default and non-default constructors and static factory methods are supported.
* Validation of form data (using beanvalidation with javax.validation annotations).
* Support for file uploads and configurable max. request/file size.
* Form definitions are immutable, composable and can be freely shared and cached (for e.g. in static variables).
* One simple entry point to API: Forms class.
* Easy integration to existing frameworks.
* Usable with various template frameworks and in environments with or without access to HttpServletRequest (not necessarily dependent on Servlet API).
* Simply unit testable forms.

## Build

Maven is used:
mvn clean package

Importing maven project into Eclipse:
mvn eclipse:clean eclipse:eclipse

## Alternative Gradle Build

Note that Gradle build need not to be up-to-date.

Gradle automation tool is used.
Clean, build and publish to Artifactory repository: 
gradle clean build -Dartifactory.user=admin -Dartifactory.password=password uploadArchives

Importing gradle project into Eclipse:
gradle clean cleanEclipse eclipse
Import existing project into Eclipse
