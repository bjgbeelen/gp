package shifts
package calendar

case class DayView(day: Int, id: String, label: String, dayOfWeek: Int)
case class PartialWeekView(week: Int, days: Seq[DayView])
case class MonthView(month: Int, name: String, weeks: Seq[PartialWeekView])
case class YearView(year: Int, months: Seq[MonthView])
case class CalendarView(name: String, years: Seq[YearView])

object DayView {
  def from(day: Day) = DayView(
    day = day.number,
    id = day.id,
    label = day.label,
    dayOfWeek = day.dayOfWeek
  )
}

object PartialWeekView {
  def from(partialWeek: PartialWeek) = PartialWeekView(
    week = partialWeek.number,
    days = partialWeek.days.map(DayView.from)
  )
}

object MonthView {
  def from(month: Month) = MonthView(
    month = month.number,
    name = Month.longNames(month.number),
    weeks = month.weeks.map(PartialWeekView.from)
  )
}

object YearView {
  def from(year: Year) = YearView(
    year = year.number,
    months = year.months.map(MonthView.from)
  )
}

object CalendarView {
  def from(calendar: Calendar) = CalendarView(
    name = calendar.name,
    years = calendar.years.map(YearView.from)
  )
}
