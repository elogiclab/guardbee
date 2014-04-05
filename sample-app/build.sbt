import play.Project._

name := """sample-app"""

version := "1.0-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  "com.elogiclab.guardbee" %% "guardbee-core" % "0.1.0-SNAPSHOT" changing(),
  "com.elogiclab.guardbee" %% "guardbee-views" % "0.1.0-SNAPSHOT" changing(),
  "com.elogiclab.guardbee" %% "guardbee-ldap" % "0.1.0-SNAPSHOT" changing()
)

playScalaSettings
