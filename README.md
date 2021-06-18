# test-tools [![Download](https://maven-badges.herokuapp.com/maven-central/com.github.t1/test-tools/badge.svg)](https://search.maven.org/artifact/com.github.t1/test-tools)

Collection of test utility classes:

* Subclass `AbstractPackageDependenciesTest` and annotate your packages with `@DependsUpon` to check dependencies between packages.
* Declare a `@Rule` with `FileMemento` to restore a file after the test.
* Overwrite and restore system properties with the `SystemPropertiesRule`.
* Declare a generic `MementoRule` for arbitrary things to restore, using a `Supplier` and a `Consumer`.
* Log the beginning and end of a test run, by declaring a `TestLoggerRule`.

# Compatibility with JDK-9+

The `AbstractPackageDependenciesTest` depends on classes from the `jdeps` tool in JDK 8 residing in the `tools.jar`.
This file has been removed in JDK 9 and the jdeps classes where moved into the module `jdk.jdeps`,
but without exporting the classes, so we can't just add a `module-info.java`.
This may become a problem in the future, but for now, we can live with a Maven profile, so building works on JDK8 and 9.
