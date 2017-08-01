package shifts
package chance
package influencer

import task._
import counter._
import resource._
import schedule._

object OverlappingTaskInfluencer extends ChanceInfluencer {
  def apply(
      task: Task,
      counters: Seq[Counter],
      constraints: Map[Resource, ResourceConstraints],
      assignments: Map[Resource, Set[Task]]
  ): Map[Resource, Float] =
    constraints.map {
      case (resource, constraint) =>
        val chance =
          if (assignments
                .getOrElse(resource, Set.empty)
                .exists(_.overlapsWith(task))) 0F
          else 1F
        (resource -> chance)
    }.toMap
}
