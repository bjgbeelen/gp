package shifts
package chance
package influencer

import task._
import counter._
import resource._
import schedule._
import constraint._

object CurrentAssignmentsInfluencer {
  def apply(
      counters: Seq[Counter],
      resourceDesiredNumberOfTasks: Map[Resource, Seq[CounterConstraint]],
      assignments: Map[Resource, Set[Task]]
  )(task: Task): Map[Resource, Float] =
    resourceDesiredNumberOfTasks.map {
      case (resource, constraints) =>
        val chance = counters.select(task).foldLeft(1F) {
          case (acc, counter) =>
            val currentCount =
              counter.count(assignments.getOrElse(resource, Set.empty))
            val desiredCount = constraints.collect {
              case CounterConstraint(`counter`, desiredCount, _) =>
                desiredCount
            }.head
            if (desiredCount == 0) 0F
            else acc * (desiredCount - currentCount) / desiredCount
        }
        (resource -> chance)
    }.toMap
}
