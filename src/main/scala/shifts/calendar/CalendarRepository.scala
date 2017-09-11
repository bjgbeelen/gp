// package shifts
// package calendar

// import doobie._
// import doobie.implicits._
// import cats.effect.IO

// class CalendarRepository {
//     val xa = Transactor.fromDriverManager[IO](
//     "org.postgresql.Driver", "jdbc:postgresql:gp-shifts", "app", ""
//   )

//   def find(name: String): ConnectionIO[Option[Calendar]] =
//     sql"select name, from, to, labels from calendar left join day_labels where name = $name".query[Calendar].option
//   }
