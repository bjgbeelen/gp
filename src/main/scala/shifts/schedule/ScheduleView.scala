package shifts
package schedule

import task.TaskId
import resource.ResourceId
import constraint._

case class ScheduleView(
    name: String,
    calendarName: String,
    assignments: Map[TaskId, ResourceId],
    resourceConstraints: Map[ResourceId, Seq[ConstraintView]],
    totalScore: Int
) {
  def updateConstraints(map: Map[ResourceId, Seq[ConstraintView]]): ScheduleView = {
    val updatedConstraints = map.foldLeft(resourceConstraints) {
      case (acc, item) => acc + item
    }
    copy(resourceConstraints = updatedConstraints)
  }
}

object ScheduleView {
  def from(schedule: Schedule) = ScheduleView(
    name = schedule.name,
    assignments = schedule.assignments.map { case (t, r) => (t.id -> r.id) },
    resourceConstraints = schedule.resourceConstraints.map {
      case (r, constraints) =>
        (r.id -> constraints.map(c => ConstraintView.from(c, schedule.tasks(r))(schedule.context)))
    },
    totalScore = schedule.totalScore,
    calendarName = schedule.calendar.name
  )
}
