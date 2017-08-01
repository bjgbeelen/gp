package shifts

import calendar._
import task._
import resource._
import counter._
import schedule._

object Data2018 {
  val holidays = Seq(
    Holiday(dayId = "20171231", label = "Oudjaarsavond", wholeDay = false),
    Holiday("20180101", "Nieuwjaarsdag", true),
    Holiday("20180330", "Goede vrijdag", true),
    Holiday("20180331", "", true),
    Holiday("20180401", "1e paasdag", true),
    Holiday("20180402", "2e paasdag", true),
    Holiday("20180426", "Koningsnacht", false),
    Holiday("20180427", "Koningsdag", true),
    Holiday("20180509", "", wholeDay = false),
    Holiday("20180510", "Hemelvaart", true),
    Holiday("20180511", "", true),
    Holiday("20180512", "", true),
    Holiday("20180518", "", false),
    Holiday("20180519", "", true),
    Holiday("20180520", "1e pinksterdag", true),
    Holiday("20180521", "2e pinksterdag", true),
    Holiday("20181224", "Kerstavond", false),
    Holiday("20181225", "1e Kerstdag", true),
    Holiday("20181226", "2e Kerstdag", true)
  )

  val holidayLabels = holidays.map { case Holiday(id, label, _) ⇒ (id, label) }.toMap

  val calendar = Calendar("2018", "2017-12-27", "2019-01-04", holidayLabels)

  val counters =
    Counter.withParent(name = "week",
                       include = Set("week"),
                       exclude = Set("ignore"))(
      Seq(
        Counter(name = "consult", include = Set("consult")),
        Counter(name = "visite", include = Set("visite")),
        Counter(name = "nacht", include = Set("nacht"))
      )) ++
      Counter.withParent(name = "weekend",
                         include = Set("weekend"),
                         exclude = Set("ignore"))(
        Seq(
          Counter(name = "consult", include = Set("consult")),
          Counter(name = "visite", include = Set("visite")),
          Counter(name = "nacht", include = Set("nacht")),
          Counter(name = "feest", include = Set("feest"))
        ))

  val resources: List[Resource] = List(
    Resource(name = "Acker, vd", numberOfPatients = 1518),
    Resource(name = "Ambachtsheer", numberOfPatients = 2297),
    Resource(name = "Baars", numberOfPatients = 3411),
    Resource(name = "Beelen", numberOfPatients = 2553),
    Resource(name = "Daamen", numberOfPatients = 2276),
    Resource(name = "Dooren, van", numberOfPatients = 2720),
    Resource(name = "Gielen", numberOfPatients = 2862),
    Resource(name = "Heeden, vd", numberOfPatients = 2276),
    Resource(name = "HeHo", numberOfPatients = 2450),
    Resource(name = "Hoeks", numberOfPatients = 2686),
    Resource(name = "Homa", numberOfPatients = 2601),
    Resource(name = "Houppermans", numberOfPatients = 2041),
    Resource(name = "Marcelis", numberOfPatients = 1700),
    Resource(name = "Nierop, van", numberOfPatients = 1701),
    Resource(name = "Onderwater", numberOfPatients = 2024),
    Resource(name = "Pruijssen", numberOfPatients = 1700),
    Resource(name = "Rekkers", numberOfPatients = 2648),
    Resource(name = "Rens, van", numberOfPatients = 2276),
    Resource(name = "Sluijs, vd", numberOfPatients = 2774)
  )

  val weekDaysWithoutFriday = WeekDaySelection(monday to thursday)
  val weekDays = WeekDaySelection(monday to friday)
  val weekendDaysIncludingFriday = WeekDaySelection(friday to sunday)
  val weekendDays = WeekDaySelection(saturday to sunday)
  val holidaySelection = DayIdSelection(holidays.collect {
    case Holiday(id, label, _) ⇒ id
  }.toSeq)
  val holidayWholeDaySelection = DayIdSelection(holidays.collect {
    case Holiday(id, label, true) ⇒ id
  }.toSeq)
  val holidayPartDaySelection = DayIdSelection(holidays.collect {
    case Holiday(id, label, false) ⇒ id
  }.toSeq)
  val noHolidaySelection = InverseSelection(holidaySelection)
  val noWholeHolidaySelection = InverseSelection(holidayWholeDaySelection)

