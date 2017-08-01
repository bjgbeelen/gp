package shifts
package chance
package influencer

import task._
import counter._
import resource._
import schedule._

object CurrentAssignmentsInfluencer extends ChanceInfluencer {
  def apply(
      task: Task,
      counters: Seq[Counter],
      constraints: Map[Resource, ResourceConstraints],
      assignments: Map[Resource, Set[Task]]
  ): Map[Resource, Float] =
    constraints.map {
      case (resource, const) =>
        val chance = counters.select(task).foldLeft(1F) {
          case (acc, counter) =>
            val currentCount =
              counter.count(assignments.getOrElse(resource, Set.empty))
            val desiredCount = const.desiredNumberOfTasks(counter)
            if (desiredCount == 0) 0F
            else {
              val diff = (desiredCount - currentCount)
              // if (diff == 0) println(desiredCount, currentCount)
              acc * diff * 1F / desiredCount
            }
        }
        (resource -> chance)
    }.toMap
}
