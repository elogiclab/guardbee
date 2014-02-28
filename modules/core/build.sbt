play.Project.playScalaSettings

organization := "com.elogiclab"

name := "guardbee-core"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-Xlint","-deprecation", "-unchecked","-encoding", "utf8")


resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/repo/"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.0", 
  "org.webjars" % "bootstrap" % "3.0.3",
  "org.webjars" % "jquery" % "1.11.0",
  "org.webjars" % "font-awesome" % "4.0.3",
  "commons-codec" % "commons-codec" % "1.8",
  "org.mindrot" % "jbcrypt" % "0.3m"
)

templatesImport += "com.elogiclab.guardbee.core.GuardbeeService._"





