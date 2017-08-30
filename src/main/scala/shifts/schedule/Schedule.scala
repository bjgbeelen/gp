package shifts
package schedule

import scala.annotation.tailrec
import util.Either

import task._
import calendar._
import resource._
import counter._
import chance.influencer._
import chance._
import constraint._
import concurrent._

case class Schedule(assignments: Map[Task, Resource]) {
  def tasks(resource: Resource): Set[Task] =
    assignments
      .filter {
        case (t, r) => r == resource
      }
      .keys
      .toSet
}

case class ScheduleRunResult(incomplete: Seq[IncompleteSchedule],
                             complete: Seq[Schedule]) {
  def merge(other: ScheduleRunResult) =
    ScheduleRunResult(incomplete = incomplete ++ other.incomplete,
                      complete = complete ++ other.complete)
}

object Schedule {

  def plan(tasks: List[Task],
           calendar: Calendar,
           counters: Seq[Counter],
           resourceConstraints: Map[Resource, Seq[Constraint]],
           assignments: Map[Resource, Set[Task]] = Map.empty)(
      implicit context: TaskContext): Either[IncompleteSchedule, Schedule] = {

    @tailrec
    def assignByChance(tasks: List[Task],
                       assignments: Map[Resource, Set[Task]])
      : Either[IncompleteSchedule, Schedule] = tasks match {
      case Nil =>
        val initialValue = Map[Task, Resource]()
        Right(Schedule(assignments.foldLeft(initialValue) {
          case (result, (k, v)) => result ++ v.map(_ -> k).toMap
        }))
      case task :: rest =>
        val chanceCalculator = ChanceCalculator(
          "current assignments" -> CurrentAssignmentsInfluencer(
            counters,
            resourceConstraints.map {
              case (resource, constraints) =>
                (resource -> constraints.collect {
                  case counterConstraint: CounterConstraint =>
                    counterConstraint
                })
            }.toMap,
            assignments
          ),
          "absence" -> AbsenceInfluencer(resourceConstraints.map {
            case (resource, constraints) =>
              val absence = constraints.collect {
                case AbsenceConstraint(absence, _) => absence
              }.head
              (resource -> absence)
          }),
          "overlapping tasks" -> OverlappingTaskInfluencer(
            resourceConstraints.keys.toList,
            assignments),
          "connecting tasks" -> ConnectingTaskInfluencer(
            resourceConstraints.map {
              case (resource, constraints) =>
                val input = constraints.collect {
                  case ConnectionConstraint(desired, hard) => (desired, hard)
                }.head
                (resource, input)
            }.toMap,
            assignments
          ),
          "tasks in one weekend" -> TasksInOneWeekendInfluencer(
            weekTasks = context.weekTasks(task.week).toSet,
            desiredTasksInOneWeekend = resourceConstraints.map {
              case (resource, constraints) =>
                val desiredWeekendTasks = constraints.collect {
                  case WeekendTasksConstraint(desired, excludeNights, _) =>
                    (desired, excludeNights)
                }.head
                (resource -> desiredWeekendTasks)
            }.toMap,
            assignments = assignments
          ),
          "weekend distances" -> WeekendDistanceInfluencer(
            desiredDistances = resourceConstraints.map{
              case (resource, constraints) =>
                val desiredDistance = constraints.collect {
                  case WeekendDistanceConstraint(desired, _, hard) => desired
                }.head
                (resource -> desiredDistance)
            }.toMap,
            hard = false,
            calendar = calendar,
            taskContext = context,
            assignments = assignments
          )
        )(task)

        ResourcePicker(chanceCalculator).pick() match {
          case Some(resource) =>
            val updatedResourceTasks =
              assignments.getOrElse(resource, Set.empty) + task
            assignByChance(
              tasks = rest,
              assignments = assignments + (resource -> updatedResourceTasks))
          case None =>
            Left(
              IncompleteSchedule(
                task = task,
                tasksToGo = rest,
                assignments = assignments,
                chanceCalculator = chanceCalculator
              ))
        }
    }

    val completeAssignments: Map[Resource, Set[Task]] =
      resourceConstraints.map {
        case (resource, _) =>
          (resource, assignments.getOrElse(resource, Set.empty))
      }
    assignByChance(tasks, completeAssignments)
  }

  def run(tasks: List[Task],
          calendar: Calendar,
          counters: Seq[Counter],
          resourceConstraints: Map[Resource, Seq[Constraint]],
          assignments: Map[Resource, Set[Task]] = Map.empty,
          runs: Int = 200,
          parallel: Int = 8)(
      implicit context: TaskContext,
      executionContext: ExecutionContext): Future[ScheduleRunResult] = {
    val results: Seq[Future[ScheduleRunResult]] = (1 to parallel).map { _ =>
      Future {
        val (lefts, rights) = (1 to runs)
          .map { _ =>
            plan(tasks, calendar, counters, resourceConstraints, assignments)
          }
          .partition(_.isLeft)
        ScheduleRunResult(incomplete = lefts.map(_.left.get),
                          complete = rights.map(_.right.get))
      }
    }
    Future.sequence(results).map { runResults =>
      runResults.foldLeft(ScheduleRunResult(Seq.empty, Seq.empty)) {
        case (acc, runResult) => acc.merge(runResult)
      }
    }
  }
}
