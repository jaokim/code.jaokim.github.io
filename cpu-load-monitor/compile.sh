
JAVA_VER=$(java -version 2>&1 | sed -n ';s/.* version "\(.*\)\.\(.*\)\..*".*/\1\2/p;')

if [[ "$JAVA_VER" -le 113 ]]
then
  echo Compiling Java 8 jar
  mkdir -p classes
  javac -d classes src/main/java8/inside/dumpster/monitor/*.java
  jar cfe cpuloadmonitor.jar inside.dumpster.monitor.DumpJFROnHighCPU -C classes/ .
else
  echo Compiling Multi-Release jar
  javac --release 8 -d classes src/main/java8/inside/dumpster/monitor/*.java
  javac --release 17 -cp classes -d classes-17 src/main/java17/inside/dumpster/monitor/*.java
  jar --create --file cpuloadmonitor.jar --main-class inside.dumpster.monitor.DumpJFROnHighCPU -C classes . --release 17 -C classes-17 .
fi

echo To test the CPU load monitor, you can run:
echo java -jar cpuloadmonitor.jar 
