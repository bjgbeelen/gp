package shifts
package chance
package influencer

import task._
import resource._
import schedule._

object OverlappingTaskInfluencer {
  def apply(
      resources: Seq[Resource],
      assignments: Map[Resource, Set[Task]]
  )(task: Task): Map[Resource, Float] =
    resources.map { resource =>
      val chance =
        if (assignments
              .getOrElse(resource, Set.empty)
              .exists(_.overlapsWith(task))) 0F
        else 1F
      (resource -> chance)
    }.toMap
}
