package shifts

import task._

package object counter {
  implicit class SeqCounterExtension(counters: Seq[Counter]) {
    def count(tasks: Set[Task]): Map[Counter, Int] =
      counters.map { counter =>
        (counter, counter.count(tasks))
      }.toMap
    def select(task: Task): Seq[Counter] = counters.filter(_.appliesTo(task))
  }

  implicit class SeqMapCounterExtension(counters: Seq[Map[Counter, Int]]) {
    def sumUp: Map[Counter, Int] = counters.fold(Map.empty) {
      case (result, counterMap) =>
        counterMap.map {
          case (counter, size) =>
            (counter, size + result.getOrElse(counter, 0))
        }.toMap
    }
  }

  implicit class MapCounterExtension(counters: Map[Counter, Int]) {
    def -(other: Map[Counter, Int]) =
      counters.map {
        case (counter, size) => (counter, size - other(counter))
      }.toMap
  }
}
