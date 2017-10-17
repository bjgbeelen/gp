package shifts
package constraint

import calendar.DayId
import task._

case class RequiredAssignmentsConstraint(
    shouldHaveTasks: Set[Task],
    shouldNotHaveTasks: Set[Task],
    hard: Boolean
) extends Constraint {
  type U = (Set[Task], Set[Task])
  val obeyed = (Set[Task](), Set[Task]())
  def violations(input: Set[Task])(implicit context: TaskContext): (Set[Task], Set[Task]) =
    (shouldHaveTasks diff input, shouldNotHaveTasks intersect input)
  def score(input: Set[Task])(implicit context: TaskContext): Int = violations(input) match {
    case (set1, set2) => set1.size + set2.size
  }
}
