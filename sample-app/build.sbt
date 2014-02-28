import play.Project._

name := """sample-app"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  "com.elogiclab" %% "guardbee-core" % "1.0-SNAPSHOT",
  "com.elogiclab" %% "guardbee-google" % "1.0-SNAPSHOT"
)

templatesImport += "com.elogiclab.guardbee.core.GuardbeeService._"

playScalaSettings
