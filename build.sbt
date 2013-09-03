name := "metrics_storm"

// Remove -SNAPSHOT from the version before publishing a release. Don't forget to change the version to
// $(NEXT_VERSION)-SNAPSHOT afterwards!
version := "0.0.7-SNAPSHOT"

scalaVersion := "2.9.1"

scalacOptions += "-Yresolve-term-conflict:package"

unmanagedSourceDirectories in Compile <++= Seq(baseDirectory(_ / "src" )).join

unmanagedSourceDirectories in Test <++= Seq(baseDirectory(_ / "test" )).join

libraryDependencies ++= Seq(
  // slf4j >= 1.6 is needed so jetty logging won't throw an exception
  "org.slf4j" % "slf4j-api" % "1.6.4",
  "org.slf4j" % "slf4j-log4j12" % "1.6.4",
  "com.yammer.metrics" % "metrics-core" % "2.1.2",
  "com.yammer.metrics" % "metrics-servlet" % "2.1.2",
  "javax.servlet" % "servlet-api" % "2.5"
)

// Testing deps
libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "1.9.1" % "test",
                            "org.mockito" % "mockito-all" % "1.9.0" % "test")

resolvers ++= Seq("clojars" at "http://clojars.org/repo/",
                  "clojure-releases" at "http://build.clojure.org/releases")

libraryDependencies += "storm" % "storm" % "0.8.2"

// Testing deps
libraryDependencies ++= Seq()
