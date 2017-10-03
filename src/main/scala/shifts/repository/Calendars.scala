package shifts
package repository

import doobie._
import doobie.implicits._
import cats._, cats.implicits._, cats.effect.IO

import calendar._

object Calendars extends Repository[CalendarDescription, String] {
  implicit val StringMap = codecMeta[Map[String, String]]

  def insert(o: CalendarDescription) = batchInsert(List(o))

  def batchInsert(o: List[CalendarDescription]) = {
    val sql = "INSERT INTO CalendarDescription (name, _from, _to, labels) VALUES (?, ?, ?, ?)"
    Update[CalendarDescription](sql).updateMany(o)
  }

  def update(o: CalendarDescription) = ???

  def find(name: String)(implicit meta: Meta[String]): ConnectionIO[Option[CalendarDescription]] =
    sql"select name, _from, _to, labels from CalendarDescription where name = $name"
      .query[CalendarDescription]
      .option

  def list(): ConnectionIO[List[CalendarDescription]] =
    sql"select name, _from, _to, labels from CalendarDescription"
      .query[CalendarDescription]
      .list
}
