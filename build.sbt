val scalaV = "2.13.6"
val akkaV = "2.6.14"
val akkaHttpV = "10.2.4"
val sprayJsonV = "1.3.6"
val upickleV = "0.8.0"
val utestV = "0.7.10"
val scalaJsDomV = "1.1.0"
val specs2V = "4.11.0"

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
      scalaJSUseMainModuleInitializer := true,
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
        val f1 = (fastOptJS in Compile in frontend).value.data
        Seq(f1, new File(f1.getPath+".map"))
      }.taskValue,
      watchSources ++= (watchSources in frontend).value,

      buildInfoPackage := "example.akkawschat",
      buildInfoKeys ++= Seq(
        "longProjectName" -> "Example Project"
      ),

      // use separate dependency and app jars
      assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false, includeDependency = false),
      assemblyJarName in assembly := "app.jar", // contract with Dockerfile
      assemblyJarName in assemblyPackageDependency := "deps.jar", // contract with Dockerfile
    )
  .dependsOn(logic)

def commonSettings = Seq(
  scalaVersion := scalaV,
  scalacOptions ++= Seq("-deprecation", "-feature", "-encoding", "utf8", "-Ywarn-dead-code", "-unchecked", "-Xlint")
)
