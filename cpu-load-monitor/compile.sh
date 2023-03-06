
JAVA_VER=$(java -version 2>&1 | sed -n ';s/.* version "\(.*\)\.\(.*\)\..*".*/\1\2/p;')

if [[ "$JAVA_VER" -le 113 ]]
then
  echo Compiling for Java 8 only
  mkdir -p classes
  javac -d classes src/main/java8/inside/dumpster/monitor/*.java
  jar cfe cpuloadmonitor.jar inside.dumpster.monitor.DumpJFROnHighCPU -C classes/ .
else
  echo Compiling Multi-Release JAR
  javac --release 8 -d classes src/main/java8/inside/dumpster/monitor/*.java
  javac --release 17 -d classes-17 src/main/java17/inside/dumpster/*.java
  jar --create --file cpuloadmonitor.jar --main-class inside.dumpster.monitor.DumpJFROnHighCPU -C classes . --release 17 -C classes-17 .
fi

java -jar cpuloadmonitor.jar $@

