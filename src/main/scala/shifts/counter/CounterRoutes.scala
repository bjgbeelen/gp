package shifts
package counter

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.Printer
import doobie._, doobie.implicits._
import cats.effect.IO

import monix.execution.Scheduler.Implicits.global
import monix.eval.Task

import calendar.CalendarDescription
import repository.Counters

trait CounterRoutes extends FailFastCirceSupport {
  def counterRoutes(calendar: CalendarDescription)(implicit printer: Printer, transactor: Transactor[IO]) =
    pathPrefix("counters") {
      (get & (pathSingleSlash | pathEnd)) {
        val counters = Task.fromIO(Counters.list(calendarName = calendar.name).transact(transactor))
        complete(counters.map(_.groupBy(_.groupName)).runAsync)
      }
    }
}
