package shifts
package repository

import doobie._
import doobie.implicits._
import doobie.postgres._, doobie.postgres.implicits._
import cats._, cats.implicits._, cats.effect.IO

import task.TaskId
import resource.ResourceId

case class Assignment(taskId: TaskId, resourceId: ResourceId, scheduleName: String, calendarName: CalendarName)
case class ScheduleTask(taskId: TaskId, scheduleName: String, calendarName: CalendarName)

object Assignments extends Repository[Assignment, Assignment] {
  def insert(o: Assignment) = batchInsert(List(o))

  def batchInsert(o: List[Assignment]) = {
    val sql = "INSERT INTO Assignments (taskId, resourceId, scheduleName, calendarName) VALUES (?, ?, ?, ?)"
    Update[Assignment](sql).updateMany(o)
  }

  def update(o: Assignment) = ???

  def find(identifier: Assignment)(implicit meta: Meta[Assignment]): ConnectionIO[Option[Assignment]] = ???

  def delete(identifier: ScheduleTask): ConnectionIO[Int] =
    sql"DELETE FROM Assignments WHERE taskId = ${identifier.taskId} AND scheduleName = ${identifier.scheduleName} AND calendarName = ${identifier.calendarName}".update.run

  def find(identifier: ScheduleIdentifier): ConnectionIO[Map[TaskId, ResourceId]] =
    sql"select taskId, resourceId, scheduleName, calendarName FROM Assignments where calendarName = ${identifier.calendarName} AND scheduleName = ${identifier.name}"
      .query[Assignment]
      .map(a => (a.taskId -> a.resourceId))
      .list
      .map(_.toMap)

}
