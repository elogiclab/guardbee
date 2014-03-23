play.Project.playScalaSettings

organization := Common.buildOrganization

version := Common.buildVersion

scalaVersion := Common.buildScalaVersion

scalacOptions ++= Seq("-Xlint","-deprecation", "-unchecked","-encoding", "utf8")


resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/repo/"

libraryDependencies ++= Seq(
  "com.unboundid" % "unboundid-ldapsdk" % "2.3.6",
  Common.buildOrganization %% "guardbee-core" % Common.buildVersion
)





