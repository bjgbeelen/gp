package shifts
package constraint

import task._

trait Constraint {
  type U
  val obeyed: U
  val hard: Boolean
  def violations(tasks: Set[Task])(implicit context: TaskContext): U
}
