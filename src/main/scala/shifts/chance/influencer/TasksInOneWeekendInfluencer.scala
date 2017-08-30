package shifts
package chance
package influencer

import task._
import Task._
import resource._
import schedule._

object TasksInOneWeekendInfluencer {
  def apply(
      weekTasks: Set[Task],
      desiredTasksInOneWeekend: Map[Resource, (Int, Boolean)],
      assignments: Map[Resource, Set[Task]]
  )(task: Task): Map[Resource, Float] = {
    desiredTasksInOneWeekend.map {
      case (resource, (desiredNumberOfWeekendTasks, excludeNights)) =>
        val chance: Float =
          if (task.is(Weekend)) {
            val resourceTasks = assignments.getOrElse(resource, Set.empty)
            val weekendTasks = weekTasks.filter(_.is(Weekend))
            val tasksInSameWeekend = weekendTasks intersect resourceTasks
            val nrOfNightTasks = tasksInSameWeekend.count(_.is(Night))

            if (tasksInSameWeekend.size > 0) {
              if (excludeNights && task.is(Night)) 0F
              else if (nrOfNightTasks > 0 && (excludeNights || task.is(Night)))
                0F
              else if (tasksInSameWeekend.size < desiredNumberOfWeekendTasks) {
                10000F
              } else {
                0.00001F
              }
            } else
              1F
          } else 1F
        (resource -> chance)
    }
  }
}
