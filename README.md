# test-tools [![Download](https://api.bintray.com/packages/t1/javaee-helpers/test-tools/images/download.svg) ](https://bintray.com/t1/javaee-helpers/test-tools/_latestVersion) [![Build Status](https://travis-ci.org/t1/test-tools.svg)](https://travis-ci.org/t1/test-tools)

Collection of test utility classes:

* Subclass `AbstractPackageDependenciesTest` and annotate your packages with `@DependsUpon` to check dependencies between packages.
* Declare a `@Rule` with `FileMemento` to restore a file after the test.
* Overwrite and restore system properties with the `SystemPropertiesRule`.
* Declare a generic `MementoRule` for arbitrary things to restore, using a `Supplier` and a `Consumer`.
* Log the beginning and end of a test run, by declaring a `TestLoggerRule`.
