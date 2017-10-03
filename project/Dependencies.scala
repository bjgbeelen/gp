import sbt._

object Dependencies {
  val Http4sVersion = "0.17.0-M3"
  val CirceVersion = "0.8.0"
  val DoobieVersion = "0.5.0-M7"
  val MonixVersion = "3.0.0-M1"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
  lazy val nScalaTime = "com.github.nscala-time" %% "nscala-time" % "2.16.0"
  val http4sServer =  "org.http4s"     %% "http4s-blaze-server" % Http4sVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.0.10"
  val akkaHttpCirce = "de.heikoseeberger" %% "akka-http-circe" % "1.18.0"
  val http4sCirce = "org.http4s"     %% "http4s-circe"        % Http4sVersion
  val monixCore = "io.monix"  %% "monix" % MonixVersion
  val http4sDsl = "org.http4s"     %% "http4s-dsl"          % Http4sVersion
  val circeCore = "io.circe" %% "circe-core" % CirceVersion
  val circeGeneric = "io.circe" %% "circe-generic" % CirceVersion
  val circeParser = "io.circe" %% "circe-parser" % CirceVersion
  val cats = "org.typelevel" %% "cats" % "0.9.0"
  val logback =  "ch.qos.logback" %  "logback-classic"     % "1.2.1"
  val doobieCore = "org.tpolecat" %% "doobie-core"      % DoobieVersion
  val doobiePostgres = "org.tpolecat" %% "doobie-postgres"  % DoobieVersion // Postgres driver 42.1.4 + type mappings.
  val doobieScalaTest = "org.tpolecat" %% "doobie-scalatest" % DoobieVersion  // ScalaTest support for typechecking statements
}
