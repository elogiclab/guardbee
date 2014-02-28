import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "guardbee"
  val appVersion      = "1.0"

  val appDependencies = Seq(
    //if it's a java project add javaCore, javaJdbc, jdbc etc.
  )
  
  lazy val core = Project("guardbee-core", file("modules/core"))

  lazy val ldap = Project("guardbee-ldap", file("modules/ldap"))

  lazy val google = Project("guardbee-google", file("modules/google"))


  val main = play.Project(
    appName, appVersion, appDependencies
  ).dependsOn(core, ldap, google).aggregate(core, ldap, google)



}

