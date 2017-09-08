package shifts
package resource

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.Printer

import calendar.Calendar
import Data2018._

trait ResourceRoutes extends FailFastCirceSupport {
  def resourceRoutes(calendar: Calendar)(implicit printer: Printer) =
    pathPrefix("resources") {
      (get & (pathSingleSlash | pathEnd)) {
        val resourceMap: Map[String, Resource] = resources.map {
          case resource @ Resource(id, _, _) => (id -> resource)
        }.toMap
        complete(resourceMap)
      }
    }
}
