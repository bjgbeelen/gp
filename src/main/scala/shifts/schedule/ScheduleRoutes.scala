package shifts
package schedule

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.Printer
import io.circe._
import doobie._, doobie.implicits._
import cats.effect.IO
import cats.implicits._

import monix.execution.Scheduler.Implicits.global
import monix.eval.Task

import calendar.CalendarDescription
import resource.ResourceId
import constraint._
import Data2018._
import scala.concurrent._
import repository._

trait ScheduleRoutes extends FailFastCirceSupport {
  def scheduleRoutes(
      calendar: CalendarDescription
  )(implicit ec: ExecutionContext, printer: Printer, transactor: Transactor[IO]) = {

    def scheduleViewDependentRoutes(view: ScheduleView) =
      (get & (pathSingleSlash | pathEnd))(complete(view)) ~
      (path("constraints") & put & entity(as[Map[ResourceId, Seq[ConstraintView]]])) { newConstraints =>
          val update = Schedules.update(newConstraints, view.name, calendar.name).transact(transactor)
          val task = Task.fromIO(update).map {
            case 1 => complete(NoContent)
            case other => complete(InternalServerError, s"Unexpected $other updates")
          }
          ctx => task.runAsync.flatMap(_(ctx))
      } ~ {
          pathPrefix(Segment) { taskId =>
            (delete & (pathSingleSlash | pathEnd)) {
              val deleteAssignment = Assignments
                .delete(ScheduleTask(taskId, view.name, calendar.name))
                .transact(transactor)
              val task = Task.fromIO(deleteAssignment).map(_ => NoContent)
              complete(task.runAsync)
            } ~
            pathPrefix(Segment) { resourceId =>
              post {
                val queries = for {
                  _ <- Assignments.delete(ScheduleTask(taskId, view.name, calendar.name))
                  _ <- Assignments.insert(Assignment(taskId, resourceId, view.name, calendar.name))
                } yield ()
                val task = Task.fromIO(queries.transact(transactor)).map(_ => Created)
                complete(task.runAsync)
              }
            }

          }
        }

    pathPrefix("schedules") {
      (get & (pathSingleSlash | pathEnd)) {
        val schedules = Schedules.list(calendarName = calendar.name)
        val task      = Task.fromIO(schedules.transact(transactor))
        complete(task.runAsync)
      } ~ pathPrefix(Segment) { scheduleName =>
          val scheduleView = Schedules
              .find(ScheduleIdentifier(name = scheduleName, calendarName = calendar.name))
              .transact(transactor)
            val task = Task.fromIO(scheduleView).map{
              case Some(view) => scheduleViewDependentRoutes(view)
              case None => complete(NotFound)
            }
            ctx => task.runAsync.flatMap(_(ctx))
        }
    }
  }
}

//   def scheduleViewDependentRoutes()
//           )
//         }
//       }
//     }
// }
