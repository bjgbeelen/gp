package shifts

import task._

package object calendar {
  //import com.github.nscala_time.time.Imports._

  type DateTime = com.github.nscala_time.time.Imports.DateTime

  type YearNumber = Int
  type MonthNumber = Int
  type WeekNumber = Int
  type DayOfWeekNumber = Int
  type DayNumber = Int
  type DayId = String

  val monday: DayOfWeekNumber = 1
  val tuesday: DayOfWeekNumber = 2
  val wednesday: DayOfWeekNumber = 3
  val thursday: DayOfWeekNumber = 4
  val friday: DayOfWeekNumber = 5
  val saturday: DayOfWeekNumber = 6
  val sunday: DayOfWeekNumber = 7

  implicit class SeqHelper[T](e: T) {
    def in(seq: Seq[T]) = seq.contains(e)
  }

}
