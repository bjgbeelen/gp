package shifts

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._

import Data2018._
import calendar._

object Service extends App with CalendarRoutes {
  private final case class Foo(bar: String)

  implicit val system       = ActorSystem("shifts-http-server")
  implicit val materializer = ActorMaterializer()
  implicit val ec           = system.dispatcher

  val route =
    pathPrefix("api") {
      calendarRoutes
    }

  Http().bindAndHandle(route, "localhost", 5000)
}
