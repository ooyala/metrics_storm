name := "metrics_storm"

// Remove -SNAPSHOT from the version before publishing a release. Don't forget to change the version to
// $(NEXT_VERSION)-SNAPSHOT afterwards!
version := "0.0.7-SNAPSHOT"

libraryDependencies ++= Seq(
  // slf4j >= 1.6 is needed so jetty logging won't throw an exception
  "org.slf4j" % "slf4j-api" % "1.6.4",
  "org.slf4j" % "slf4j-log4j12" % "1.6.4",
  "com.yammer.metrics" % "metrics-core" % "2.1.2",
  "com.yammer.metrics" % "metrics-servlet" % "2.1.2",
  "javax.servlet" % "servlet-api" % "2.5"
)

libraryDependencies in Test ++= Seq(
  "org.scalatest" %% "scalatest" % "1.8" % "test"
)

resolvers ++= Seq("clojars" at "http://clojars.org/repo/",
                  "clojure-releases" at "http://build.clojure.org/releases")

libraryDependencies += "storm" % "storm" % "0.8.1"

// Testing deps
libraryDependencies ++= Seq()
