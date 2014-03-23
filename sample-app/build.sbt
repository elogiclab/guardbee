import play.Project._

name := """sample-app"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  "com.elogiclab.guardbee" %% "guardbee-core" % "0.1.0-SNAPSHOT",
  "com.elogiclab.guardbee" %% "guardbee-google" % "0.1.0-SNAPSHOT",
  "com.elogiclab.guardbee" %% "guardbee-ldap" % "0.1.0-SNAPSHOT"
)

templatesImport += "com.elogiclab.guardbee.core.GuardbeeService._"

playScalaSettings
