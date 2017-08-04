package shifts
package chance
package influencer

import task._
import resource._
import schedule._
import calendar.DayId

object AbsenceInfluencer {
  def apply(
      resourceAbsence: Map[Resource, Set[DayId]]
  )(task: Task): Map[Resource, Float] =
    resourceAbsence.map {
      case (resource, absence) =>
        val chance = if (absence.contains(task.day.id)) 0F else 1F
        (resource -> chance)
    }.toMap
}
