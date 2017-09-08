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
}

object Constraint {
  implicit val constraintEncoder: Encoder[Constraint] = new Encoder[Constraint] {
    final def apply(constraint: Constraint): Json = {
      val foo = constraint match {
        case x: CounterConstraint          => x.asJson
        case x: AbsenceConstraint          => x.asJson
        case x: ConnectionConstraint       => x.asJson
        case x: OverlappingTasksConstraint => x.asJson
        case x: WeekendDistanceConstraint  => Map("desiredDistance" -> x.desiredDistance).asJson
        case x: WeekendTasksConstraint     => x.asJson
      }
      foo.asObject.map { _.add("type", constraint.getClass.getSimpleName.asJson).asJson }.get
      // foo += ("type" -> "CounterConstraint")
    }
  }
}
