package shifts
package constraint

import task._
import calendar.Week
import Task._
import counter._

case class WeekendTasksConstraint(desiredTasksPerWeekend: Int, excludeNights: Boolean = true, hard: Boolean = false)
    extends Constraint {
  type U = Map[Week, Set[Task]]
  val obeyed = Map[Week, Set[Task]]()
  def violations(input: Set[Task])(implicit context: TaskContext): Map[Week, Set[Task]] = {
    val allWeekendTasks = input.filter(_.is(Weekend))
    context.weekTasks
      .map {
        case (week, tasks) => (week -> tasks.toSet.intersect(allWeekendTasks))
      }
      .filter {
        case (week, weekendTasks) =>
          val hasDesiredNumber = weekendTasks.size == desiredTasksPerWeekend
          val hasOneNight = excludeNights && weekendTasks.size == 1 && weekendTasks.head
            .is(Night)
          val hasNoTasks = weekendTasks.size == 0
          !(hasDesiredNumber || hasOneNight || hasNoTasks)
      }
      .toMap
  }
  def score(input: Set[Task])(implicit context: TaskContext): Int =
    violations(input).values.toList
      .map(real => Math.abs(desiredTasksPerWeekend - real.size))
      .sum
}
