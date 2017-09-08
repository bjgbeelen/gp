package shifts
package counter

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.Printer

import calendar.Calendar
import Data2018._

trait CounterRoutes extends FailFastCirceSupport {
  def counterRoutes(calendar: Calendar)(implicit printer: Printer) =
    pathPrefix("counters") {
      (get & (pathSingleSlash | pathEnd)) {
        complete(countersMap)
      }
    }
}
