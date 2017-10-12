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

import scala.util.{ Success => TrySuccess, Failure }

import monix.execution.Scheduler.Implicits.{ global => taskScheduler }
import monix.eval.{ Task => MonixTask }

import calendar._
import task._
import resource._
import counter._
import resource.ResourceId
import constraint._
import Data2018._
import scala.concurrent._
import repository._

trait ScheduleRoutes extends FailFastCirceSupport {

  def scheduleRoutes(
      calendarDescription: CalendarDescription
  )(implicit ec: ExecutionContext, printer: Printer, transactor: Transactor[IO]) = {
    def scheduleViewDependentRoutes(view: ScheduleView) =
      (get & (pathSingleSlash | pathEnd))(complete(view)) ~
      (path("constraints") & put & entity(as[Map[ResourceId, Seq[ConstraintView]]])) { newConstraints =>
        val update = Schedules.update(newConstraints, view.name, calendarDescription.name).transact(transactor)
        val task = MonixTask.fromIO(update).map {
          case 1     => complete(NoContent)
          case other => complete(InternalServerError, s"Unexpected $other updates")
        }
        ctx =>
          task.runAsync.flatMap(_(ctx))
      } ~ path("scores") {
        val optionalSchedule = SolutionSearchManager.completeSolutions.get(view.name)
        optionalSchedule match {
          case Some(schedule) =>
            val str = schedule.resourceConstraints.map{ case (resource, constraints) =>
              val result = s"${resource.name} has the following scores:\n" + constraints.map{ constraint =>
                val score = constraint.score(schedule.assignments.collect{ case (t, r) if (r == resource) => t}.toSet)(schedule.context)
                s"${constraint.getClass.getSimpleName}: $score"
              }.mkString("\n")
              result
          }
            complete(str.mkString("\n\n"))
          case None => complete(NotFound -> "What?")
        }
      } ~ path("completeWeekends") {
        val calendar = Calendar(calendarDescription)
        val dbResults = for {
          tasks     <- Tasks.list(calendarDescription.name).map(_.map(view => Task.from(view, calendar)))
          counters  <- Counters.list(calendarDescription.name)
          resources <- Resources.list(calendarDescription.name)
          constraints <- view.resourceConstraints
            .map {
              case (resourceId, constraintsView) =>
                val resource = resources.filter(_.id == resourceId).head
                val constraints =
                  constraintsView.map(constraintView => Constraint.from(constraintView, calendar, counters))
                (resource, constraints)
            }
            .pure[ConnectionIO]
          assignments <- view.assignments.map {
             case (taskId, resourceId) =>
                val task = tasks.filter(_.id == taskId).head
                val resource = resources.filter(_.id == resourceId).head
                (task, resource)
          }.pure[ConnectionIO]
        } yield (tasks, counters, constraints,
            resources.map{ resource =>
              val (shouldHave, shouldNotHave) = assignments.partition{ case (_, _resource) => _resource == resource}
              resource -> RequiredAssignmentsConstraint(
                shouldHaveTasks = shouldHave.keys.toSet,
                shouldNotHaveTasks = shouldNotHave.keys.toSet,
                hard = true
              )
            }.toMap
          )

        val task = MonixTask.fromIO(dbResults.transact(transactor)).foreach {
          case (tasks, counters, constraints, currentAssignmentsConstraints) =>
            SolutionSearchManager.start(
              scheduleName = view.name,
              tasks = tasks.toList.filter(!_.tags.contains("ignore")),
              calendar = calendar,
              counters = counters,
              resourceConstraints = resourceConstraints.map{ case (resource, constraints) =>
                (resource, constraints :+ currentAssignmentsConstraints(resource))
              }
            )
        }

        complete("Calculating possible solutions...")
      } ~ {
        pathPrefix(Segment) { taskId =>
          (delete & (pathSingleSlash | pathEnd)) {
            val deleteAssignment = Assignments
              .delete(ScheduleTask(taskId, view.name, calendar.name))
              .transact(transactor)
            val task = MonixTask.fromIO(deleteAssignment).map(_ => NoContent)
            complete(task.runAsync)
          } ~
          pathPrefix(Segment) { resourceId =>
            post {
              val queries = for {
                _ <- Assignments.delete(ScheduleTask(taskId, view.name, calendarDescription.name))
                _ <- Assignments.insert(Assignment(taskId, resourceId, view.name, calendar.name))
              } yield ()
              val task = MonixTask.fromIO(queries.transact(transactor)).map(_ => Created)
              complete(task.runAsync)
            }
          }

        }
      }

    pathPrefix("schedules") {
      (get & (pathSingleSlash | pathEnd)) {
        val schedules = Schedules.list(calendarName = calendar.name)
        val task =
          MonixTask.fromIO(schedules.transact(transactor)).map(_ ++ SolutionSearchManager.calculatedSolutions.keys)
        complete(task.runAsync)
      } ~ pathPrefix(Segment) { scheduleName =>
        val scheduleView: IO[Option[ScheduleView]] =
          SolutionSearchManager.calculatedSolutions.get(scheduleName).pure[IO].flatMap {
            case Some(view) => Some(view).pure[IO]
            case None =>
              Schedules
                .find(ScheduleIdentifier(name = scheduleName, calendarName = calendar.name))
                .transact(transactor)
          }

        val task = MonixTask.fromIO(scheduleView).map {
          case Some(view) => scheduleViewDependentRoutes(view)
          case None       => complete(NotFound)
        }
        ctx =>
          task.runAsync.flatMap(_(ctx))
      }
    }
  }
}
