libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "2.4.9" % "test"
)

scalaVersion := "2.11.7"

ScalariformSupport.formatSettings

Revolver.settings
