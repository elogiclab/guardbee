play.Project.playScalaSettings

organization := "com.elogiclab"

name := "guardbee-ldap"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-Xlint","-deprecation", "-unchecked","-encoding", "utf8")


resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/repo/"

libraryDependencies ++= Seq(
  "com.unboundid" % "unboundid-ldapsdk" % "2.3.6",
  "com.elogiclab" %% "guardbee-core" % "1.0-SNAPSHOT"
)





