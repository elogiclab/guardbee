play.Project.playScalaSettings


name := "guardbee-core"

organization := Common.buildOrganization

version := Common.buildVersion

scalaVersion := Common.buildScalaVersion

scalacOptions ++= Seq("-Xlint","-deprecation", "-unchecked","-encoding", "utf8")


resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/repo/"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.0", 
  "org.webjars" % "bootstrap" % "3.0.3",
  "org.webjars" % "jquery" % "1.11.0",
  "org.webjars" % "font-awesome" % "4.0.3",
  "commons-codec" % "commons-codec" % "1.8",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.typesafe" %% "play-plugins-mailer" % "2.2.0"
)

templatesImport += "com.elogiclab.guardbee.core.GuardbeeService._"

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






