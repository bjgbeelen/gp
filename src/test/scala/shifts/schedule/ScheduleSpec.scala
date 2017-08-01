package shifts
package schedule

import org.scalatest._

import Data2018._

class ScheduleSpec extends WordSpec with Matchers {
  "Schedule.plan()" should {
    "automatically assign resources to tasks" in {
      val testTasks = tasks.toList.sortBy(!_.tags.contains("feest"))
      Schedule.plan(testTasks, calendar, counters, resourceConstraints) match {
        case Right((schedule, incompleteSchedules)) =>
          println(s"Found a schedule after ${incompleteSchedules.size} attempts")
          resourceConstraints.foreach {
            case (resource, constraints) =>
              val resourceTasks = schedule.tasks(resource)
              // Test everyone has got their expected nr of assignments
              constraints.desiredNumberOfTasks.foreach {
                case (counter, desiredNumber) =>
                  counter.count(resourceTasks) shouldBe desiredNumber
              }

              // Test noone is assigned a task during their absence
              resourceTasks.map(_.dayId) intersect constraints.absence shouldBe Set.empty

              // Test noone is assigned overlapping tasks
              resourceTasks.foreach { task =>
                resourceTasks.filter(_.overlapsWith(task)) shouldBe Set.empty
              }
          }
        case Left(incompletes) =>
          println(incompletes.head)
          assert(false)
      }

    }
  }
}
