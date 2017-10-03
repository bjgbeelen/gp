package shifts
package counter

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.joda._

import task._

case class CounterView(id: String,
                       name: String,
                       groupName: String,
                       calendarName: String,
                       include: List[Tag],
                       exclude: List[Tag])
object CounterView {
  def from(calendarName: CalendarName)(counter: Counter) = CounterView(
    id = counter.id,
    name = counter.name,
    groupName = counter.groupName,
    calendarName = calendarName,
    include = counter.include.toList,
    exclude = counter.exclude.toList
  )
}

case class Counter private (id: String, name: String, groupName: String, include: Set[Tag], exclude: Set[Tag]) {
  def appliesTo(task: Task): Boolean =
    task.tags.intersect(include) == include && task.tags
      .intersect(exclude)
      .isEmpty

  def count(tasks: Set[Task]): Int = tasks.count(appliesTo)
}

object Counter {
  def withParent(name: String, include: Set[Tag], exclude: Set[Tag])(
      counters: Seq[Counter]
  ): Seq[Counter] =
    counters.map {
      case Counter(_id, _name, _, _include, _exclude) =>
        val newId = include.toList.sorted.mkString("_") + "_" + _id
        new Counter(newId, _name, name, include ++ _include, exclude ++ _exclude)
    }

  def from(view: CounterView) = Counter(
    id = view.id,
    name = view.name,
    groupName = view.groupName,
    include = view.include.toSet,
    exclude = view.exclude.toSet
  )

  def apply(name: String, include: Set[Tag] = Set.empty, exclude: Set[Tag] = Set.empty): Counter =
    new Counter(id = include.toList.sorted.mkString("_").toLowerCase,
                name = name,
                groupName = "",
                include = include,
                exclude = exclude)
}
