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
      assignments: Map[Task, Resource]
  ): Unit = {
    implicit val taskContext = TaskContext(tasks.toSeq)
    cancelable = Some(taskScheduler.scheduleWithFixedDelay(1 seconds, 5 minute) {
      MonixTask
        .fromFuture {
          println(s"[${taskScheduler.currentTimeMillis}] Searching for solutions,,,")
          Schedule.run(
            tasks = tasks.toList
              .sortBy(!_.tags.contains("feest"))
              .filter(_.is(Task.Weekend)),
            calendar = calendar,
            counters = counters,
            resourceConstraints = resourceConstraints,
            assignments = Map.empty,
            runs = 1000,
            parallel = 4
          )
        }
        .foreach {
          case ScheduleRunResult(_, completes) =>
            val newSolutions: Map[String, ScheduleView] = completes
              .map(schedule => s"${schedule.totalScore}-auto-$scheduleName" -> ScheduleView.from(schedule))
              .toMap
            calculatedSolutions ++= newSolutions
            println(s"Added ${newSolutions.size} new solutions")
          case other => println(other)
        }
    })
  }

  def stop = cancelable.foreach(_.cancel)
}
