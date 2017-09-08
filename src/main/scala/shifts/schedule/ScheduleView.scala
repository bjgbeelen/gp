package shifts
package schedule

import task.TaskId
import resource.ResourceId
import constraint._

case class ScheduleView(assignments: Map[TaskId, ResourceId], resourceConstraints: Map[ResourceId, Seq[Constraint]])

object ScheduleView {
  def from(schedule: Schedule) = ScheduleView(
    assignments = schedule.assignments.map { case (t, r)         => (t.id -> r.id) },
    resourceConstraints = schedule.constraints.map { case (r, c) => (r.id -> c) }
  )
}
