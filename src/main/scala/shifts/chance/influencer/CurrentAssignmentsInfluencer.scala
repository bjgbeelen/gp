package shifts
package chance
package influencer

import task._
import counter._
import resource._
import schedule._
import constraint._

case class CurrentAssignmentsInfluencer(counter: Counter, desiredNumberOfTasks: Int, assignments: Set[Task]) extends ChanceInfluencer {
  def chance(task: Task): Float = {
     val currentCount = counter.count(assignments)
            if (desiredNumberOfTasks == 0) 0F
            else (desiredNumberOfTasks - currentCount) / desiredNumberOfTasks
          }
}

// object CurrentAssignmentsInfluencer {
//   def apply(
//       counter: Counter,
//       desiredNumberOfTasks: Int,
//       assignments: Set[Task]
//   )(task: Task): Float = {
//      val currentCount = counter.count(assignments)
//             if (desiredNumberOfTasks == 0) 0F
//             else (desiredNumberOfTasks - currentCount) / desiredNumberOfTasks
//           }
// }
