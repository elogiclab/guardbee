play.Project.playScalaSettings

organization := Common.buildOrganization

version := Common.buildVersion

scalaVersion := Common.buildScalaVersion

scalacOptions ++= Seq("-Xlint","-deprecation", "-unchecked","-encoding", "utf8")

resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/repo/"

resolvers += "Snapshot repository" at "https://oss.sonatype.org/content/repositories/snapshots"


libraryDependencies ++= Seq(
  "com.unboundid" % "unboundid-ldapsdk" % "2.3.6",
  Common.buildOrganization %% "guardbee-core" % Common.buildVersion
)

publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) 
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else                             
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { x => false }

pomExtra := (
  <url>http://www.elogiclab.com</url>
  <licenses>
    <license>
      <name>MIT</name>
      <url>http://opensource.org/licenses/MIT</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:elogiclab/guardbee.git</url>
    <connection>scm:git:git@github.com:elogiclab/guardbee.git</connection>
  </scm>
  <developers>
    <developer>
      <id>msarti</id>
      <name>Marco Sarti</name>
      <url>http://www.elogiclab.com</url>
    </developer>
  </developers>
)

apiURL := Some(url("http://www.elogiclab.com/guardbee/api/"))

scalacOptions in (Compile,doc) := Seq("-groups", "-implicits")

testOptions in Test += Tests.Argument("exclude", "ldap")




