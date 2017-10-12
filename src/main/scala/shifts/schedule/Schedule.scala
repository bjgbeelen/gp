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

case class Schedule(
    name: String,
    calendar: Calendar,
    assignments: Map[Task, Resource],
    resourceConstraints: Map[Resource, Seq[Constraint]] = Map.empty
)(implicit val taskContext: TaskContext) {
  def context = taskContext

  def tasks(resource: Resource): Set[Task] =
    assignments.collect{ case (t, r) if r == resource => t}.toSet

  def totalScore: Int =
    resourceConstraints.map {
      case (resource, constraints) =>
        constraints.foldLeft(0) {
          case (acc, constraint) => acc + constraint.score(tasks(resource))
        }
    }.sum
}

case class ScheduleRunResult(incomplete: Seq[IncompleteSchedule], complete: Seq[Schedule]) {
  def merge(other: ScheduleRunResult) =
    ScheduleRunResult(incomplete = incomplete ++ other.incomplete, complete = complete ++ other.complete)
}

object Schedule {

  def plan(
      tasks: List[Task],
      calendar: Calendar,
      counters: Seq[Counter],
      resourceConstraints: Map[Resource, Seq[Constraint]],
      assignments: Map[Resource, Set[Task]] = Map.empty
  )(implicit context: TaskContext): Either[IncompleteSchedule, Schedule] = {

    @tailrec
    def assignByChance(
        tasks: List[Task],
        assignments: Map[Resource, Set[Task]]
    ): Either[IncompleteSchedule, Schedule] = {
      val taskResourceMap = assignments.foldLeft(Map[Task, Resource]()) {
        case (result, (k, v)) => result ++ v.map(_ -> k).toMap
      }
      tasks match {
        case Nil =>
          Right(
            Schedule(
              name = "dummy",
              calendar = calendar,
              taskResourceMap,
              resourceConstraints
            )
          )
        case task :: rest =>
          val influencers: Map[Resource, Seq[ChanceInfluencer]] =
            resourceConstraints.map {
              case (resource, constraints) =>
                val resourceAssignments =
                  assignments.getOrElse(resource, Set.empty)
                (resource -> constraints.map {
                  case CounterConstraint(counter, desiredCount, _) =>
                    CounterInfluencer(counter, desiredCount, resourceAssignments)
                  case AbsenceConstraint(absence, _) =>
                    AbsenceInfluencer(absence)
                  case RequiredAssignmentsConstraint(shouldHave, shouldNotHave, _) =>
                    RequiredAssignmentsInfluencer(shouldHave, shouldNotHave)
                  case ConnectionConstraint(connectionDesired, hard) =>
                    ConnectingTaskInfluencer(connectionDesired, hard, resourceAssignments)
                  case OverlappingTasksConstraint(hard) =>
                    OverlappingTaskInfluencer(resourceAssignments)
                  case WeekendDistanceConstraint(desiredDistance, _, hard) =>
                    WeekendDistanceInfluencer(desiredDistance, hard, calendar, context, resourceAssignments)
                  case WeekendTasksConstraint(desiredTasksPerWeekend, excludeNights, _) =>
                    val weekTasks = context.weekTasks(task.week).toSet
                    TasksInOneWeekendInfluencer(weekTasks, desiredTasksPerWeekend, excludeNights, resourceAssignments)
                })
            }
          val chanceCalculator = ChanceCalculator(influencers)(task)

          ResourcePicker(chanceCalculator).pick() match {
            case Some(resource) =>
              val updatedResourceTasks =
              assignments.getOrElse(resource, Set.empty) + task
              assignByChance(tasks = rest, assignments = assignments + (resource -> updatedResourceTasks))
            case None =>
              Left(
                IncompleteSchedule(
                  task = task,
                  tasksToGo = rest,
                  assignments = assignments,
                  chanceCalculator = chanceCalculator
                )
              )
          }
      }
    }

    val completeAssignments: Map[Resource, Set[Task]] =
      resourceConstraints.map {
        case (resource, _) =>
          (resource, assignments.getOrElse(resource, Set.empty))
      }
    assignByChance(tasks, completeAssignments)
  }

  def run(
      tasks: List[Task],
      calendar: Calendar,
      counters: Seq[Counter],
      resourceConstraints: Map[Resource, Seq[Constraint]],
      assignments: Map[Resource, Set[Task]] = Map.empty,
      runs: Int = 200,
      parallel: Int = 8
  )(implicit context: TaskContext, executionContext: ExecutionContext): Future[ScheduleRunResult] = {
    val results: Seq[Future[ScheduleRunResult]] = (1 to parallel).map { _ =>
      Future {
        val (lefts, rights) = (1 to runs)
          .map { iteration =>
            val result = plan(tasks, calendar, counters, resourceConstraints, assignments)
            println(s"finished $iteration: ${result.getClass.getSimpleName}")
            result
          }
          .partition(_.isLeft)
        ScheduleRunResult(incomplete = lefts.map(_.left.get), complete = rights.map(_.right.get))
      }
    }
    Future.sequence(results).map { runResults =>
      println("komt hier wel")
      val result = runResults.foldLeft(ScheduleRunResult(Seq.empty, Seq.empty)) {
        case (acc, runResult) => acc.merge(runResult)
      }
      println("done making result")
      result
    }
  }
}
