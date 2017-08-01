package shifts
package counter

import task._

case class Counter(name: String,
                   include: Set[Tag] = Set.empty,
                   exclude: Set[Tag] = Set.empty) {
  def appliesTo(task: Task): Boolean =
    task.tags.intersect(include) == include && task.tags
      .intersect(exclude)
      .isEmpty

  def count(tasks: Set[Task]): Int = tasks.count(appliesTo)
}

object Counter {
  def withParent(name: String, include: Set[Tag], exclude: Set[Tag])(
      counters: Seq[Counter]): Seq[Counter] = counters.map {
    case Counter(_name, _include, _exclude) =>
      Counter(s"${name}_${_name}", include ++ _include, exclude ++ _exclude)
  }
}
