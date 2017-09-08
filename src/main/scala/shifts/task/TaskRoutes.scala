package shifts
package task

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._

import calendar.Calendar
import Data2018._

trait TaskRoutes extends FailFastCirceSupport {
  def taskRoutes(calendar: Calendar) =
    pathPrefix("tasks") {
      (get & (pathSingleSlash | pathEnd)) {
        val taskMap: Map[String, Set[Task]] = tasks.groupBy(_.day.id)
        complete(taskMap)
      }
    }
}
