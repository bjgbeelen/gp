package shifts
package repository

import doobie._
import doobie.implicits._
import doobie.postgres._, doobie.postgres.implicits._
import cats._, cats.implicits._, cats.effect.IO
import io.circe.generic.auto._

import task.TaskId
import resource.ResourceId
import schedule._
import constraint._

case class ScheduleIdentifier(name: String, calendarName: CalendarName)

object Schedules extends Repository[ScheduleView, ScheduleIdentifier] {
  implicit val ConstraintJson = codecMeta[Map[ResourceId, Seq[ConstraintView]]]

  def insert(o: ScheduleView) = batchInsert(List(o))

  def batchInsert(o: List[ScheduleView]) = {
    val sql = "INSERT INTO Schedules (name, calendarName, constraints) VALUES (?, ?, ?)"
    Update[(String, String, Map[ResourceId, Seq[ConstraintView]])](sql).updateMany(o.map { view =>
      (view.name, view.calendarName, view.resourceConstraints)
    }).flatMap{ _ => o.map{ case view =>
        val assignments = view.assignments.map{ case (taskId, resourceId) =>
          Assignment(taskId, resourceId, view.name, view.calendarName)
        }
        Assignments.batchInsert(assignments.toList)
      }.head // fixme, workaround
    }
  }

  def update(constraints: Map[ResourceId, Seq[ConstraintView]],
             scheduleName: String,
             calendarName: String): ConnectionIO[Int] =
    sql"UPDATE Schedules SET constraints = $constraints WHERE name = $scheduleName AND calendarName = $calendarName".update.run

  def list(calendarName: CalendarName): ConnectionIO[List[String]] =
    sql"SELECT name FROM Schedules where calendarName = $calendarName".query[String].list

  def find(identifier: ScheduleIdentifier): ConnectionIO[Option[ScheduleView]] =
    sql"select name, calendarName, constraints FROM Schedules where calendarName = ${identifier.calendarName} AND name = ${identifier.name}"
      .query[(String, String, Map[ResourceId, Seq[ConstraintView]])]
      .option
      .flatMap {
        case Some((name, calendarName, constraints)) =>
          Assignments.find(identifier).map { assignments =>
            Some(
              ScheduleView(
                name = name,
                calendarName = calendarName,
                assignments = assignments,
                resourceConstraints = constraints,
                totalScore = 0
              )
            )
          }
        case _ => Option.empty[ScheduleView].pure[ConnectionIO]
      }
}
