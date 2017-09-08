package shifts
package schedule

import task.TaskId
import resource.ResourceId
import constraint._

case class ScheduleView(
    assignments: Map[TaskId, ResourceId],
    resourceConstraints: Map[ResourceId, Seq[ConstraintView]],
    totalScore: Int
)

object ScheduleView {
  def from(schedule: Schedule) = ScheduleView(
    assignments = schedule.assignments.map { case (t, r) => (t.id -> r.id) },
    resourceConstraints = schedule.resourceConstraints.map {
      case (r, constraints) =>
        (r.id -> constraints.map(c => ConstraintView.from(c, schedule.tasks(r))(schedule.context)))
    },
    totalScore = schedule.totalScore
  )
}
