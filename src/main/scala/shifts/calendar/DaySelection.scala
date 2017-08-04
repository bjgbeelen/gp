package shifts
package calendar

abstract class DaySelection {
  def exec: Day ⇒ Boolean
}

case class InverseSelection(selection: DaySelection) extends DaySelection {
  def exec = !selection.exec(_)
}
case class WeekDaySelection(dayOfWeeks: Seq[DayOfWeekNumber])
    extends DaySelection {
  def exec = _.dayOfWeek in dayOfWeeks
}
case class PeriodSelection(from: DayId, to: DayId) extends DaySelection {
  def exec = day ⇒ from <= day.id && to >= day.id
}
case class DayIdSelection(ids: DayId*) extends DaySelection {
  def exec = _.id in ids.toSeq
}
