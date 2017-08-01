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

case class Schedule(assignments: Map[Task, Resource]) {
  def tasks(resource: Resource): Set[Task] =
    assignments
      .filter {
        case (t, r) => r == resource
      }
      .keys
      .toSet
}

object Schedule {
  @tailrec
  def plan(tasks: List[Task],
           calendar: Calendar,
           counters: Seq[Counter],
           constraints: Map[Resource, ResourceConstraints],
           assignments: Map[Resource, Set[Task]] = Map.empty,
           retries: Int = 10,
           incompleteSchedules: List[IncompleteSchedule] = List.empty): Either[Seq[IncompleteSchedule], (Schedule, Seq[IncompleteSchedule])] = {

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
          CurrentAssignmentsInfluencer,
          AbsenceInfluencer,
          OverlappingTaskInfluencer
        )(task, counters, constraints, assignments)

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

    assignByChance(tasks, assignments) match {
      case Left(incomplete) if retries > 0 =>
        plan(tasks, calendar, counters, constraints, assignments, retries - 1, incomplete :: incompleteSchedules)
      case Right(schedule) => Right(schedule, incompleteSchedules)
      case Left(incomplete) => Left(incomplete :: incompleteSchedules)
    }
  }
}
