package shifts
package chance
package influencer

import task._
import resource._
import schedule._
import calendar.DayId

case class RequiredAssignmentsInfluencer(shouldHaveTasks: Set[Task], shouldNotHaveTasks: Set[Task]) extends ChanceInfluencer {
  def chance(task: Task): Float = if (shouldNotHaveTasks contains task) 0F else 1F
}
