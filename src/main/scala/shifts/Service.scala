package shifts

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

import org.http4s._
import org.http4s.dsl._
import org.http4s.circe._
import org.http4s.server.blaze._

import cats.implicits._

import Data2018._

object Service extends App {
  val service = HttpService {
    case GET -> Root / "hello" =>
      Ok("Hello, better world.")
    case GET -> Root / "tasks" =>
      Ok(tasks.asJson)
  }

  BlazeBuilder
    .bindHttp(8080)
    .mountService(service, "/")
    .run
    .awaitShutdown()
}
