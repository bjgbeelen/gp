package shifts
package schedule

import task._
import resource._
import chance._

case class IncompleteSchedule(task: Task,
                              tasksToGo: List[Task],
                              assignments: Map[Resource, Set[Task]],
                              chanceCalculator: ChanceCalculator) {
  override def toString(): String = s"""
    Could not assign $task.
    ${tasksToGo.size} tasks to go after this one.\n
    $chanceCalculator
  """
}
