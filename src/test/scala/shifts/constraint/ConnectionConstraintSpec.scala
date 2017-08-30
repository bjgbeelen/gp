package shifts
package constraint

import calendar._
import task._
import Task._
import org.scalatest._

class ConnectionConstraintSpec extends WordSpec with Matchers {
  import Data2018._
  val task1 = tasks
    .filter(task => task.day.label == "1e Kerstdag" && task.is(Morning))
    .head
  val task2 = tasks
    .filter(task => task.day.label == "1e Kerstdag" && task.is(Evening))
    .head
  val task3 = tasks
    .filter(task => task.day.label == "2e Kerstdag" && task.is(Evening))
    .head
  val task4 = tasks
    .filter(task => task.day.label == "1e paasdag" && task.is(Evening))
    .head
  implicit val context = TaskContext(tasks.toSeq)

  "ConnectionConstraint" should {
    "detect tasks that don't connect when connection is desired" in {
      val constraint =
        ConnectionConstraint(connectionDesired = true, hard = false)
      constraint.violations(Set(task1, task2, task3, task4)) shouldBe Set(
        task3)
    }

    "detect tasks that connect when connection is not desired" in {
      val constraint =
        ConnectionConstraint(connectionDesired = false, hard = false)
      constraint.violations(Set(task1, task2, task3, task4)) shouldBe Set(
        task1,
        task2)
    }
  }
}
