package shifts
package resource

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.Printer
import doobie._, doobie.implicits._
import cats.effect.IO

import monix.execution.Scheduler.Implicits.global
import monix.eval.Task

import repository.Resources
import calendar.CalendarDescription

trait ResourceRoutes extends FailFastCirceSupport {
  def resourceRoutes(calendar: CalendarDescription)(implicit printer: Printer, transactor: Transactor[IO]) =
    pathPrefix("resources") {
      (get & (pathSingleSlash | pathEnd)) {
        val resources = Resources
          .list(calendarName = calendar.name)
          .transact(transactor)
          .map(
            list =>
              list.map {
                case resource @ Resource(id, _, _) => (id -> resource)
              }.toMap
          )
        val task = Task.fromIO(resources)
        complete(task.runAsync)
      }
    }
}
