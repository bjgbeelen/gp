package shifts
package chance
package influencer

import task._
import Task._
import resource._
import schedule._

case class TasksInOneWeekendInfluencer(weekTasks: Set[Task],
                                       desiredTasksInOneWeekend: Int,
                                       excludeNights: Boolean,
                                       assignments: Set[Task])
    extends ChanceInfluencer {
  def chance(task: Task): Float =
    if (task.is(Weekend)) {
      val weekendTasks       = weekTasks.filter(_.is(Weekend))
      val tasksInSameWeekend = weekendTasks intersect assignments
      val nrOfNightTasks     = tasksInSameWeekend.count(_.is(Night))

      if (tasksInSameWeekend.size > 0) {
        if (excludeNights && task.is(Night)) 0F
        else if (nrOfNightTasks > 0 && (excludeNights || task.is(Night)))
          0F
        else if (tasksInSameWeekend.size < desiredTasksInOneWeekend) {
          10000F
        } else {
          0.00001F
        }
      } else
        1F
    } else 1F
}
