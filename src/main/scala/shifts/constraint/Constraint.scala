package shifts
package constraint

import task._
import counter._
import calendar._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

trait Constraint {
  type U
  val obeyed: U
  val hard: Boolean
  def violations(tasks: Set[Task])(implicit context: TaskContext): U
  def score(tasks: Set[Task])(implicit context: TaskContext): Int
}

object Constraint {
  implicit val constraintEncoder: Encoder[Constraint] = new Encoder[Constraint] {
    final def apply(constraint: Constraint): Json = {
      val json = constraint match {
        case x: CounterConstraint          => x.asJsonObject
        case x: AbsenceConstraint          => x.asJsonObject
        case x: ConnectionConstraint       => x.asJsonObject
        case x: OverlappingTasksConstraint => x.asJsonObject
        case x: WeekendDistanceConstraint  => Map("desiredDistance" -> x.desiredDistance).asJsonObject
        case x: WeekendTasksConstraint     => x.asJsonObject
      }
      json.add("type", constraint.getClass.getSimpleName.asJson).asJson
    }
  }

  def from(view: ConstraintView, calendar: Calendar, counters: Seq[Counter]): Constraint = view match {
    case view: AbsenceConstraintView =>
      AbsenceConstraint(
        absence = view.absence,
        hard = view.hard
      )
    case view: ConnectionConstraintView =>
      ConnectionConstraint(
        connectionDesired = view.connectionDesired,
        hard = view.hard
      )
    case view: CounterConstraintView =>
      CounterConstraint(
        counter = counters.filter(_.id == view.counterId).head,
        desiredNumber = view.desired,
        hard = view.hard
      )
    case view: OverlappingTasksConstraintView =>
      OverlappingTasksConstraint(hard = view.hard)
    case view: WeekendDistanceConstraintView =>
      WeekendDistanceConstraint(desiredDistance = view.desiredDistance, calendar = calendar, hard = view.hard)
    case view: WeekendTasksConstraintView =>
      WeekendTasksConstraint(desiredTasksPerWeekend = view.desiredTasksPerWeekend,
                             excludeNights = view.excludeNights,
                             hard = view.hard)
  }
}
