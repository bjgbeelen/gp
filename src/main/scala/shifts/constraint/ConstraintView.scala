package shifts
package constraint

import task._
import resource.ResourceId
import constraint._
import calendar.{ DayId, WeekNumber }

import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

trait ConstraintView {
  val `type`: String
  val hard: Boolean
}

case class AbsenceConstraintView(`type`: String, absence: Set[DayId], violations: Seq[TaskId], hard: Boolean)
    extends ConstraintView
case class ConnectionConstraintView(`type`: String, connectionDesired: Boolean, violations: Seq[TaskId], hard: Boolean)
    extends ConstraintView
case class CounterConstraintView(`type`: String,
                                 counterId: String,
                                 desired: Int,
                                 violations: Seq[TaskId],
                                 hard: Boolean)
    extends ConstraintView
case class OverlappingTasksConstraintView(`type`: String, hard: Boolean, violations: Seq[(TaskId, TaskId)])
    extends ConstraintView
case class WeekendDistanceConstraintView(`type`: String, desiredDistance: Int, violations: Seq[String], hard: Boolean)
    extends ConstraintView
case class RequiredAssignmentsConstraintView(`type`: String,
                                             shouldHaveTasks: Set[TaskId],
                                             shouldNotHaveTasks: Set[TaskId],
                                             hard: Boolean)
    extends ConstraintView
case class WeekendTasksConstraintView(`type`: String,
                                      desiredTasksPerWeekend: Int,
                                      excludeNights: Boolean,
                                      violations: Map[String, Seq[TaskId]],
                                      hard: Boolean)
    extends ConstraintView

object ConstraintView {
  implicit val constraintViewEncoder: Encoder[ConstraintView] = new Encoder[ConstraintView] {
    final def apply(constraintView: ConstraintView): Json = constraintView match {
      case x: AbsenceConstraintView             => x.asJson
      case x: ConnectionConstraintView          => x.asJson
      case x: CounterConstraintView             => x.asJson
      case x: WeekendDistanceConstraintView     => x.asJson
      case x: OverlappingTasksConstraintView    => x.asJson
      case x: WeekendTasksConstraintView        => x.asJson
      case x: RequiredAssignmentsConstraintView => x.asJson
    }
  }

  implicit val constraintViewDecoder: Decoder[ConstraintView] = new Decoder[ConstraintView] {
    final def apply(c: HCursor): Decoder.Result[ConstraintView] =
      c.downField("type").as[String].flatMap { _type =>
        val decoder = _type match {
          case "AbsenceConstraint"             => implicitly[Decoder[AbsenceConstraintView]]
          case "ConnectionConstraint"          => implicitly[Decoder[ConnectionConstraintView]]
          case "CounterConstraint"             => implicitly[Decoder[CounterConstraintView]]
          case "WeekendDistanceConstraint"     => implicitly[Decoder[WeekendDistanceConstraintView]]
          case "OverlappingTasksConstraint"    => implicitly[Decoder[OverlappingTasksConstraintView]]
          case "WeekendTasksConstraint"        => implicitly[Decoder[WeekendTasksConstraintView]]
          case "RequiredAssignmentsConstraint" => implicitly[Decoder[RequiredAssignmentsConstraintView]]
        }
        decoder(c)
      }
  }

  def from(constraint: Constraint, tasks: Set[Task])(implicit context: TaskContext) = {
    val typeName = constraint.getClass.getSimpleName
    constraint match {
      case x: CounterConstraint =>
        CounterConstraintView(typeName,
                              counterId = x.counter.id,
                              desired = x.desiredNumber,
                              violations = x.violations(tasks).map(_.id).toList.sorted,
                              hard = x.hard)
      case x: AbsenceConstraint =>
        AbsenceConstraintView(typeName, x.absence, x.violations(tasks).map(_.id).toList.sorted, x.hard)
      case x: ConnectionConstraint =>
        ConnectionConstraintView(typeName,
                                 connectionDesired = x.connectionDesired,
                                 violations = x.violations(tasks).map(_.id).toList.sorted,
                                 hard = x.hard)
      case x: OverlappingTasksConstraint =>
        OverlappingTasksConstraintView(typeName,
                                       x.hard,
                                       x.violations(tasks).map { case (t1, t2) => (t1.id, t2.id) }.toList.sorted)
      case x: WeekendDistanceConstraint =>
        WeekendDistanceConstraintView(typeName,
                                      desiredDistance = x.desiredDistance,
                                      violations = x.violations(tasks).map(_.id).toList.sorted,
                                      x.hard)
      case x: RequiredAssignmentsConstraint =>
        RequiredAssignmentsConstraintView(typeName,
                                          shouldHaveTasks = x.shouldHaveTasks.map(_.id),
                                          shouldNotHaveTasks = x.shouldNotHaveTasks.map(_.id),
                                          hard = x.hard)
      case x: WeekendTasksConstraint =>
        WeekendTasksConstraintView(
          typeName,
          desiredTasksPerWeekend = x.desiredTasksPerWeekend,
          excludeNights = x.excludeNights,
          violations = x.violations(tasks).map { case (week, tasks) => (week.id -> tasks.map(_.id).toList.sorted) },
          hard = x.hard
        )
    }
  }
}
