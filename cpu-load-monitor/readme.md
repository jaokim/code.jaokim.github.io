Code for the Inside Java blog post [CPU Load Monitor](https://jaokim.github.io/2024/06/02/cpu-load-monitor).

# Introduction
This project shows how you can add a CPU load monitor to your Java program, and when load is too high, a JFR file can be dumped.

## Components
The project is divided in a JDK 8 part, found in the [java8](src/main/java8) drawer, and a JDK 17 part, [java17](src/main/java17).

The main starting point to show-case the CPU Monitor can be found in the Java 8 part, in [DumpJFROnHighCPU.java](src/main/java8/inside/dumpster/monitor/DumpJFROnHighCPU.java). This is used regardless of JDK version. Its not until you create a new `CPULoadMonitorImpl` that the JVM chooses the appropriate version for [JDK 8](src/main/java8/inside/dumpster/monitor/CPULoadMonitorImpl.java) or [JDK 17](src/main/java17/inside/dumpster/monitor/CPULoadMonitorImpl.java).

# Building
You can build it using either
* Maven and the POM files
* bash scripts

The compile.sh will create Multi-Release JAR-file to combine JDK 17 and 8 classes. If you have JDK 8, only a JDK 8 JAR will be created.