package shifts

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.Printer

import doobie._
import doobie.implicits._
import cats._, cats.implicits._, cats.effect.IO

import Data2018._
import calendar._
import resource._
import counter._
import task._
import repository._
import schedule._

object Service extends App with CalendarRoutes {

  implicit val system       = ActorSystem("shifts-http-server")
  implicit val materializer = ActorMaterializer()
  implicit val ec           = system.dispatcher
  implicit val printer: Printer = Printer(
    preserveOrder = true,
    dropNullKeys = false,
    indent = "  ",
    lbraceRight = "\n",
    rbraceLeft = "\n",
    lbracketRight = "\n",
    rbracketLeft = "\n",
    lrbracketsEmpty = "\n",
    arrayCommaRight = "",
    objectCommaRight = "\n",
    colonLeft = "",
    colonRight = " "
  )

  implicit val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:gp-shifts",
    "app",
    ""
  )

  val initData = Calendars
    .find("2018")
    .flatMap {
      case None =>
        for {
          _ <- Calendars.insert(Data2018.calendarDescription)
          _ <- Tasks.batchInsert(Data2018.tasks.map(TaskView.from).toList)
          _ <- Resources.batchInsert(Data2018.resources.map(ResourceView.from(Data2018.calendarDescription.name)))
          _ <- Counters.batchInsert(Data2018.counters.map(CounterView.from(Data2018.calendarDescription.name)))
          _ <- Schedules.insert(ScheduleView.from(Data2018.testSchedule))
        } yield 1
      case _ =>
        sql"select 42".query[Int].unique
    }
    .transact(xa)
    .unsafeRunSync

  val route =
    pathPrefix("api") {
      calendarRoutes
    }

  Http().bindAndHandle(route, "localhost", 5000)
}
