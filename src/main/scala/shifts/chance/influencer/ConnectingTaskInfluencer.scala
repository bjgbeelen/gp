package shifts
package chance
package influencer

import task._
import resource._
import schedule._

object ConnectingTaskInfluencer {
  type ConnectionDesired = Boolean
  type HardConstraint = Boolean
  def apply(
      resources: Map[Resource, (ConnectionDesired, HardConstraint)],
      assignments: Map[Resource, Set[Task]]
  )(task: Task): Map[Resource, Float] =
    resources.map {
      case (resource, (connectionDesired, hardConstraint)) =>
        val resourceTasks = assignments.getOrElse(resource, Set.empty)
        val chance = resourceTasks match {
          case tasks
              if tasks.exists(_.connectsWith(task)) == connectionDesired =>
            5F
          case _ if !hardConstraint => 0.1F
          case _ => 0F
        }
        (resource -> chance)
    }.toMap
}
