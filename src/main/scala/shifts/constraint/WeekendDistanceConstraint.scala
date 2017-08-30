package shifts
package constraint

import calendar._
import task._

case class WeekendDistanceConstraint(desiredDistance: Int = 2,
                                calendar: Calendar,
                                hard: Boolean = true)
    extends Constraint {
  type U = Set[Week]
  val obeyed = Set[Week]()

  private def withinDistance(w1: Week, w2: Week): Boolean = {
    val distance = w1 distance w2
    distance > 0 && distance < desiredDistance
  }

  def violations(input: Set[Task])(implicit context: TaskContext): Set[Week] = {
    val weeks = input.toList.sortBy(_.day.id).map(context.taskWeek).sliding(2)
    weeks.collect{
      case List(w1, w2) if withinDistance(w1, w2) => w1
    }.toSet
    // val allWeeks = calendar.weeks.toList
    //   .sortBy { case w => (w.year, w.number) }
    //   .sliding(distance)
    //   .toList
    // // println(allWeeks.toList)
    // val inputWeeks = input
    //   .map { task =>
    //     (task, context.taskWeek(task))
    //   }
    //   .groupBy { case (_, week) => week }
    //   .map { case (week, tasks) => week }
    //   .toSeq
    //   .sortBy(w => (w.year, w.number))

    // inputWeeks.filter {
    //   case week =>
    //     val nextWeeks: List[Week] = allWeeks.flatMap {
    //       case `week` :: nextOnes => nextOnes
    //       case other => List.empty
    //     }
    //     nextWeeks.exists(_ in inputWeeks)
    // }.toSet

  }
}
