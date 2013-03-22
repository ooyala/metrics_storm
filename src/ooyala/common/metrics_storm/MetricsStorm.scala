package ooyala.common.metrics_storm

import collection.JavaConversions._
import collection.mutable.HashMap
import java.util.concurrent.TimeUnit

import backtype.storm.task.TopologyContext
import com.yammer.metrics.util.DeadlockHealthCheck
import com.yammer.metrics.reporting.{MetricsServlet, ThreadDumpServlet, HealthCheckServlet}
import com.yammer.metrics.{Metrics, HealthChecks}
import com.yammer.metrics.core.{MetricName, Meter}
import org.mortbay.jetty.Server
import org.mortbay.jetty.servlet.Context

// TODO(ev): Move this into ScalaStorm
package object config {
  type StormConfigMap = java.util.Map[_, _]

  class StormConfig(val origConfig: StormConfigMap) {
    val accessibleConfig = origConfig.asInstanceOf[java.util.Map[String, AnyRef]]

    // Returns an integer list from a configuration list of numbers, or Nil if key doesn't exist
    def getIntList(key: String): Seq[Int] = {
      if (accessibleConfig.containsKey(key))
        accessibleConfig.get(key).asInstanceOf[java.util.List[Any]].
          map { case l: Long => l.toInt
                case i: Int => i }
      else
        Nil
    }
  }

  implicit def toScalaStormConfig(config: StormConfigMap) = new StormConfig(config)
}

/**
 * Utilities for registering metrics for Storm spouts and bolts, as well as spinning up a metrics web
 * console for storm workers.
 *
 * This can be used for monitoring and debugging of storm workers.
 *
 * Metrics monitored:
 * - acks - the rate and count of tuples acked by this bolt
 * - emits - the rate and count of tuples emitted by this bolt
 */
object MetricsStorm {
  import config._

  // Default port if one above cannot be found
  val DefaultPort = 7070

  // Various metrics for tasks
  val ackMeters = new HashMap[Int, Meter]
  val emitMeters = new HashMap[Int, Meter]

  private var consoleInitialized = false
  private var server: Server = _

  /**
   * Initializes the web console using the storm configuration parameter "worker.webconsole.ports"
   * This should be a list of ports for the web console, and should have the same length as
   * "supervisor.slots.ports", where the console port corresponds to the slot in the same position.
   */
  def initWebConsoleFromTask(conf: StormConfigMap, context: TopologyContext) {
    // Get the list of Storm worker ports, and find the index of current port.  Then use that
    // to index into worker.webconsole.ports.
    val portIndex = conf.getIntList("supervisor.slots.ports").indexOf(context.getThisWorkerPort)
    val portList = conf.getIntList("worker.webconsole.ports")
    val port = if (portIndex >= 0 && portList.length > portIndex) portList(portIndex) else DefaultPort
    initWebConsole(port)
  }

  /**
   * Initializes an embedded Jetty web console
   */
  def initWebConsole(port: Int) {
    synchronized {
      if (consoleInitialized) return

      HealthChecks.register(new DeadlockHealthCheck)

      server = new Server(port)
      val context = new Context(server, "/")
      context.addServlet(classOf[HealthCheckServlet], "/healthz/*")
      context.addServlet(classOf[ThreadDumpServlet], "/threadz/*")
      context.addServlet(classOf[MetricsServlet], "/metricz/*")

      // Starts the Jetty server on a separate thread; won't block.  Don't call join.
      server.start()

      consoleInitialized = true
    }
  }

  /**
   * Returns true if the web console is initialized.
   *
   * @return Boolean True if the web console is initialized, false otherwise
   */
  def isConsoleInitialized = consoleInitialized

  /**
   * Stops the embedded web console.  It's safe to call this multiple times.
   */
  def stopWebConsole() {
    synchronized {
      if (!consoleInitialized) return
      server.stop()
      consoleInitialized = false
    }
  }

  /**
   * A convenience function to create a new {@link com.yammer.metrics.core.MetricName}
   * for use with Metrics.new* metrics factory methods for Storm tasks.
   *
   * @return a new {@link com.yammer.metrics.core.MetricName}
   */
  def getMetricName(name: String, context: TopologyContext) = {
    val scope = if (context == null) "UNKNOWN" else context.getThisComponentId
    new MetricName("storm", "taskInfo", name, scope)
  }

  /**
   * Register metrics for one task.  Should be called only once.
   */
  def setupTaskMetrics(context: TopologyContext) {
    ackMeters(context.getThisTaskId) = Metrics.newMeter(
      getMetricName("acks", context), "acks", TimeUnit.SECONDS)
    emitMeters(context.getThisTaskId) = Metrics.newMeter(
      getMetricName("emits", context), "emits", TimeUnit.SECONDS)
  }
}
