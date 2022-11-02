val scalaV = "2.13.10"
val akkaV = "2.6.20"
val akkaHttpV = "10.2.10"
val sprayJsonV = "1.3.6"
val upickleV = "0.8.1"
val utestV = "0.7.10"
val scalaJsDomV = "2.3.0"

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
      ),
      Compile / resourceGenerators += Def.task {
        val f1 = (frontend / Compile /fastOptJS).value.data
        Seq(f1, new File(f1.getPath+".map"))
      }.taskValue,
      watchSources ++= (frontend / watchSources).value,

      buildInfoPackage := "example.akkawschat",
      buildInfoKeys ++= Seq(
        "longProjectName" -> "Example Project"
      ),

      // use separate dependency and app jars
      assembly / assemblyOption := (assembly / assemblyOption).value.copy(includeScala = false, includeDependency = false),
      assembly / assemblyJarName := "app.jar", // contract with Dockerfile
      assembly / assemblyJarName := "deps.jar", // contract with Dockerfile
    )
  .dependsOn(logic)

def commonSettings = Seq(
  scalaVersion := scalaV,
  scalacOptions ++= Seq("-deprecation", "-feature", "-encoding", "utf8", "-Ywarn-dead-code", "-unchecked", "-Xlint")
)
