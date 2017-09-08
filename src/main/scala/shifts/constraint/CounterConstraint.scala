package shifts
package constraint

import task._
import counter._

case class CounterConstraint(counter: Counter, desiredNumber: Int, hard: Boolean = true) extends Constraint {
  type U = Set[Task]
  val obeyed = Set[Task]()
  def violations(input: Set[Task])(implicit context: TaskContext): Set[Task] = {
    val counterTasks = input.filter(task => counter.appliesTo(task))
    if (counterTasks.size == desiredNumber) obeyed else counterTasks
  }
}
