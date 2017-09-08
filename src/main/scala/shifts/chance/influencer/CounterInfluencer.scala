package shifts
package chance
package influencer

import task._
import counter._
import resource._
import schedule._
import constraint._

case class CounterInfluencer(counter: Counter, desiredNumberOfTasks: Int, assignments: Set[Task])
    extends ChanceInfluencer {
  def chance(task: Task): Float =
    if (counter.appliesTo(task)) {
      val currentCount = counter.count(assignments)
      if (desiredNumberOfTasks == 0) 0F
      else 1F * (desiredNumberOfTasks - currentCount) / desiredNumberOfTasks
    } else 1F
}
