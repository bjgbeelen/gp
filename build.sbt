import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.besquare",
      scalaVersion := "2.12.3",
      version      := "0.1.0-SNAPSHOT",
      scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature", "-language:higherKinds", "-language:implicitConversions", "-Ydelambdafy:method", "-target:jvm-1.8"),
  resolvers     ++= Seq(
    "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.bintrayRepo("hseeberger", "maven")
  )
    )),
    name := "Shifts",
    fork in run := true,
    libraryDependencies ++= Seq(
      nScalaTime % Compile,
      scalaTest % Test,
      akkaHttp % Compile,
      akkaHttpCirce % Compile,
      monixCore % Compile,
      // http4sServer % Compile,
      // http4sDsl % Compile,
      // http4sCirce % Compile,
      doobieCore % Compile,
      doobiePostgres % Compile,
      circeCore % Compile,
      circeGeneric % Compile,
      circeParser % Compile,
      cats % Compile,
      logback % Runtime
    )
  )
