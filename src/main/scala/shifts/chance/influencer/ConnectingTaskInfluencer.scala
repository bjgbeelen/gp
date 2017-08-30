package shifts
package chance
package influencer

import task._
import resource._
import schedule._

// object ConnectingTaskInfluencer {
//   type ConnectionDesired = Boolean
//   type HardConstraint = Boolean
//   def apply(
//       connectionDesired: Boolean,
//       hardConstraint: Boolean,
//       assignments: Set[Task]
//   )(task: Task): Float = assignments match {
//       case tasks
//           if tasks.exists(_.connectsWith(task)) == connectionDesired =>
//         5F
//       case _ if !hardConstraint => 0.1F
//       case _ => 0F
// }
// }

case class ConnectingTaskInfluencer(connectionDesired: Boolean, hardConstraint: Boolean, assignments: Set[Task]) extends ChanceInfluencer {
  def chance(task: Task): Float = assignments match {
      case tasks
          if tasks.exists(_.connectsWith(task)) == connectionDesired =>
        5F
      case _ if !hardConstraint => 0.1F
      case _ => 0F
  }
}
