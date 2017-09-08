package shifts
package schedule

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.Printer
import io.circe._

import calendar.Calendar
import resource.ResourceId
import constraint._
import Data2018._
import scala.concurrent._

trait ScheduleRoutes extends FailFastCirceSupport {
  def scheduleRoutes(calendar: Calendar)(implicit ec: ExecutionContext, printer: Printer) =
    pathPrefix("schedules") {
      (get & (pathSingleSlash | pathEnd)) {
        complete(schedules.map(_.map(_.name)))
      } ~ {
        path(Segment) { name =>
          val schedule: Future[Schedule] = schedules.map(_.find(_.name == name).head)
          complete(schedule.map(ScheduleView.from))
        }
      }
    }
}
