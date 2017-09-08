package shifts
package counter

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.joda._

import task._

case class Counter private (id: String, name: String, include: Set[Tag], exclude: Set[Tag]) {
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
      case Counter(_id, _name, _include, _exclude) =>
        val newId = include.toList.sorted.mkString("_") + "_" + _id
        new Counter(newId, _name, include ++ _include, exclude ++ _exclude)
    }

  def apply(name: String, include: Set[Tag] = Set.empty, exclude: Set[Tag] = Set.empty): Counter =
    new Counter(id = include.toList.sorted.mkString("_").toLowerCase,
                name = name,
                include = include,
                exclude = exclude)
}
