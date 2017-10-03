package shifts
package repository

import doobie._
import doobie.implicits._
import doobie.postgres._, doobie.postgres.implicits._
import cats._, cats.implicits._, cats.effect.IO
import io.circe._
import io.circe.generic.auto._
import io.circe.jawn._
import io.circe.syntax._
import scala.reflect.runtime.universe._
import org.postgresql.util.PGobject
import shapeless._

trait Repository[O, A] {
  // implicit val SetMeta: Meta[Set[_]] =
  //   Meta[List[_]].xmap[Set[_]](_.toSet)(_.toList)

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

  def insert(o: O): ConnectionIO[Int]

  def batchInsert(l: List[O]): ConnectionIO[Int]

  // def update(o: O): ConnectionIO[Int]

  // def find(id: A)(implicit meta: Meta[A]): ConnectionIO[Option[O]]
}
