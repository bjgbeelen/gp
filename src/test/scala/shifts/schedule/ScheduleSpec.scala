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
      val testTasks = tasks.toList.sortBy(!_.tags.contains("feest")).filter(_.is(Weekend))
      implicit val context = task.TaskContext(testTasks)
      // implicit val defaultPatience = PatienceConfig(timeout = Span(20, Seconds))

      whenReady(Schedule.run(testTasks,
                             calendar,
                             counters,
                             resourceConstraints,
                             runs = 300,
                             parallel = 4),
                timeout(Span(1, Hours))) {
        case ScheduleRunResult(incomplete, completes) =>
          println(s"Complete schedules: ${completes.size}")

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
      // Schedule.run(testTasks, calendar, counters, resourceConstraints) match {
      //   case Right((schedule, incompleteSchedules)) =>
      //     println(
      //       s"Found a schedule after ${incompleteSchedules.size + 1} attempts")
      //     resourceConstraints.foreach {
      //       case (resource, constraints) =>
      //         val resourceTasks = schedule.tasks(resource)
      //         constraints.filter(_.hard).foreach { constraint =>
      //           constraint.violations(resourceTasks) shouldBe constraint.obeyed
      //         }

      //         val weekendTasksConstraint = constraints.collect {
      //           case x: WeekendTasksConstraint => x
      //         }.head
      //         val foo = weekendTasksConstraint.violations(resourceTasks).map{
      //                   case (week, task) => (week.id -> task.map(_.id))
      //                   }.toMap
      //         println(s"$resource: $foo")
      //         println("")
      //       // Test everyone has got their expected nr of assignments
      //       // constraints.collect{case a: CounterConstraint => a}.foreach {
      //       //   case CounterConstraint(counter, desiredNumber, _) =>
      //       //     counter.count(resourceTasks) shouldBe desiredNumber
      //       // }

      //       // // Test noone is assigned a task during their absence
      //       // resourceTasks.map(_.day.id) intersect constraints.absence shouldBe Set.empty

      //       // // Test noone is assigned overlapping tasks
      //       // resourceTasks.foreach { task =>
      //       //   resourceTasks.count(_.overlapsWith(task)) shouldBe 0
      //       // }

      //       // val map = calendar.weeks.map{ week =>
      //       //   val count = resourceTasks.filter(task => task.is(Weekend) && !task.is(Night)).count(_.week == week)
      //       //   (week -> count)
      //       // }.toMap

      //       // if (resource.id == "beelen")
      //       // println(map.filter{
      //       //   case (week, size) => size > 0
      //       // })
      //     }
      //   case Left(incompletes) =>
      //     println(incompletes.head)
      //     assert(false)
      // }

    }
  }
}
