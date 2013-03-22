Intro
=====
The metrics_storm project adds easy performance metrics collection infrastructure, as well as an
embedded web console, to any Storm (http://github.com/nathanmarz/storm) topology, based on Coda Hale's
excellent metrics library (http://metrics.codahale.com). At a minimum, the following is supported:

* All metrics (gauges, meters, etc.) will be exported via JMX
* A web console that exposes these routes:
    * /metricz - all metrics as JSON blobs, including JVM uptime & other info
    * /healthz
    * /threadz - JVM thread information

In addition, if you enable MetricsStormHooks, then you get automatic emit and ack rate meters for every bolt
and spout in your topology.  To enable, place the following in your storm.yaml:

    topology.auto.task.hooks:
        - "ooyala.common.metrics_storm.MetricsStormHooks"

You can also programmatically inject the above configuration, like this:

    config.put(Config.TOPOLOGY_AUTO_TASK_HOOKS, List(classOf[MetricsStormHooks].getName).asJava)

Refer to https://github.com/nathanmarz/storm/wiki/Hooks for more information.

Web Console Configuration
=========================

Set the following parameter in storm.yaml, listing one port per supervisor.slot.ports, like this:

    worker.webconsole.ports:
        - 7000
        - 7001

If this parameter is not set, the web console port defaults to 7070.

Building
========

You can build this project using SBT 0.11.2 or higher.    To run the unit tests:

    sbt test

To create a jar in target/scala-*/:

    sbt package

To publish to ~/.ivy2/local/, including POM files:

    sbt publish-local

Contributing
============
Contributions via pull request are very welcome.

License
=======
Apache 2.0, see LICENSE.md

Copyright(c) 2013, Ooyala, Inc.
