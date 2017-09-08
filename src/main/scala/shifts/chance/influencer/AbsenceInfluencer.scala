package shifts
package chance
package influencer

import task._
import resource._
import schedule._
import calendar.DayId

case class AbsenceInfluencer(absence: Set[DayId]) extends ChanceInfluencer {
  def chance(task: Task): Float = if (absence.contains(task.day.id)) 0F else 1F
}
