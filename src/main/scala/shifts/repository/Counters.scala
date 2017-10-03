package shifts
package repository

import doobie._
import doobie.implicits._
import doobie.postgres._, doobie.postgres.implicits._
import cats._, cats.implicits._, cats.effect.IO

import counter._

case class CounterIdentifier(id: String, calendarName: String)

object Counters extends Repository[CounterView, CounterIdentifier] {
  def insert(o: CounterView) = batchInsert(List(o))

  def batchInsert(o: List[CounterView]) = {
    val sql = "INSERT INTO Counters (id, name, groupName, calendarName, include, exclude) VALUES (?, ?, ?, ?, ?, ?)"
    Update[CounterView](sql).updateMany(o)
  }

  def update(o: CounterView) = ???

  def find(identifier: CounterIdentifier)(implicit meta: Meta[CounterIdentifier]): ConnectionIO[Option[CounterView]] =
    sql"select id, name, groupName, calendarName, include, exclude from Counters where id = ${identifier.id} AND calendarName = ${identifier.calendarName}"
      .query[CounterView]
      .option

  def list(calendarName: CalendarName)(implicit meta: Meta[CalendarName]): ConnectionIO[List[Counter]] =
    sql"select id, name, groupName, calendarName, include, exclude from Counters where calendarName = $calendarName"
      .query[CounterView]
      .map(Counter.from)
      .list
}