  val instructions = Seq(
    // week tasks
    TaskGenerationInstruction("nacht",
                              start = 0 :: 0,
                              end = 8 :: 0,
                              Set("week", "nacht"),
                              Seq(weekDays, noWholeHolidaySelection)),
    TaskGenerationInstruction("visite",
                              start = 17 :: 0,
                              end = 23 :: 0,
                              Set("week", "visite", "avond"),
                              Seq(weekDaysWithoutFriday, noHolidaySelection)),
    TaskGenerationInstruction("consult",
                              start = 17 :: 0,
                              end = 23 :: 0,
                              Set("week", "consult", "avond"),
                              Seq(weekDaysWithoutFriday, noHolidaySelection)),
    // weekend tasks
    TaskGenerationInstruction(
      "consult",
      start = 17 :: 0,
      end = 23 :: 0,
      Set("weekend", "consult", "avond"),
      Seq(WeekDaySelection(Seq(friday)), noHolidaySelection)),
    TaskGenerationInstruction(
      "visite",
      start = 17 :: 0,
      end = 23 :: 0,
      Set("weekend", "visite", "avond"),
      Seq(WeekDaySelection(Seq(friday)), noHolidaySelection)),
    TaskGenerationInstruction("nacht",
                              start = 0 :: 0,
                              end = 8 :: 0,
                              Set("weekend", "nacht"),
                              Seq(weekendDays, noWholeHolidaySelection)),
    TaskGenerationInstruction("consult",
                              start = 8 :: 0,
                              end = 16 :: 0,
                              Set("weekend", "consult", "ochtend"),
                              Seq(weekendDays, noWholeHolidaySelection)),
    TaskGenerationInstruction("consult",
                              start = 16 :: 0,
                              end = 23 :: 0,
                              Set("weekend", "consult", "avond"),
                              Seq(weekendDays, noHolidaySelection)),
    TaskGenerationInstruction(
      "visite",
      start = 9 :: 0,
      end = 16 :: 0,
      Set("weekend", "visite", "ochtend"),
      Seq(WeekDaySelection(Seq(saturday)), noWholeHolidaySelection)),
    TaskGenerationInstruction(
      "visite",
      start = 9 :: 0,
      end = 16 :: 0,
      Set("weekend", "visite", "ochtend"),
      Seq(WeekDaySelection(Seq(sunday)), noWholeHolidaySelection)),
    TaskGenerationInstruction(
      "visite",
      start = 16 :: 0,
      end = 23 :: 0,
      Set("weekend", "visite", "avond"),
      Seq(WeekDaySelection(Seq(saturday)), noHolidaySelection)),
    TaskGenerationInstruction(
      "visite",
      start = 16 :: 0,
      end = 23 :: 0,
      Set("weekend", "visite", "avond"),
      Seq(WeekDaySelection(Seq(sunday)), noHolidaySelection)),
    // holiday tasks
    TaskGenerationInstruction("nacht",
                              start = 0 :: 0,
                              end = 8 :: 0,
                              Set("weekend", "nacht", "feest"),
                              Seq(holidayWholeDaySelection)),
    TaskGenerationInstruction("visite",
                              start = 9 :: 0,
                              end = 16 :: 0,
                              Set("weekend", "visite", "ochtend", "feest"),
                              Seq(holidayWholeDaySelection)),
    TaskGenerationInstruction("consult",
                              start = 8 :: 0,
                              end = 16 :: 0,
                              Set("weekend", "consult", "ochtend", "feest"),
                              Seq(holidayWholeDaySelection)),
    TaskGenerationInstruction("visite",
                              start = 16 :: 0,
                              end = 23 :: 0,
                              Set("weekend", "visite", "avond", "feest"),
                              Seq(holidaySelection)),
    TaskGenerationInstruction("consult",
                              start = 16 :: 0,
                              end = 23 :: 0,
                              Set("weekend", "consult", "avond", "feest"),
                              Seq(holidaySelection))
  )

  val tasks = TaskGenerator.generate(calendar, instructions)

  def calculateDesiredNumberOfTasks(
      tasks: Set[Task],
      counters: Seq[Counter],
      ratio: Float
  ): Map[Counter, Int] = counters.count(tasks).map {
    case (counter, size) => (counter, Math.floor(size * ratio).toInt)
  }

  val totalPatients = resources.map(_.numberOfPatients).sum
  val resourceConstraints = {
    val first :: others = resources
    val otherResourceConstraints = others.map {
      case Resource(id, _, nrOfPatients) =>
        ResourceConstraints(
          resourceId = id,
          desiredNumberOfTasks =
            calculateDesiredNumberOfTasks(tasks,
                                          counters,
                                          nrOfPatients * 1F / totalPatients),
          desiredNumberOfTasksInOneWeekend = 2,
          wantsEveningNightCombination = false,
          wantsCoupledTasks = false,
          absence = Set.empty
        )
    }
    val othersDesiredNumberOfTaskSum =
      otherResourceConstraints.map(_.desiredNumberOfTasks).sumUp
    val firstResourceConstraints = ResourceConstraints(
      resourceId = first.id,
      desiredNumberOfTasks = calculateDesiredNumberOfTasks(tasks, counters, 1) - othersDesiredNumberOfTaskSum,
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false,
      absence = Set.empty
    )
    (firstResourceConstraints :: otherResourceConstraints).map { rc =>
      (resources.find(_.id == rc.resourceId).get -> rc)
    }.toMap
  }

  val actualFirst = calculateDesiredNumberOfTasks(
    tasks,
    counters,
    resources.head.numberOfPatients * 1F / totalPatients)

  // val schedule =
  //   Schedule.plan(tasks.toList, calendar, counters, resourceConstraints)
}