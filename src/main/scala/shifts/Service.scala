//   package shifts

// import io.circe._
// import io.circe.generic.auto._
// import io.circe.syntax._

// import fs2.{ Stream, Task }
// import org.http4s._
// import org.http4s.dsl._
// import org.http4s.circe._
// import org.http4s.server.blaze._
// import org.http4s.util.StreamApp

// import cats.implicits._

// import Data2018._
// import calendar.CalendarCodec._

// object Service extends StreamApp {
//   override def stream(args: List[String]): Stream[Task, Nothing] = {
//     val service = HttpService {
//       // case GET -> path =>
//       //   Ok(path.toString)
//       case GET -> Root / "calendars" / path =>
//         Ok(path.toString)
//         // Ok(calendars.map(_.name).asJson)
//       case GET -> Root / "calendars" / name =>
//         Ok(calendars.find(_.name == name).get.asJson.spaces2)
//       case GET -> Root / "tasks" =>
//         Ok(tasks.asJson.spaces2)
//     }

//     BlazeBuilder
//       .bindHttp(5000)
//       .mountService(service, "/api")
//       .serve
//   }
// }
