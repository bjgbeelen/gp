package shifts
package calendar

import scala.annotation.tailrec

case class Calendar(name: String, from: DateTime, to: DateTime, children: Seq[Year]) extends CalendarNode[Year] {
  def years = children
  def months = years.foldLeft(Seq[Month]()) {
    case (months, year) ⇒ months ++ year.months
  }
  def partialWeeks: Seq[PartialWeek] = months.foldLeft(Seq[PartialWeek]()) {
    case (weeks, month) ⇒ weeks ++ month.weeks
  }
  def weeks: Seq[Week] = {
    val result: List[Seq[(String, PartialWeek)]] =
      partialWeeks.map(pw ⇒ (pw.id, pw)).groupBy(_._1).toList.map(_._2)
    result.map(pwSeq ⇒ Week(pwSeq.map(_._2)))
  }

  def filter(filters: DaySelection*) = filters.toSeq.foldLeft(days) {
    case (days, daySelection) ⇒ days.filter(daySelection.exec)
  }
}

object Calendar {
  def apply(name: String, from: String, to: String, dayLabels: Map[DayId, String]): Calendar =
    apply(name, DateTime(from), DateTime(to), dayLabels)

  def apply(name: String, from: DateTime, to: DateTime, dayLabels: Map[DayId, String]): Calendar = {
    @tailrec
    def daysMapping(
        date: DateTime,
        mapping: Map[YearNumber, Map[MonthNumber, Map[WeekNumber, Seq[(DayNumber, DayOfWeekNumber)]]]] = Map.empty
    ): Map[YearNumber, Map[MonthNumber, Map[WeekNumber, Seq[(DayNumber, DayOfWeekNumber)]]]] = {
      val year: YearNumber           = date.getYear
      val month: MonthNumber         = date.getMonthOfYear
      val week: WeekNumber           = date.getWeekOfWeekyear
      val day: DayNumber             = date.getDayOfMonth
      val dayOfWeek: DayOfWeekNumber = date.getDayOfWeek
      if (date.isBefore(to) || date == to) {
        val updatedDays = mapping
          .getOrElse(year, Map.empty)
          .getOrElse(month, Map.empty)
          .getOrElse(week, Seq.empty) :+ (day, dayOfWeek)
        val updatedWeeks = mapping
          .getOrElse(year, Map.empty)
          .getOrElse(month, Map.empty) ++ Map(week -> updatedDays)
        val updatedMonths = mapping.getOrElse(year, Map.empty) ++ Map(month -> updatedWeeks)
        daysMapping(date.plusDays(1), mapping ++ Map(year -> updatedMonths))
      } else mapping
    }

    def sortWeeks(weeks: Iterable[WeekNumber], month: MonthNumber): List[WeekNumber] = {
      val sorted = weeks.toList.sorted

      val moveToEnd: Option[List[WeekNumber]] = sorted match {
        case w :: tail if month == 12 && w == 1 =>
          Some(tail :+ w)
        case _ => None
      }
      val moveToFront = sorted.reverse match {
        case w :: _ if month == 1 && (w >= 52) ⇒
          Some(w :: sorted.take(sorted.length - 1))
        case _ => None
      }
      moveToEnd.orElse(moveToFront).getOrElse(sorted)

      // sorted.reverse match {
      //   case w :: _ if month == 1 && (w >= 52) ⇒
      //     w :: sorted.take(sorted.length - 1)
      //   case _ ⇒ sorted
      // }
    }

    val calendar = daysMapping(from)

    lazy val Calendar = new Calendar(name, from, to, years)
    lazy val years: Seq[Year] = calendar.keys.toList.sorted.map { yearNumber ⇒
      lazy val year = Year(yearNumber, () ⇒ Calendar, months)
      lazy val months: Seq[Month] =
        calendar(yearNumber).keys.toList.sorted.map { monthNumber ⇒
          lazy val month = Month(monthNumber, () ⇒ year, weeks)
          lazy val weeks: Seq[PartialWeek] =
            sortWeeks(calendar(yearNumber)(monthNumber).keys, monthNumber)
              .map { weekNumber ⇒
                lazy val week = PartialWeek(weekNumber, () ⇒ month, days)
                lazy val days: Seq[Day] =
                  calendar(yearNumber)(monthNumber)(weekNumber).map {
                    case (day, dayOfWeek) ⇒
                      val label = dayLabels.getOrElse(Day.id(yearNumber, monthNumber, day), "")
                      Day(label, day, dayOfWeek, () ⇒ week)
                  }
                week
              }
          month
        }
      year
    }

    Calendar
  }
}

