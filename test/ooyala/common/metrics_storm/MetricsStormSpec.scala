package ooyala.common.metrics_storm

import scala.sys.process._

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSpec
import backtype.storm.task.TopologyContext
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

class MetricsStormSpec extends FunSpec with ShouldMatchers with MockitoSugar {
  describe("MetricsStorm") {
    it("should initialize web console and then shut it down properly") {
      MetricsStorm.isConsoleInitialized should equal (false)

      MetricsStorm.initWebConsole(7000)
      MetricsStorm.isConsoleInitialized should equal (true)
      Thread sleep  100
      val healthzOutput = { "curl localhost:7000/healthz" !! }
      healthzOutput should include ("deadlock")

      MetricsStorm.stopWebConsole()
      MetricsStorm.isConsoleInitialized should equal (false)
    }

    it("should have a /threadz route") (pending)
  }

  describe("getMetricname") {
    it("should handle passing in null TopologyContext") {
      val metricName = MetricsStorm.getMetricName("foo", null)
      metricName.getScope should equal ("UNKNOWN")
    }

    it("should create metric name from context") {
      val context = mock[TopologyContext]
      when(context.getThisComponentId).thenReturn("foo")
      when(context.getThisTaskId).thenReturn(5)
      MetricsStorm.getMetricName("metric1", context).toString should equal (
        "storm:type=taskInfo,scope=foo,name=metric1")
    }
  }
}
