# Formio

Form definition and binding framework with these basic features:
* Simple, configurable and easy-to-use handy tool rather than complex framework with its own lifecycle, special widgets etc.
* Automatic binding, even to immutable objects, collections and arrays.
* Validation of form data (via javax.validation annotations).
* Support for file uploads. 
* Form definitions are immutable, can be freely shared and cached (for e.g. in static variables).
* Respecting possible immutability of form data.
* One simple entry point to API: Forms class.
* Easy integration to existing frameworks.
* Usable with various template frameworks and in web environments without direct access to HttpServletRequest (not necessarily dependent on Servlet API).

## Build

Gradle automation tool is used.
Clean, build and publish to Artifactory repository: 
gradle clean build -Dartifactory.user=admin -Dartifactory.password=password uploadArchives

## Importing project into Eclipse

gradle cleanEclipse
gradle eclipse
Import existing project into Eclipse
