play.Project.playScalaSettings

name := "guardbee-google"

organization := Common.buildOrganization

version := Common.buildVersion

scalaVersion := Common.buildScalaVersion

scalacOptions ++= Seq("-Xlint","-deprecation", "-unchecked","-encoding", "utf8")


resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/repo/"

libraryDependencies ++= Seq(
  Common.buildOrganization %% "guardbee-core" % Common.buildVersion
)





