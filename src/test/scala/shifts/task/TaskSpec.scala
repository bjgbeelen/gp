package shifts
package task

import org.scalatest._

class TaskSpec extends WordSpec with Matchers {
  "Task" should {
    "detect overlapping tasks" in {
      val task1 = Task(dayId = "20170101",
                       label = "",
                       start = 9 :: 0,
                       end = 12 :: 0,
                       Set.empty)
      val task2 = Task(dayId = "20180101",
                       label = "",
                       start = 9 :: 0,
                       end = 12 :: 0,
                       Set.empty)
      val task3 = Task(dayId = "20170101",
                       label = "",
                       start = 9 :: 0,
                       end = 12 :: 0,
                       Set("visite"))
      val task4 = Task(dayId = "20170101",
                       label = "",
                       start = 12 :: 0,
                       end = 16 :: 0,
                       Set.empty)
      val task5 = Task(dayId = "20170101",
                       label = "",
                       start = 11 :: 59,
                       end = 16 :: 0,
                       Set.empty)

      withClue("task1 overlapsWith task2") {
        task1.overlapsWith(task2) shouldBe false
      } // different year
      withClue("task1 overlapsWith task1") {
        task1.overlapsWith(task1) shouldBe false
      } // does not overlap with itself
      withClue("task1 overlapsWith task3") {
        task1.overlapsWith(task3) shouldBe true
      }
      withClue("task1 overlapsWith task4") {
        task1.overlapsWith(task4) shouldBe false
      }
      withClue("task1 overlapsWith task5") {
        task1.overlapsWith(task5) shouldBe true
      }
    }
  }
}
