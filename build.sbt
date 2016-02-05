name := "play-assignments-rest"
version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.11.7"

incOptions := incOptions.value.withNameHashing(true)
updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

initialize := {
  val required = "1.8"
  val current = sys.props("java.specification.version")
  assert(current == required, s"Unsupported JDK: java.specification.version $current != $required")
}

val playSlickVersion = "1.1.1"
libraryDependencies ++= Seq(
  evolutions,
  cache,
  ws,
  filters,
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.play" %% "play-slick" % playSlickVersion,
  "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion,

  // -------------------------------------------------------------------------
  // Web Jars
  "org.webjars" %% "webjars-play" % "2.4.0",
  "org.webjars" % "bootstrap" % "3.3.5",

  // Angular-specific.
  "org.webjars" % "angularjs" % "1.4.6",
  "org.webjars" % "angular-ui-bootstrap" % "0.13.3",
  "org.webjars.bower" % "angular-flash" % "0.1.14",
  "org.webjars.bower" % "angular-tablesort" % "1.1.0",
  "org.webjars.bower" % "angular-websocket" % "1.0.13",
  "org.webjars" % "angular-paginate-anything" % "4.0.2",
  "org.webjars.bower" % "angular-sanitize" % "1.4.7",

  "com.h2database" % "h2" % "1.4.189",

  //  specs2 % Test,
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "org.scalatestplus" %% "play" % "1.4.0" % "test",
  "org.mockito" % "mockito-all" % "1.10.19"
)
JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

import com.arpnetworking.sbt.typescript.Import.TypescriptKeys._

sourceMap := true
removeComments := true


resolvers += "typesafe-bintray" at "http://dl.bintray.com/typesafe/maven-releases"

//synchronize scala lib dependencies to our scala version
ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

routesGenerator := InjectedRoutesGenerator
