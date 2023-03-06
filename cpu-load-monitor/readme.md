Code for the Inside Java blog post [Deciphering the stacktrace](https://jaokim.github.io/2021/02/12/deciphering-the-stacktrace.html).

# Introduction
In essence this project shows how you can add a CPU load monitor to your Java program, and when load is too high, a JFR file can be dumped.

## Components
The project is divided in a JDK 8 part, found in the [java8](src/main/java8) drawer, and a JDK 17 part, [java17](src/main/java17).

# Building
You can build it using either
* Maven and the POM files
* bash scripts

The compile.sh will create Multi-Release JAR-file to combine JDK 17 and 8 classes. If you have JDK 8, only a JDK 8 JAR will be created.