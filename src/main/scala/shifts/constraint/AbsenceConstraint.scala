package shifts
package constraint

import calendar.DayId
import task._

case class AbsenceConstraint(absence: Set[DayId], hard: Boolean = true)
    extends Constraint {
  type U = Set[Task]
  val obeyed = Set[Task]()
  def violations(input: Set[Task])(implicit context: TaskContext): Set[Task] =
    input.filter(task => absence.contains(task.day.id))
}
