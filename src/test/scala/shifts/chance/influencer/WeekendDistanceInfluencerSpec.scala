package shifts
package constraint

import calendar._
import chance.influencer._
import task._
import Task._
import org.scalatest._

class WeekendDistanceInfluencerSpec extends WordSpec with Matchers {
  import Data2018._

  val context = TaskContext(tasks.toSeq)
  val resource = resources.head

  "WeekendDistanceInfluencer" should {
    "say yes (return 1) to a task if the distances in weeks between tasks is equal or larger than the desired distance" in {
      val influence = WeekendDistanceInfluencer(
        desiredDistance = 2,
        hard = true,
        calendar = calendar,
        taskContext = context,
        assignments = Set[Task]()
      ).chance(context.tasks.head) shouldBe 1F
    }
    "say yes (return 1) to a task if there is already a task in the same week" in {
      val assigned = context.tasks.find(_.day.id == "20180101").get
      val sameWeekTask = context.nextTasks(assigned)
      val assignedWeek = context.taskWeek(assigned)
      val sameWeek = context.taskWeek(sameWeekTask)
      assignedWeek distance sameWeek shouldBe 0

      val influence = WeekendDistanceInfluencer(
        desiredDistance = 2,
        hard = true,
        calendar = calendar,
        taskContext = context,
        assignments = Set(assigned)
      ).chance(sameWeekTask) shouldBe 1F
    }
    "say no (return 0) to a task if there is already a task in the previous week" in {
      val assigned = context.tasks.find(_.day.id == "20180101").get
      val nextWeekTask = context.tasks.find(_.day.id == "20180107").get
      val assignedWeek = context.taskWeek(assigned)
      val nextWeek = context.taskWeek(nextWeekTask)
      assignedWeek distance nextWeek shouldBe 1

      val influence = WeekendDistanceInfluencer(
        desiredDistance = 2,
        hard = true,
        calendar = calendar,
        taskContext = context,
        assignments = Set(assigned)
      ).chance(nextWeekTask) shouldBe 0F
    }
    "say yes (return 1) to a task if the distance is large enough" in {
      val assigned = context.tasks.find(_.day.id == "20180101").get
      val nextWeekTask = context.tasks.find(_.day.id == "20180114").get
      val assignedWeek = context.taskWeek(assigned)
      val nextWeek = context.taskWeek(nextWeekTask)
      context.taskWeek(assigned) distance context.taskWeek(nextWeekTask) shouldBe 2
      val influence = WeekendDistanceInfluencer(
        desiredDistance = 2,
        hard = true,
        calendar = calendar,
        taskContext = context,
        assignments = Set(assigned)
      ).chance(nextWeekTask) shouldBe 1F
    }
  }
}
