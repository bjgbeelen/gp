package shifts
package calendar

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._

import task.TaskRoutes
import resource.ResourceRoutes
import counter.CounterRoutes
import schedule.ScheduleRoutes
import Data2018._
import scala.concurrent._

trait CalendarRoutes
    extends FailFastCirceSupport
    with TaskRoutes
    with ResourceRoutes
    with CounterRoutes
    with ScheduleRoutes {
  def calendarRoutes(implicit ec: ExecutionContext) =
    pathPrefix("calendars") {
      (get & (pathSingleSlash | pathEnd)) {
        complete(
          calendars.map(_.name)
        )
      } ~ pathPrefix(Segment) { calendarName =>
        val calendar = calendars.find(_.name == calendarName).get
        (get & pathEnd) {
          complete(CalendarView.from(calendar))
        } ~ taskRoutes(calendar) ~ resourceRoutes(calendar) ~ counterRoutes(calendar) ~ scheduleRoutes(calendar)
      }
    }
}
