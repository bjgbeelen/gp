package shifts
package constraint

import task._
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
}
