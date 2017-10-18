package shifts
package schedule

import scala.language.postfixOps

import monix.eval.{ Task => MonixTask }
import monix.execution._
import scala.concurrent.duration._

import java.util.concurrent.Executors
import scala.concurrent._

import calendar._
import counter._
import task._
import constraint._
import resource._

object SolutionSearchManager {
  var calculatedSolutions: Map[String, ScheduleView] = Map.empty
  var completeSolutions: Map[String, Schedule]       = Map.empty
  var cancelable: Option[Cancelable]                 = None

  val ec = new ExecutionContext {
    val threadPool = Executors.newFixedThreadPool(1000);
    def execute(runnable: Runnable) {
      threadPool.submit(runnable)
    }
    def reportFailure(t: Throwable) {}
  }
  implicit val taskScheduler = Scheduler(ec)

  def start(
      scheduleName: String,
      tasks: Seq[Task],
      calendar: Calendar,
      counters: Seq[Counter],
      resourceConstraints: Map[Resource, Seq[Constraint]],
      assignments: Map[Task, Resource] = Map.empty
  ): Unit = {
    implicit val taskContext = TaskContext(tasks.toSeq)
    cancelable = Some(taskScheduler.scheduleWithFixedDelay(1 seconds, 20 minutes) {
      MonixTask
        .fromFuture {
          println(s"[${taskScheduler.currentTimeMillis}] Searching for solutions...")
          Schedule.run(
            tasks = tasks.toList
              .sortBy(!_.tags.contains("feest"))
              .filter(_.is(Task.Weekend)),
            calendar = calendar,
            counters = counters,
            resourceConstraints = resourceConstraints,
            assignments = Map.empty,
            runs = 3000,
            parallel = 6
          )
        }
        .foreach {
          case ScheduleRunResult(incomplete, Nil) =>
            println("no complete schedules: " + incomplete.head.toString)
          case ScheduleRunResult(_, completes) =>
            println(s"found ${completes.size} solutions")
            val newCompletes: Map[String, Schedule] = completes.foldLeft(Map[String, Schedule]()){
              case (map, schedule) =>
                val newName = findName(s"${schedule.totalScore}-auto-$scheduleName", map)
                map + (newName -> schedule.copy(name = newName))
            }
            val newViews: Map[String, ScheduleView] = newCompletes.map {
              case (name, schedule) => (name, ScheduleView.from(schedule))
            }
            calculatedSolutions ++= newViews
            completeSolutions ++= newCompletes
            println(s"Added ${newViews.size} new solutions")
          case other => println(other)
        }
    })
  }

  private def findName(proposal: String, map: Map[String, Schedule], iteration: Int = 0): String = {
    val newProposal = if (iteration == 0) proposal else proposal + "-" + iteration.toString
    if (map.get(proposal).isEmpty) proposal
    else findName(proposal, map, iteration + 1)
  }

  def stop = cancelable.foreach(_.cancel)
}
