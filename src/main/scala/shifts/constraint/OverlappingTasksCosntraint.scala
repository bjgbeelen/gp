package shifts
package constraint

import scala.annotation.tailrec

import task._

case class OverlappingTasksConstraint(hard: Boolean = true) extends Constraint {
  type U = Set[(Task, Task)]
  val obeyed = Set[(Task, Task)]()
  def violations(input: Set[Task])(implicit context: TaskContext): Set[(Task, Task)] = {
    @tailrec
    def go(tasks: List[Task], pairs: Set[(Task, Task)]): Set[(Task, Task)] =
      tasks match {
        case Nil => pairs
        case head :: tail =>
          go(
            tasks = tail,
            pairs = pairs ++ tail.map(task => (head, task)).filter {
              case (task1, task2) => task1.overlapsWith(task2)
            }
          )
      }
    go(input.toList, Set.empty)
  }
}
