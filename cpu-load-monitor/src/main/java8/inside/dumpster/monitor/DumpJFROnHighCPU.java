/*
 * 
 */
package inside.dumpster.monitor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example to show how to dump a JFR file when the CPU load is too high. A
 * recording stream is used to monitor the CPULoad event and then trigger a JFR
 * file to be written when the CPU load is above a certain level.
 *
 * @author Joakim Nordstrom joakim.nordstrom@oracle.com
 */
public class DumpJFROnHighCPU {

  public static final double CPU_LEVEL_THRESHOLD = 0.3;

  public static void main(String[] args) throws Exception {
    final Runnable cpuConsumerThread = new CPUConsumer();
    final double cpuLevelThreshold;
    if (args.length > 0) {
      cpuLevelThreshold = Double.parseDouble(args[0]);
    } else {
      cpuLevelThreshold = CPU_LEVEL_THRESHOLD;
    }
    System.out.println("");
    System.out.println("- CPU Level Monitor -");
    System.out.println("Let the CPU level register a few test-points. When");
    System.out.println("ready, press enter to start a new thread that consumes");
    System.out.println("some CPU. You can alter the CPU level threshold by ");
    System.out.println("giving another value as argument.");
    System.out.println("A JFR file will only be dumped once every minute.");
    System.out.println("");
	System.out.println(String.format("CPU level threshold: %f", cpuLevelThreshold));
    System.out.println("");

	// This starts our CPU level monitor
    CPULoadMonitor cpuLoad = new CPULoadMonitorImpl();
    cpuLoad.monitor(cpuLevel -> cpuLevel > cpuLevelThreshold,
            () -> jfrDumpDestination());

    System.out.println("Press enter to start the CPU consuming threads.");
    System.in.read();
    for(int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
      new Thread(cpuConsumerThread).start();
    }
  }

  private static final int DUMPINTERVAL_SECONDS = 60;

  /**
   * Keeps track of last dumped JFR file.
   */
  private static Date lastJFRDump = new Date();

  /**
   * Get the file to dump JFR recording to. If the optional 
   * is empty, a JFR recording has already been dumped in the 
   * last DUMPINTERVAL_SECONDS.
   *
   * @return the file to dump recording to, unless empty. 
   */
  private synchronized static Optional<File> jfrDumpDestination() {
    final Date now = new Date();
    if (now.after(lastJFRDump)) {
      final String filename = new SimpleDateFormat("yyyyMMddHHmm").format(now);
      lastJFRDump = new Date(now.getTime() + (DUMPINTERVAL_SECONDS * 1000));
      final File jfrFile = new File(filename + ".jfr");
      return Optional.of(jfrFile);
    } else {
      return Optional.empty();
    }
  }

  private static class CPUConsumer implements Runnable {
    @Override
    public void run() {
      try {
        System.out.println("Starting CPU consuming thread");
        while (true) {
          double res = Math.random();
          if (System.currentTimeMillis() % 100 == 0) {
            res = Math.IEEEremainder(res, Math.random());
            Thread.sleep(10);
          }
        }
      } catch (InterruptedException e) {
        System.out.println("CPU consuming thread was interrupted");
      }
    }
  }
}
