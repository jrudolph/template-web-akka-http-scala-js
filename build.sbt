val akkaV = "2.5.1"
val akkaHttpV = "10.0.6"
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
        "org.scala-lang" % "scala-reflect" % scalaVersion.value, // needed not to fail in frontend/updateClassifiers
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
        "com.typesafe.akka" %% "akka-stream" % akkaV % "runtime",
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
      watchSources ++= (watchSources in frontend).value
    )

def commonSettings = Seq(
  scalaVersion := "2.12.2",
  scalacOptions ++= Seq("-deprecation", "-feature", "-encoding", "utf8", "-Ywarn-dead-code", "-unchecked", "-Xlint", "-Ywarn-unused-import")
)