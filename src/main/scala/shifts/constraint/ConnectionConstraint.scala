package shifts
package constraint

import calendar.DayId
import task._

case class ConnectionConstraint(connectionDesired: Boolean, hard: Boolean = true) extends Constraint {
  type U = Set[Task]
  val obeyed = Set[Task]()
  def violations(input: Set[Task])(implicit context: TaskContext): Set[Task] = {
    val assignmentsPerWeek = input
      .map { task =>
        (task, context.taskWeek(task))
      }
      .groupBy { case (_, week) => week }
      .map { case (week, tasks) => (week, tasks.map(_._1)) }

    assignmentsPerWeek.flatMap {
      case (week, tasks) =>
        tasks.foldLeft(Set[Task]()) {
          case (acc, task) =>
            val otherTasks = tasks - task
            if (otherTasks
                  .filter(_.connectsWith(task))
                  .nonEmpty != connectionDesired && otherTasks.nonEmpty)
              acc + task
            else acc
        }
    }.toSet
  }
  def score(input: Set[Task])(implicit context: TaskContext): Int = violations(input).size
}
