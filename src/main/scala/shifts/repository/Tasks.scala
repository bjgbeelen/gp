package shifts
package repository

import doobie._
import doobie.implicits._
import doobie.postgres._, doobie.postgres.implicits._
import cats._, cats.implicits._, cats.effect.IO

import task._

case class TaskIdentifier(id: String, calendarName: String)

object Tasks extends Repository[TaskView, TaskIdentifier] {
  // implicit val StringArray = codecMeta[Set[String]]

  def insert(o: TaskView) = batchInsert(List(o))

  def batchInsert(o: List[TaskView]) = {
    val sql = "INSERT INTO Tasks (id, day, calendarName, label, start, finish, tags) VALUES (?, ?, ?, ?, ?, ?, ?)"
    Update[TaskView](sql).updateMany(o)
  }

  def update(o: TaskView) = ???

  def find(identifier: TaskIdentifier)(implicit meta: Meta[TaskIdentifier]): ConnectionIO[Option[TaskView]] =
    sql"select id, day, calendarName, label, start, finish, tags FROM Tasks where id = ${identifier.id} AND calendarName = ${identifier.calendarName}"
      .query[TaskView]
      .option

  def find(calendarName: String)(implicit meta: Meta[String]): ConnectionIO[List[TaskView]] =
    sql"select id, day, calendarName, label, start, finish, tags FROM Tasks where calendarName = $calendarName"
      .query[TaskView]
      .list
}
