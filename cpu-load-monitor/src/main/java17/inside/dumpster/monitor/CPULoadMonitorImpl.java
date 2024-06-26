/*
 *
 */
package inside.dumpster.monitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.function.DoublePredicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.jfr.Configuration;
import jdk.jfr.consumer.RecordingStream;

/**
 * Example to show how to dump a JFR file when the CPU load is too high.
 * A recording stream is used to monitor the CPULoad event and then trigger
 * a JFR file to be written when the CPU load is above a certain level.
 *
 * @author Joakim Nordstrom joakim.nordstrom@oracle.com
 */
public class CPULoadMonitorImpl implements CPULoadMonitor {

  /**
   * Monitors the JFR CPU load event. The event is triggered based on JFR
   * configuration.
   * @param cpuLevelTester
   * @param jfrRecordingDestination
   * @throws Exception
   */
  @Override
  public void monitor(DoublePredicate cpuLevelTester, Supplier<Optional<File>> jfrRecordingDestination) throws Exception {
    System.out.println("Adding JDK17 type CPU level monitor");
	new Thread( () -> {
    
      try (RecordingStream stream = new RecordingStream()) {
        stream.enable("jdk.CPULoad").withPeriod(Duration.ofSeconds(1));
        stream.onEvent("jdk.CPULoad", (event) -> {
          // get CPU measurement: "jvmSystem", "jvmUser" or "machineTotal"
          float cpuLoad = event.getFloat("machineTotal");

          if (cpuLevelTester.test(cpuLoad)) {
            System.out.println(String.format("High CPU level noticed: %.3f", cpuLoad));
            Optional<File> jfrFileDestination = jfrRecordingDestination.get();
            jfrFileDestination.ifPresent((file -> {
              try {
                stream.dump(file.toPath());
                System.out.println("Dumped JFR recording to: "+file.getAbsolutePath());
              } catch (IOException ex) {
                Logger.getLogger(CPULoadMonitorImpl.class.getName()).log(Level.SEVERE, null, ex);
              }
            }));
          } else {
            System.out.println(String.format("CPU level ok: %.3f", cpuLoad));
          }
        });
        stream.start();
      }
    }).start();
  }


}
