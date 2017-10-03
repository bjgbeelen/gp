package shifts
package repository

import doobie._
import doobie.implicits._
import cats._, cats.implicits._, cats.effect.IO

import resource._

case class ResourceIdentifier(id: String, calendarName: String)

object Resources extends Repository[ResourceView, ResourceIdentifier] {
  def insert(o: ResourceView) = batchInsert(List(o))

  def batchInsert(o: List[ResourceView]) = {
    val sql = "INSERT INTO Resources (id, name, numberOfPatients, calendarName) VALUES (?, ?, ?, ?)"
    Update[ResourceView](sql).updateMany(o)
  }

  def update(o: ResourceView) = ???

  def find(
      identifier: ResourceIdentifier
  )(implicit meta: Meta[ResourceIdentifier]): ConnectionIO[Option[ResourceView]] =
    sql"select id, name, numberOfPatients, calendarName from Resources where id = ${identifier.id} AND calendarName = ${identifier.calendarName}"
      .query[ResourceView]
      .option

  def list(calendarName: CalendarName): ConnectionIO[List[Resource]] =
    sql"select id, name, numberOfPatients from Resources where calendarName = $calendarName"
      .query[Resource]
      .list
}
