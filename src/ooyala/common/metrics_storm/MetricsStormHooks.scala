package ooyala.common.metrics_storm

import backtype.storm.hooks.info._

import backtype.storm.hooks._
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
class MetricsStormHooks extends BaseTaskHook {
  override def prepare(config: java.util.Map[_, _], context: TopologyContext) {
    MetricsStorm.initWebConsoleFromTask(config, context)
    MetricsStorm.setupTaskMetrics(context)
  }

  override def cleanup() {
    MetricsStorm.stopWebConsole()
    Metrics.shutdown()
  }

  override def emit(emitData: EmitInfo) {
    MetricsStorm.emitMeters.get(emitData.taskId) foreach { _.mark }
  }

  override def spoutAck(ackData: SpoutAckInfo) {}

  override def spoutFail(failData: SpoutFailInfo) {}  

  override def boltAck(ackData: BoltAckInfo) {
    val taskId = ackData.ackingTaskId
    MetricsStorm.ackMeters.get(taskId) foreach { meter => meter.mark() }
  }

  override def boltFail(failData: BoltFailInfo) {}
}
