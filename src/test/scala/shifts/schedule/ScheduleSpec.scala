package shifts
package schedule

import org.scalatest._
import org.scalatest.concurrent._
import org.scalatest.time._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import Data2018._
import task.Task._
import constraint._

class ScheduleSpec extends WordSpec with Matchers with ScalaFutures {
  "Schedule.plan()" should {
    "automatically assign resources to tasks" in {
      val testTasks = applicableTasks.toList.sortBy(!_.tags.contains("feest")).filter(_.is(Weekend))
      implicit val context = task.TaskContext(testTasks)

      whenReady(Schedule.run(testTasks,
                             calendar,
                             counters,
                             resourceConstraints,
                             runs = 300,
                             parallel = 4),
                timeout(Span(1, Hours))) {
        case ScheduleRunResult(incompletes, completes) =>
          println(s"Complete schedules: ${completes.size}")

          if (completes.isEmpty) {
            println("\n*******************************\n")
            println(incompletes.head.toString)
          }

          completes.foreach { schedule =>
            resourceConstraints.foreach {
              case (resource, constraints) =>
                val resourceTasks = schedule.tasks(resource)
                constraints.filter(_.hard).foreach { constraint =>
                  constraint
                    .violations(resourceTasks) shouldBe constraint.obeyed
                }
            }
          }
      }
    }
  }
}
