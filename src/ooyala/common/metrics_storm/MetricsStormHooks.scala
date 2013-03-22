package ooyala.common.metrics_storm

import backtype.storm.hooks.info._

import backtype.storm.hooks.ITaskHook
import backtype.storm.task.TopologyContext
import backtype.storm.hooks.info._
import com.yammer.metrics.Metrics

/**
 * Implements Storm 0.8.1 hooks interface for easy
 * metrics / embedded web console for Storm topologies.
 *
 * Add using the topology.auto.task.hooks config in Storm.
 * This class must have a zero-arg constructor.
 */
class MetricsStormHooks extends ITaskHook {
  def prepare(config: java.util.Map[_, _], context: TopologyContext) {
    MetricsStorm.initWebConsoleFromTask(config, context)
    MetricsStorm.setupTaskMetrics(context)
  }

  def cleanup() {
    MetricsStorm.stopWebConsole()
    Metrics.shutdown()
  }

  def emit(emitData: EmitInfo) {
    MetricsStorm.emitMeters.get(emitData.taskId) foreach { _.mark }
  }

  def spoutAck(ackData: SpoutAckInfo) {}

  def spoutFail(failData: SpoutFailInfo) {}

  def error(error: Throwable) {}

  def boltAck(ackData: BoltAckInfo) {
    val taskId = ackData.ackingTaskId
    MetricsStorm.ackMeters.get(taskId) foreach { meter => meter.mark() }
  }

  def boltFail(failData: BoltFailInfo) {}
}
