package shifts
package chance
package influencer

import task._
import counter._
import resource._
import schedule._

object CurrentAssignmentsInfluencer {
  def apply(
      counters: Seq[Counter],
      resourceDesiredNumberOfTasks: Map[Resource, Map[Counter, Int]],
      assignments: Map[Resource, Set[Task]]
  )(task: Task): Map[Resource, Float] =
    resourceDesiredNumberOfTasks.map {
      case (resource, desiredNumberOfTasks) =>
        val chance = counters.select(task).foldLeft(1F) {
          case (acc, counter) =>
            val currentCount =
              counter.count(assignments.getOrElse(resource, Set.empty))
            val desiredCount = desiredNumberOfTasks(counter)
            if (desiredCount == 0) 0F
            else acc * (desiredCount - currentCount) / desiredCount
        }
        (resource -> chance)
    }.toMap
}
