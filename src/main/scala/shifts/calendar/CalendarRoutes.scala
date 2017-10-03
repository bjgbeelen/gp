package shifts
package calendar

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.Printer

import monix.execution.Scheduler.Implicits.global
import monix.eval.Task

import task.TaskRoutes
import repository._
import resource.ResourceRoutes
import counter.CounterRoutes
import schedule.ScheduleRoutes
import Data2018._
import scala.concurrent._
import doobie._, doobie.implicits._
import cats.effect.IO

trait CalendarRoutes
    extends FailFastCirceSupport
    with TaskRoutes
    with ResourceRoutes
    with CounterRoutes
    with ScheduleRoutes {
  def calendarRoutes(implicit ec: ExecutionContext, printer: Printer, transactor: Transactor[IO]) =
    pathPrefix("calendars") {
      (get & (pathSingleSlash | pathEnd)) {
        val io = Calendars.list().transact(transactor).map(l => l.map(_.name))
        complete(Task.fromIO(io).runAsync)
      } ~ pathPrefix(Segment) { calendarName =>
        val calendarIO = Calendars.find(calendarName).transact(transactor)
        val task = Task.fromIO(calendarIO).map {
          case Some(calendarDescription) =>
            (get & pathEnd) {
              complete(CalendarView.from(Calendar(calendarDescription)))
            } ~ (taskRoutes(calendarDescription)
            ~ resourceRoutes(calendarDescription)
            ~ counterRoutes(calendarDescription)
            ~ scheduleRoutes(calendarDescription))
          case None => complete(NotFound)
        }
        ctx =>
          task.runAsync.flatMap(route => route(ctx))
      }
    }
}
