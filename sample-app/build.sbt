import play.Project._

name := """sample-app"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  "com.elogiclab.guardbee" %% "guardbee-core" % "0.1.0-M1",
  "com.elogiclab.guardbee" %% "guardbee-ldap" % "0.1.0-M1"
)

templatesImport += "com.elogiclab.guardbee.core.GuardbeeService._"

playScalaSettings
