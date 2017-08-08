package shifts
package task

import calendar._

case class Task private (id: TaskId,
                         day: Day,
                         label: String,
                         start: Minute,
                         end: Minute,
                         tags: Set[Tag]) { self =>
  require(start < end, "The start of the task should be earlier than its end")
  import Task._

  def updateLabel(label: String) = copy(label = label)
  def updateStart(start: Minute) = copy(start = start)
  def updateEnd(end: Minute) = copy(end = end)
  def addTags(newTags: Set[Tag]) = copy(tags = tags ++ newTags)

  def overlapsWith(task: Task): Boolean =
    this != task && this.day == task.day && this.start < task.end && this.end > task.start

  def is(descriptor: Task.Descriptor) = tags.contains(descriptor.tag)
}

object Task {
  sealed trait Descriptor {
    def tag: Tag = this.getClass().getSimpleName().toLowerCase.replace("$", "")
  }
  case object Weekend extends Descriptor
  case object Night extends Descriptor {
    override val tag = "nacht"
  }

  def apply(day: Day,
            label: String,
            start: Minute,
            end: Minute,
            tags: Set[Tag]): Task =
    Task(
      id = day.id + tags.mkString("_", "_", ""),
      day = day,
      label = label,
      start = start,
      end = end,
      tags = tags
    )
}
