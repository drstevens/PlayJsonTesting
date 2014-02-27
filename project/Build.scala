import sbt._
import sbt.Keys._

object PlayJsonTestingBuild extends Build {

  lazy val playjsontesting = Project(
    id = "playjsontesting",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "PlayJsonTesting",
      organization := "com.daverstevens",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.3"
      // add other settings here
    )
  )
}
