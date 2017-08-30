import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.11.8",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Hello",
    libraryDependencies ++= Seq(
      nScalaTime % Compile,
      scalaTest % Test,
      http4sServer % Compile,
      http4sCirce % Compile,
      http4sDsl % Compile,
      circeCore % Compile,
      circeGeneric % Compile,
      circeParser % Compile,
      cats % Compile,
      logback % Runtime
    )
  )
