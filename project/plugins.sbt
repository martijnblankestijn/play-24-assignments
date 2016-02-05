// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.6")
logLevel := Level.Warn

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.1")
addSbtPlugin("com.arpnetworking" % "sbt-typescript" % "0.1.9")

// Checks dependencies of the project if newer versions are available
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.10")

resolvers += "typesafe-bintray" at "http://dl.bintray.com/typesafe/maven-releases"
