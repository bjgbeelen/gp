package shifts
package plan

import calendar._
import task._
import resource._
import counter._
import constraint._
import schedule._

case class Plan(
    name: String,
    calendar: Calendar,
    tasks: TaskContext,
    resources: List[Resource],
    counters: Seq[Counter],
    constraints: Map[Resource, Seq[Constraint]],
    schedules: Seq[Schedule]
)
