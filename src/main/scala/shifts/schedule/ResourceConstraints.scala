package shifts
package schedule

import resource._
import calendar._
import counter._

case class ResourceConstraints(
    resourceId: ResourceId,
    desiredNumberOfTasks: Map[Counter, Int],
    desiredNumberOfTasksInOneWeekend: Int,
    wantsEveningNightCombination: Boolean,
    wantsCoupledTasks: Boolean,
    absence: Set[DayId] = Set.empty
)
