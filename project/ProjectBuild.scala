import sbt._
import Keys._

import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin._
import autoImport._

import spray.revolver.RevolverPlugin._

object ProjectBuild extends Build {
  val akkaV = "2.4.17"
  val akkaHttpV = "10.0.4"
  val sprayJsonV = "1.3.3"
  val upickleV = "0.4.4"
  val utestV = "0.4.5"
  val scalaJsDomV = "0.9.1"
  val specs2V = "3.8.9"

  lazy val root =
    Project("root", file("."))
      .aggregate(frontend, backend)

  // Scala-Js frontend
  lazy val frontend =
    Project("frontend", file("frontend"))
      .enablePlugins(ScalaJSPlugin)
      .settings(commonSettings: _*)
      .settings(
        persistLauncher in Compile := true,
        persistLauncher in Test := false,
        testFrameworks += new TestFramework("utest.runner.Framework"),
        libraryDependencies ++= Seq(
          "org.scala-js" %%% "scalajs-dom" % scalaJsDomV,
          "com.lihaoyi" %%% "utest" % utestV % "test"
        )
      )

  // Akka Http based backend
  lazy val backend =
    Project("backend", file("backend"))
      .settings(commonSettings: _*)
      .settings(
        libraryDependencies ++= Seq(
          "com.typesafe.akka" %% "akka-http" % akkaHttpV,
          "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
          "io.spray" %% "spray-json" % sprayJsonV,
          "org.specs2" %% "specs2-core" % specs2V % "test"
        ),
        (resourceGenerators in Compile) <+=
          (fastOptJS in Compile in frontend, packageScalaJSLauncher in Compile in frontend)
            .map((f1, f2) => Seq(f1.data, f2.data)),
        watchSources <++= (watchSources in frontend)
      )

  def commonSettings = Seq(
    scalaVersion := "2.12.1",
    scalacOptions ++= Seq("-deprecation", "-feature", "-encoding", "utf8", "-Ywarn-dead-code", "-unchecked", "-Xlint", "-Ywarn-unused-import")
  ) ++ ScalariformSupport.formatSettings
}
