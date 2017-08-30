package shifts
package chance
package influencer

import task._
import resource._
import schedule._

case class OverlappingTaskInfluencer(assignments: Set[Task]) extends ChanceInfluencer {
  def chance(task: Task): Float = if (assignments.exists(_.overlapsWith(task))) 0F else 1F
}

// object OverlappingTaskInfluencer {
//   def apply(
//       assignments: Set[Task]
//   )(task: Task): Float = if (assignments.exists(_.overlapsWith(task))) 0F else 1F
// }
