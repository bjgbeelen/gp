package shifts
package task

import calendar._

case class Holiday(dayId: DayId, label: String, wholeDay: Boolean)

case class TaskGenerationInstruction(label: String,
                                     start: Minute,
                                     end: Minute,
                                     tags: Set[Tag],
                                     daySelection: Seq[DaySelection])

object TaskGenerator {
  def generate(calendar: Calendar,
               instructions: Seq[TaskGenerationInstruction]): Set[Task] =
    instructions.foldLeft(Set[Task]()) {
      case (acc, instr) =>
        acc ++ calendar
          .filter(instr.daySelection : _*)
          .map(
            day =>
              Task(
                label = instr.label,
                day = day,
                start = instr.start,
                end = instr.end,
                tags = instr.tags
            ))
    }
}