case class Year(number: YearNumber, parent: () ⇒ Calendar, children: Seq[Month])
    extends CalendarNode[Month]
    with NeighbourSupport[Year, Calendar] {
  def months                 = children
  override lazy val toString = s"${number}"
}

case class Month(number: MonthNumber, parent: () ⇒ Year, children: Seq[PartialWeek])
    extends CalendarNode[PartialWeek]
    with NeighbourSupport[Month, Year] {
  def weeks                  = children
  def year                   = parent()
  def name                   = Month.longNames(number)
  override lazy val toString = Month.shortNames(number) + s", ${year.number}"
}

case object Month {
  val longNames = Seq("_",
                      "Januari",
                      "Februari",
                      "Maart",
                      "April",
                      "Mei",
                      "Juni",
                      "Juli",
                      "Augustus",
                      "September",
                      "Oktober",
                      "November",
                      "December")
  val shortNames =
    Seq("_", "Jan", "Feb", "Mrt", "Apr", "Mei", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec")
}

case class PartialWeek(number: WeekNumber, parent: () ⇒ Month, children: Seq[Day])
    extends CalendarNode[Day]
    with NeighbourSupport[PartialWeek, Month] {
  def month = parent()
  lazy val year: Year = {
    val optionNext = next.flatMap {
      case week if week.number == number && month.number == 12 && children.size < 4 =>
        Some(month.year.next.get)
      case _ => None
    }
    val optionPrevious = previous.flatMap {
      case week if week.number == number && month.number == 1 && children.size < 4 =>
        Some(month.year.previous.get)
      case _ => None
    }
    optionNext.orElse(optionPrevious).getOrElse(month.year)
  }

  def id = s"${year.number}-$number"

  override lazy val toString = s"Week ${number}, ${year.number}"
}

case class Week(number: WeekNumber, year: YearNumber, days: Seq[Day]) {
  import Math.abs
  def id = s"$year-$number"
  def distance(other: Week): Int =
    abs(abs(year - other.year) * 52 - abs(number - other.number))
}

case object Week {
  def apply(partialWeeks: Seq[PartialWeek]): Week = {
    require(partialWeeks.size > 0 && partialWeeks.map(_.id).toSet.size == 1)
    val days = partialWeeks.foldLeft(Seq[Day]()) {
      case (days, partialWeek) ⇒ days ++ partialWeek.days
    }
    Week(partialWeeks.head.number, partialWeeks.head.year.number, days)
  }
}

case object Day {
  val longDayOfWeekNames =
    Seq("_", "Maandag", "Dinsdag", "Woensdag", "Donderdag", "Vrijdag", "Zaterdag", "Zondag")
  val shortDayOfWeekNames = Seq("_", "Ma", "Di", "Wo", "Do", "Vr", "Za", "Zo")
  def id(year: YearNumber, month: MonthNumber, day: DayNumber) =
    f"${year}${month}%02d${day}%02d"

  import io.circe._
  import io.circe.syntax._
  implicit val dayEncoder: Encoder[Day] = new Encoder[Day] {
    final def apply(day: Day): Json = Json.obj(
      ("id", Json.fromString(day.id)),
      ("label", Json.fromString(day.label)),
      ("number", Json.fromInt(day.number)),
      ("dayOfWeek", Json.fromInt(day.dayOfWeek))
    )
  }
}

case object DateTime {
  def apply(year: Int, month: Int, day: Int = 0) =
    new DateTime(year, month, day, 0, 0, 0, 0)
  def apply(date: String) = new DateTime(date)
}

case class Day(label: String, number: DayNumber, dayOfWeek: DayOfWeekNumber, parent: () ⇒ PartialWeek)
    extends NeighbourSupport[Day, PartialWeek] {
  def partialWeek = parent()
  def week: Week = {
    val partialWeeks: Seq[PartialWeek] =
      Seq(partialWeek.previous, Some(partialWeek), partialWeek.next).collect {
        case Some(week) if week.number == partialWeek.number => week
      }
    Week(partialWeeks)
  }
  def month = partialWeek.month
  def year  = month.year

  lazy val id: DayId = Day.id(year.number, month.number, number)

  def toDateTime = DateTime(year.number, month.number, number)

  override lazy val toString = id
}
