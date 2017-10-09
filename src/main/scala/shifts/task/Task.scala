package shifts
package task

import calendar._

case class TaskView(id: TaskId,
                    day: DayId,
                    calendarName: String,
                    label: String,
                    start: Minute,
                    end: Minute,
                    tags: List[Tag])

object TaskView {
  def from(task: Task) = TaskView(
    id = task.id,
    day = task.day.id,
    calendarName = task.day.calendar.name,
    label = task.label,
    start = task.start,
    end = task.end,
    tags = task.tags.toList
  )
}

case class Task private (id: TaskId, day: Day, label: String, start: Minute, end: Minute, tags: Set[Tag]) { self =>
  require(start < end, "The start of the task should be earlier than its end")
  import Task._

  def updateLabel(label: String) = copy(label = label)
  def updateStart(start: Minute) = copy(start = start)
  def updateEnd(end: Minute)     = copy(end = end)
  def addTags(newTags: Set[Tag]) = copy(tags = tags ++ newTags)

  def overlapsWith(task: Task): Boolean =
    this != task && this.day == task.day && this.start < task.end && this.end > task.start

  def connectsWith(task: Task, allowSwitch: Boolean = true): Boolean = {
    val sameDayConnection = this.day == task.day && this.end == task.start
    val overNightConnection = this.day.next
      .map(_ == task.day)
      .getOrElse(false) && this.end == 23 :: 0 && task.start == 0 :: 0
    sameDayConnection || overNightConnection || (allowSwitch && task
      .connectsWith(self, allowSwitch = false))
  }

  def is(descriptor: Task.Descriptor) = tags.contains(descriptor.tag)
}

object Task {
  sealed trait Descriptor {
    def tag: Tag = this.getClass().getSimpleName().toLowerCase.replace("$", "")
  }

  def from(view: TaskView, calendar: Calendar) = Task(
    id = view.id,
    day = calendar.filter(DayIdSelection(view.day)).head,
    label = view.label,
    start = view.start,
    end = view.end,
    tags = view.tags.toSet
  )

  case object Weekend extends Descriptor
  case object Feest   extends Descriptor
  case object Night extends Descriptor {
    override val tag = "nacht"
  }
  case object Morning extends Descriptor {
    override val tag = "ochtend"
  }
  case object Evening extends Descriptor {
    override val tag = "avond"
  }

  def apply(day: Day, label: String, start: Minute, end: Minute, tags: Set[Tag]): Task =
    Task(
      id = day.id + tags.mkString("_", "_", ""),
      day = day,
      label = label,
      start = start,
      end = end,
      tags = tags
    )
}
