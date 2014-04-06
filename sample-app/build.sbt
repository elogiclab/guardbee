import play.Project._

name := """sample-app"""

version := "0.1.0-M2"

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  "com.elogiclab.guardbee" %% "guardbee-core" % "0.1.0-M2" changing(),
  "com.elogiclab.guardbee" %% "guardbee-views" % "0.1.0-M2" changing(),
  "com.elogiclab.guardbee" %% "guardbee-ldap" % "0.1.0-M2" changing()
)

playScalaSettings
