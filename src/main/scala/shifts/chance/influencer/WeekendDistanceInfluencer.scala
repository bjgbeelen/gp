package shifts
package chance
package influencer

import task._
import calendar._
import resource._
import schedule._

case class WeekendDistanceInfluencer(desiredDistance: Int,
                                     hard: Boolean,
                                     calendar: Calendar,
                                     taskContext: TaskContext,
                                     assignments: Set[Task])
    extends ChanceInfluencer {
  def chance(task: Task): Float = {
    val taskWeek        = taskContext.taskWeek(task)
    val assignmentWeeks = assignments.map(taskContext.taskWeek)
    val distances = assignmentWeeks
      .map(_ distance taskWeek)
      .filter(distance => distance < desiredDistance && distance > 0)

    if (distances.isEmpty) 1F else if (hard) 0F else 0.0001F
  }
}
