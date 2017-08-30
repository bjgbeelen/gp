import sbt._

object Dependencies {
  val Http4sVersion = "0.17.0-M3"
  val CirceVersion = "0.8.0"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
  lazy val nScalaTime = "com.github.nscala-time" %% "nscala-time" % "2.16.0"
  val http4sServer =  "org.http4s"     %% "http4s-blaze-server" % Http4sVersion
  val http4sCirce = "org.http4s"     %% "http4s-circe"        % Http4sVersion
  val http4sDsl = "org.http4s"     %% "http4s-dsl"          % Http4sVersion
  val circeCore = "io.circe" %% "circe-core" % CirceVersion
  val circeGeneric = "io.circe" %% "circe-generic" % CirceVersion
  val circeParser = "io.circe" %% "circe-parser" % CirceVersion
  val cats = "org.typelevel" %% "cats" % "0.9.0"
  val logback =  "ch.qos.logback" %  "logback-classic"     % "1.2.1"
}
