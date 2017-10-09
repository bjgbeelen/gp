package shifts
package task

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.Printer

import monix.execution.Scheduler.Implicits.global
import monix.eval.{ Task => MonixTask }

import calendar.CalendarDescription
import repository.Tasks
import doobie._, doobie.implicits._
import cats.effect.IO

trait TaskRoutes extends FailFastCirceSupport {
  def taskRoutes(calendar: CalendarDescription)(implicit printer: Printer, transactor: Transactor[IO]) =
    pathPrefix("tasks") {
      (get & (pathSingleSlash | pathEnd)) {
        val tasks   = MonixTask.fromIO(Tasks.list(calendar.name).transact(transactor))
        val taskMap = tasks.map(_.groupBy(_.day))
        complete(taskMap.runAsync)
      }
    }
}
