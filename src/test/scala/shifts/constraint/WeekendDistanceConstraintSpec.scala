package shifts
package constraint

import calendar._
import task._
import Task._
import org.scalatest._

class WeekendDistanceConstraintSpec extends WordSpec with Matchers {
  import Data2018._

  implicit val taskContext = TaskContext(tasks.toSeq)
  val weeks: Seq[Week] =
    calendar.weeks.sortBy { case w => (w.year, w.number) }.take(6)
  val constraint =
    WeekendDistanceConstraint(desiredDistance = 2, calendar = calendar)

  "WeekendGapConstraint" should {
    "detect when the weekend gap constraint is violated" in {
      val assignments = weeks.map(w => taskContext.weekTasks(w).head).toSet
      val violations = weeks.take(5).toSet
      constraint.violations(assignments) shouldBe violations
    }

    "detect when the weekend gap constraint is maintained" in {
      val assignments = weeks.zipWithIndex
        .filter { case (_, index) => index % 2 == 0 }
        .map { case (w, _) => taskContext.weekTasks(w).head }
        .toSet
      val violations = Set[Week]()
      constraint.violations(assignments) shouldBe violations
    }
  }

}
