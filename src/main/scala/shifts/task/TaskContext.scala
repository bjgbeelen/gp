package shifts
package task

import calendar.Week
import Task._

case class TaskContext(private val input: Seq[Task]) { self =>
  val tasks = input.sortBy { task =>
    (task.day.id, task.start, task.label)
  }

  val nextTasks: Map[Task, Task] =
    tasks.sliding(2).map { case Seq(t1, t2) => (t1, t2) }.toMap

  val previousTasks: Map[Task, Task] =
    tasks.sliding(2).map { case Seq(t1, t2) => (t2, t1) }.toMap

  lazy val taskWeek: Map[Task, Week] = {
    var tmpMap: Map[Task, Week] = Map.empty
    tasks.map { task =>
      val week = if (task.is(Weekend)) {
        previousTasks.get(task).flatMap { previousTask =>
          if ((previousTask.day == task.day) || (previousTask.day.next == Some(task.day)))
            tmpMap.get(previousTask)
          else None
        }
      }.getOrElse(task.day.week)
      else task.day.week
      tmpMap += (task -> week)
      (task -> week)
    }
  }.toMap

  lazy val weekTasks: Map[Week, Seq[Task]] = tasks.map { task =>
    val week = taskWeek(task)
    (week, tasks.filter(other => taskWeek(other) == week))
  }.toMap

  // lazy val relatedWeekTasks: Map[Task, Seq[Task]] = tasks.map{ task =>
  //   (task, tasks.filter(_.day.partialWeek == task.day.partialWeek))
  // }.toMap
}
