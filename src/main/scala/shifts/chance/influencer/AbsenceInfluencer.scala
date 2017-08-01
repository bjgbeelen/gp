package shifts
package chance
package influencer

import task._
import counter._
import resource._
import schedule._

object AbsenceInfluencer extends ChanceInfluencer {
  def apply(
      task: Task,
      counters: Seq[Counter],
      constraints: Map[Resource, ResourceConstraints],
      assignments: Map[Resource, Set[Task]]
  ): Map[Resource, Float] =
    constraints.map {
      case (resource, const) =>
        val chance = if (const.absence.contains(task.dayId)) 0F else 1F
        (resource -> chance)
    }.toMap
}
