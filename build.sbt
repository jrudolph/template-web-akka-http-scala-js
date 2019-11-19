val scalaV = "2.13.1"
val akkaV = "2.6.0-RC2"
val akkaHttpV = "10.1.10"
val sprayJsonV = "1.3.5"
val upickleV = "0.8.0"
val utestV = "0.7.1"
val scalaJsDomV = "0.9.7"
val specs2V = "4.8.0"

lazy val root =
  project.in(file("."))
    .aggregate(logic, frontend, web)

lazy val logic =
  project.in(file("logic"))
    .settings(commonSettings: _*)

// Scala-Js frontend
lazy val frontend =
  project.in(file("frontend"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(
      persistLauncher in Compile := true,
      persistLauncher in Test := false,
      testFrameworks += new TestFramework("utest.runner.Framework"),
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value, // needed not to fail in frontend/updateClassifiers
        "org.scala-js" %%% "scalajs-dom" % scalaJsDomV,
        "com.lihaoyi" %%% "utest" % utestV % "test"
      )
    )

// Akka Http based backend
lazy val web =
  project.in(file("web"))
    .enablePlugins(SbtTwirl, BuildInfoPlugin)
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-stream" % akkaV,
        "com.typesafe.akka" %% "akka-http" % akkaHttpV,
        "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
        "io.spray" %% "spray-json" % sprayJsonV,
        "org.specs2" %% "specs2-core" % specs2V % "test"
      ),
      resourceGenerators in Compile += Def.task {
        val f1 = (fastOptJS in Compile in frontend).value
        val f2 = (packageScalaJSLauncher in Compile in frontend).value
        Seq(f1.data, f2.data)
      }.taskValue,
      watchSources ++= (watchSources in frontend).value,

      buildInfoPackage := "example.akkawschat",
      buildInfoKeys ++= Seq(
        "longProjectName" -> "Example Project"
      ),
    )
  .dependsOn(logic)

def commonSettings = Seq(
  scalaVersion := scalaV,
  scalacOptions ++= Seq("-deprecation", "-feature", "-encoding", "utf8", "-Ywarn-dead-code", "-unchecked", "-Xlint")
)