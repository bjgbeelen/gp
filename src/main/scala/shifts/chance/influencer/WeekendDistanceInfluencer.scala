package shifts
package chance
package influencer

import task._
import calendar._
import resource._
import schedule._

object WeekendDistanceInfluencer {
  def apply(
      desiredDistances: Map[Resource, Int],
      hard: Boolean,
      calendar: Calendar,
      taskContext: TaskContext,
      assignments: Map[Resource, Set[Task]]
  )(task: Task): Map[Resource, Float] =
    assignments.map {
      case (resource, tasks) =>
        val desiredDistance = desiredDistances(resource)
        val taskWeek = taskContext.taskWeek(task)
        val assignmentWeeks = tasks.map(taskContext.taskWeek)
        val distances = assignmentWeeks
          .map(_ distance taskWeek)
          .filter(distance => distance < desiredDistance && distance > 0)
        val chance =
          if (distances.isEmpty) 1F else
            if (hard) 0F else 0.0001F
        (resource -> chance)
    }
}
