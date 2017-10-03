package shifts
package calendar

import doobie._
import doobie.implicits._
import cats._, cats.implicits._, cats.effect.IO
import io.circe._
import io.circe.generic.auto._
import io.circe.jawn._
import io.circe.syntax._
import scala.reflect.runtime.universe._
import org.postgresql.util.PGobject

class CalendarRepository {
  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:gp-shifts",
    "app",
    ""
  )
  // val y = xa.yolo; import y._

  implicit val JsonMeta: Meta[Json] =
    Meta
      .other[PGobject]("json")
      .xmap[Json](
        a => parse(a.getValue).leftMap[Json](e => throw e).merge, // failure raises an exception
        a => {
          val o = new PGobject
          o.setType("json")
          o.setValue(a.noSpaces)
          o
        }
      )

  def codecMeta[A: Encoder: Decoder: TypeTag]: Meta[A] =
    Meta[Json].xmap[A](
      _.as[A].fold[A](throw _, identity),
      _.asJson
    )

  implicit val StringMap = codecMeta[Map[String, String]]

  def find(name: String): Calendar =
    sql"select name, _from, _to, labels from CalendarDescription where name = $name"
      .query[CalendarDescription]
      .unique
      .transact(xa)
      .map(Calendar.apply)
      .unsafeRunSync

  def insert(description: CalendarDescription) =
    sql"""INSERT INTO CalendarDescription (name, _from, _to, labels) VALUES (
      ${description.name},
      ${description.from},
      ${description.to},
      ${description.labels})""".update.run.transact(xa).unsafeRunSync

}
