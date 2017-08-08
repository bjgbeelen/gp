package shifts
package task

import calendar.Day
import org.scalatest._

class TaskSpec extends WordSpec with Matchers {
  "Task" should {
    "detect overlapping tasks" in new Fixture {
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

    "find the previous task" in new Fixture {
      implicit val context =
        TaskContext(Seq(task1, task2, task3, task4, task5))

      task1.previous shouldBe None
      task2.previous shouldBe Some(task4)
      task3.previous shouldBe Some(task1)
      task4.previous shouldBe Some(task5)
      task5.previous shouldBe Some(task3)
    }

    "find the next task" in new Fixture {
      implicit val context =
        TaskContext(Seq(task1, task2, task3, task4, task5))

      task1.next shouldBe Some(task3)
      task2.next shouldBe None
      task3.next shouldBe Some(task5)
      task4.next shouldBe Some(task2)
      task5.next shouldBe Some(task4)
    }

    "determine the correct week that the task belongs to" in {
      import Data2018._
      implicit val context = TaskContext(tasks.toList)
      val easter1 = tasks.find(_.day.label == "1e paasdag").get
      val easter2 = tasks.find(_.day.label == "2e paasdag").get

      easter1.week shouldBe easter2.week
    }

    "calculate related week tasks for a week" in {
      import Data2018._
      implicit val context = TaskContext(tasks.toList)
    }
  }
}

class Fixture {
  val day1 =
    new Day(label = "", number = 1, dayOfWeek = 4, parent = () => ???) {
      override lazy val id = "20170101"
    }
  val day2 =
    new Day(label = "", number = 1, dayOfWeek = 4, parent = () => ???) {
      override lazy val id = "20180101"
    }
  val task1 =
    Task(day = day1, label = "consult", start = 9 :: 0, end = 12 :: 0, Set.empty)
  val task2 =
    Task(day = day2, label = "consult", start = 9 :: 0, end = 12 :: 0, Set.empty)
  val task3 =
    Task(day = day1, label = "visite", start = 9 :: 0, end = 12 :: 0, Set("visite"))
  val task4 =
    Task(day = day1, label = "consult", start = 12 :: 0, end = 16 :: 0, Set.empty)
  val task5 =
    Task(day = day1, label = "visite", start = 11 :: 59, end = 16 :: 0, Set.empty)
}
