package shifts
package chance
package influencer

import task._
import resource._
import schedule._
import calendar.DayId

// object AbsenceInfluencer {
//   def apply(
//       absence: Set[DayId]
//   )(task: Task): Float = if (absence.contains(task.day.id)) 0F else 1F
// }

case class AbsenceInfluencer(absence: Set[DayId]) extends ChanceInfluencer {
  def chance(task: Task): Float = if (absence.contains(task.day.id)) 0F else 1F
}
