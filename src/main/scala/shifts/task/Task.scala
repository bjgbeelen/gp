package shifts
package task

import calendar.DayId

case class Task private (id: TaskId,
                         dayId: DayId,
                         label: String,
                         start: Minute,
                         end: Minute,
                         tags: Set[Tag]) {
  require(start < end, "The start of the task should be earlier than its end")

  def updateLabel(label: String) = copy(label = label)
  def updateStart(start: Minute) = copy(start = start)
  def updateEnd(end: Minute) = copy(end = end)
  def addTags(newTags: Set[Tag]) = copy(tags = tags ++ newTags)

  def overlapsWith(task: Task): Boolean =
    this != task && this.dayId == task.dayId && this.start < task.end && this.end > task.start
}

object Task {
  def apply(dayId: DayId,
            label: String,
            start: Minute,
            end: Minute,
            tags: Set[Tag]): Task =
    Task(
      id = dayId + tags.mkString("_", "_", ""),
      dayId = dayId,
      label = label,
      start = start,
      end = end,
      tags = tags
    )
}
