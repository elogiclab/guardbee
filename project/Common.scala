import sbt._
import Keys._

object Common {
  
  val buildOrganization = "com.elogiclab.guardbee"
  val buildVersion = "0.1.0-SNAPSHOT"
  val buildScalaVersion = "2.10.3"
  
  val settings: Seq[Setting[_]] = {
    organization := buildOrganization;
    version := buildVersion;
    scalaVersion := buildScalaVersion
  }
}
