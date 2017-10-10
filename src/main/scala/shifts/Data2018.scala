package shifts

import calendar._
import task._
import resource._
import counter._
import schedule._
import constraint._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import repository._

import doobie.Transactor
import cats.effect.IO

import java.util.Date

object Data2018 {
  val holidays = Seq(
    Holiday(dayId = "20171231", label = "", wholeDay = false),
    Holiday("20171225", "1e Kerstdag", true),
    Holiday("20171226", "2e Kerstdag", true),
    Holiday("20180101", "Nieuwjaarsdag", true),
    Holiday("20180330", "Goede vrijdag", wholeDay = false),
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
    Holiday("20180520", "1e pinkster", true),
    Holiday("20180521", "2e pinkster", true),
    Holiday("20181224", "Kerstavond", false),
    Holiday("20181225", "1e Kerstdag", true),
    Holiday("20181226", "2e Kerstdag", true)
  )

  val holidayLabels = holidays.map { case Holiday(id, label, _) ⇒ (id, label) }.toMap

  val calendarDescription =
    CalendarDescription("2018", DateTime("2017-12-25").toDate, new DateTime("2019-01-07").toDate, holidayLabels)

  val calendar = Calendar(calendarDescription)

  val calendars = List(calendar)

  val weekCounters: Seq[Counter] =
    Counter.withParent(name = "week", include = Set("week"), exclude = Set("ignore"))(
      Seq(
        Counter(name = "consult", include = Set("consult")),
        Counter(name = "visite", include = Set("visite")),
        Counter(name = "nacht", include = Set("nacht"))
      )
    )
  val weekendCounters = Counter.withParent(name = "weekend", include = Set("weekend"), exclude = Set("ignore"))(
    Seq(
      Counter(name = "consult", include = Set("consult")),
      Counter(name = "visite", include = Set("visite")),
      Counter(name = "nacht", include = Set("nacht")),
      Counter(name = "feest", include = Set("feest"))
    )
  )

  // val ignoreTasks = Seq.empty
  val ignoreTasks = Seq(
    ("20171225", Set("nacht")),
    ("20171225", Set("consult")),
    ("20171225", Set("visite")),
    ("20171226", Set("nacht")),
    ("20171226", Set("consult")),
    ("20171226", Set("visite")),
    ("20171227", Set("nacht")),
    ("20171227", Set("consult")),
    ("20171227", Set("visite")),
    ("20171228", Set("nacht")),
    ("20171228", Set("consult")),
    ("20171228", Set("visite")),
    ("20171229", Set("nacht")),
    ("20171229", Set("consult")),
    ("20171229", Set("visite")),
    ("20171230", Set("nacht")),
    ("20171230", Set("consult")),
    ("20171230", Set("visite")),
    ("20171231", Set("nacht")),
    ("20171231", Set("consult")),
    ("20171231", Set("visite")),
    ("20180101", Set[String]()),
    ("20180102", Set[String]()  ),
    ("20180103", Set[String]()  ),
    ("20180104", Set[String]()  ),
    ("20180105", Set[String]()  ),
    ("20180106", Set[String]()  ),
    ("20180107", Set[String]()  ),
    ("20180108", Set("avond", "visite")),
    ("20180110", Set("avond", "consult")),
    ("20180113", Set("weekend", "avond", "visite")),
    ("20180114", Set("weekend", "ochtend", "consult")),
    ("20180115", Set("avond", "visite")),
    ("20180117", Set("avond", "consult")),
    ("20180118", Set("avond", "visite")),
    ("20180120", Set("weekend", "avond", "consult")),
    ("20180121", Set("weekend", "ochtend", "visite")),
    ("20180123", Set("avond", "visite")),
    ("20180125", Set("avond", "consult")),
    ("20180127", Set("weekend", "ochtend", "visite")),
    ("20180127", Set("weekend", "avond", "consult")),
    ("20180131", Set("avond", "consult")),
    ("20180201", Set("avond", "visite")),
    ("20180202", Set("avond", "visite")),
    ("20180203", Set("weekend", "visite", "ochtend")),
    ("20180204", Set("weekend", "consult", "avond")),
    ("20180204", Set("weekend", "visite", "avond")),
    ("20180205", Set("avond", "visite")),
    ("20180206", Set("avond", "consult")),
    ("20180210", Set("weekend", "consult", "ochtend")),
    ("20180210", Set("weekend", "visite", "avond")),
    ("20180211", Set("weekend", "visite", "ochtend")),
    ("20180213", Set("avond", "visite")),
    ("20180215", Set("avond", "consult")),
    ("20180217", Set("weekend", "consult", "avond")),
    ("20180218", Set("weekend", "consult", "ochtend")),
    ("20180220", Set("avond", "visite")),
    ("20180221", Set("avond", "consult")),
    ("20180222", Set("avond", "visite")),
    ("20180224", Set("weekend", "consult", "ochtend")),
    ("20180224", Set("weekend", "consult", "avond")),
    ("20180225", Set("weekend", "visite", "avond")),
    ("20180228", Set("avond", "consult")),
    ("20180301", Set("avond", "visite")),
    ("20180303", Set("weekend", "consult", "ochtend")),
    ("20180303", Set("weekend", "visite", "avond")),
    ("20180304", Set("weekend", "visite", "ochtend")),
    ("20180306", Set("avond", "consult")),
    ("20180310", Set("weekend", "visite", "avond")),
    ("20180311", Set("weekend", "visite", "ochtend")),
    ("20180314", Set("avond", "visite")),
    ("20180315", Set("avond", "consult")),
    ("20180316", Set("avond", "visite")),
    ("20180318", Set("weekend", "consult", "ochtend")),
    ("20180318", Set("weekend", "consult", "avond")),
    ("20180319", Set("avond", "consult")),
    ("20180320", Set("avond", "visite")),
    ("20180323", Set("avond", "visite")),
    ("20180324", Set("weekend", "consult", "ochtend")),
    ("20180325", Set("weekend", "visite", "avond")),
    ("20180328", Set("avond", "consult")),
    ("20180329", Set("avond", "visite")),
    ("20180331", Set("weekend", "consult", "ochtend")),
    ("20180401", Set("weekend", "consult", "ochtend")),
    ("20180401", Set("weekend", "visite", "avond")),
    ("20180402", Set("weekend", "visite", "ochtend")),
    ("20180403", Set("avond", "visite")),
    ("20180404", Set("avond", "consult")),
    ("20180407", Set("weekend", "consult", "ochtend")),
    ("20180407", Set("weekend", "visite", "avond")),
    ("20180408", Set("weekend", "consult", "avond")),
    ("20180409", Set("avond", "consult")),
    ("20180412", Set("avond", "consult")),
    ("20180414", Set("weekend", "visite", "avond")),
    ("20180415", Set("weekend", "visite", "ochtend")),
    ("20180416", Set("avond", "visite")),
    ("20180419", Set("avond", "visite")),
    ("20180421", Set("weekend", "visite", "ochtend")),
    ("20180424", Set("avond", "consult")),
    ("20180425", Set("avond", "visite")),
    ("20180427", Set("weekend", "visite", "avond")),
    ("20180428", Set("weekend", "consult", "ochtend")),
    ("20180429", Set("weekend", "visite", "ochtend")),
    ("20180501", Set("avond", "consult")),
    ("20180502", Set("avond", "visite")),
    ("20180504", Set("avond", "visite")),
    ("20180505", Set("weekend", "visite", "avond")),
    ("20180506", Set("weekend", "consult", "avond")),
    ("20180507", Set("avond", "consult")),
    ("20180510", Set("weekend", "consult", "ochtend")),
    ("20180511", Set("avond", "visite")),
    ("20180512", Set("weekend", "visite", "ochtend")),
    ("20180513", Set("weekend", "visite", "avond")),
    ("20180514", Set("avond", "visite")),
    ("20180514", Set("weekend", "consult", "avond")),
    ("20180516", Set("avond", "visite")),
    ("20180517", Set("avond", "consult")),
    ("20180519", Set("weekend", "consult", "avond")),
    ("20180520", Set("weekend", "consult", "avond")),
    ("20180521", Set("weekend", "visite", "avond")),
    ("20180522", Set("avond", "consult")),
    ("20180526", Set("weekend", "consult", "ochtend")),
    ("20180527", Set("weekend", "visite", "ochtend")),
    ("20180527", Set("weekend", "consult", "avond")),
    ("20180529", Set("avond", "consult")),
    ("20180531", Set("avond", "consult")),
    ("20180602", Set("weekend", "consult", "ochtend")),
    ("20180603", Set("weekend", "visite", "ochtend")),
    ("20180603", Set("weekend", "visite", "avond")),
    ("20180604", Set("avond", "consult")),
    ("20180605", Set("avond", "visite")),
    ("20180609", Set("weekend", "visite", "avond")),
    ("20180610", Set("weekend", "consult", "avond")),
    ("20180611", Set("avond", "visite")),
    ("20180613", Set("avond", "visite")),
    ("20180614", Set("avond", "consult")),
    ("20180616", Set("weekend", "consult", "avond")),
    ("20180617", Set("weekend", "consult", "ochtend")),
    ("20180619", Set("avond", "consult")),
    ("20180622", Set("avond", "visite")),
    ("20180623", Set("weekend", "visite", "ochtend")),
    ("20180624", Set("weekend", "consult", "ochtend")),
    ("20180624", Set("weekend", "consult", "avond")),
    ("20180627", Set("avond", "consult")),
    ("20180628", Set("avond", "visite")),
    ("20180629", Set("avond", "visite")),
    ("20180630", Set("weekend", "consult", "avond")),
    ("20180701", Set("weekend", "visite", "ochtend")),
    ("20180703", Set("avond", "consult")),
    ("20180704", Set("avond", "visite")),
    ("20180707", Set("weekend", "visite", "ochtend")),
    ("20180707", Set("weekend", "visite", "avond")),
    ("20180710", Set("avond", "consult")),
    ("20180711", Set("avond", "visite")),
    ("20180712", Set("avond", "consult")),
    ("20180714", Set("weekend", "consult", "ochtend")),
    ("20180715", Set("weekend", "consult", "avond")),
    ("20180717", Set("avond", "visite")),
    ("20180718", Set("avond", "visite")),
    ("20180721", Set("weekend", "consult", "avond")),
    ("20180722", Set("weekend", "consult", "ochtend")),
    ("20180723", Set("avond", "visite")),
    ("20180725", Set("avond", "consult")),
    ("20180726", Set("avond", "visite")),
    ("20180728", Set("weekend", "visite", "avond")),
    ("20180729", Set("weekend", "consult", "ochtend")),
    ("20180729", Set("weekend", "consult", "avond")),
    ("20180731", Set("avond", "consult")),
    ("20180803", Set("avond", "visite")),
    ("20180804", Set("weekend", "visite", "ochtend")),
    ("20180805", Set("weekend", "visite", "avond")),
    ("20180806", Set("avond", "consult")),
    ("20180808", Set("avond", "visite")),
    ("20180809", Set("avond", "consult")),
    ("20180811", Set("weekend", "consult", "ochtend")),
    ("20180812", Set("weekend", "visite", "avond")),
    ("20180813", Set("avond", "consult")),
    ("20180815", Set("avond", "visite")),
    ("20180817", Set("avond", "visite")),
    ("20180818", Set("weekend", "visite", "ochtend")),
    ("20180819", Set("weekend", "consult", "avond")),
    ("20180822", Set("avond", "consult")),
    ("20180823", Set("avond", "visite")),
    ("20180825", Set("weekend", "visite", "ochtend")),
    ("20180826", Set("weekend", "consult", "avond")),
    ("20180828", Set("avond", "visite")),
    ("20180829", Set("avond", "consult")),
    ("20180830", Set("avond", "visite")),
    ("20180901", Set("weekend", "visite", "avond")),
    ("20180902", Set("weekend", "consult", "ochtend")),
    ("20180903", Set("avond", "visite")),
    ("20180905", Set("avond", "consult")),
    ("20180906", Set("avond", "visite")),
    ("20180908", Set("weekend", "consult", "ochtend")),
    ("20180909", Set("weekend", "consult", "avond")),
    ("20180911", Set("avond", "consult")),
    ("20180914", Set("avond", "visite")),
    ("20180915", Set("weekend", "consult", "avond")),
    ("20180916", Set("weekend", "consult", "ochtend")),
    ("20180918", Set("avond", "visite")),
    ("20180920", Set("avond", "consult")),
    ("20180922", Set("weekend", "visite", "ochtend")),
    ("20180923", Set("weekend", "visite", "avond")),
    ("20180926", Set("avond", "visite")),
    ("20180927", Set("avond", "consult")),
    ("20180929", Set("weekend", "visite", "avond")),
    ("20180930", Set("weekend", "consult", "ochtend")),
    ("20181003", Set("avond", "consult")),
    ("20181004", Set("avond", "visite")),
    ("20181005", Set("avond", "visite")),
    ("20181007", Set("weekend", "visite", "ochtend")),
    ("20181007", Set("weekend", "visite", "avond")),
    ("20181009", Set("avond", "visite")),
    ("20181010", Set("avond", "consult")),
    ("20181012", Set("avond", "visite")),
    ("20181013", Set("weekend", "consult", "avond")),
    ("20181014", Set("weekend", "visite", "avond")),
    ("20181017", Set("avond", "visite")),
    ("20181018", Set("avond", "consult")),
    ("20181020", Set("weekend", "visite", "ochtend")),
    ("20181020", Set("weekend", "consult", "avond")),
    ("20181021", Set("weekend", "visite", "ochtend")),
    ("20181023", Set("avond", "consult")),
    ("20181024", Set("avond", "visite")),
    ("20181025", Set("avond", "consult")),
    ("20181027", Set("weekend", "visite", "avond")),
    ("20181028", Set("weekend", "consult", "ochtend")),
    ("20181031", Set("avond", "consult")),
    ("20181101", Set("avond", "visite")),
    ("20181104", Set("weekend", "visite", "ochtend")),
    ("20181104", Set("weekend", "visite", "avond")),
    ("20181106", Set("avond", "consult")),
    ("20181108", Set("avond", "visite")),
    ("20181110", Set("weekend", "consult", "ochtend")),
    ("20181111", Set("weekend", "consult", "avond")),
    ("20181114", Set("avond", "visite")),
    ("20181115", Set("avond", "consult")),
    ("20181116", Set("avond", "visite")),
    ("20181117", Set("weekend", "consult", "avond")),
    ("20181118", Set("weekend", "visite", "ochtend")),
    ("20181119", Set("avond", "consult")),
    ("20181120", Set("avond", "visite")),
    ("20181122", Set("avond", "visite")),
    ("20181124", Set("weekend", "consult", "avond")),
    ("20181125", Set("weekend", "consult", "ochtend")),
    ("20181126", Set("avond", "visite")),
    ("20181128", Set("avond", "consult")),
    ("20181129", Set("avond", "visite")),
    ("20181201", Set("weekend", "visite", "ochtend")),
    ("20181201", Set("weekend", "visite", "avond")),
    ("20181204", Set("avond", "consult")),
    ("20181205", Set("avond", "visite")),
    ("20181208", Set("weekend", "consult", "ochtend")),
    ("20181209", Set("weekend", "consult", "avond")),
    ("20181212", Set("avond", "visite")),
    ("20181213", Set("avond", "consult")),
    ("20181214", Set("avond", "visite")),
    ("20181215", Set("weekend", "visite", "ochtend")),
    ("20181215", Set("weekend", "visite", "avond")),
    ("20181216", Set("weekend", "consult", "avond")),
    ("20181217", Set("avond", "consult")),
    ("20181218", Set("avond", "visite")),
    ("20181220", Set("avond", "consult")),
    ("20181222", Set("weekend", "visite", "ochtend")),
    ("20181223", Set("weekend", "visite", "avond")),
    ("20181225", Set("weekend", "visite", "avond")),
    ("20181226", Set("weekend", "visite", "ochtend")),
    ("20181226", Set("weekend", "consult", "avond")),
    ("20181227", Set("avond", "visite")),
    ("20181229", Set("weekend", "consult", "ochtend")),
    ("20181230", Set("weekend", "visite", "ochtend")),
    ("20190101", Set("weekend", "consult", "ochtend")),
    ("20190103", Set("avond", "consult")),
    ("20190105", Set("weekend", "visite", "ochtend")),
    ("20190105", Set("weekend", "consult", "avond")),
    ("20190106", Set("weekend", "consult", "ochtend")),
    ("20180101", Set("nacht")),
    ("20180105", Set("nacht")),
    ("20180106", Set("nacht")),
    ("20180107", Set("nacht")),
    ("20180108", Set("nacht")),
    ("20180110", Set("nacht")),
    ("20180111", Set("nacht")),
    ("20180112", Set("nacht")),
    ("20180113", Set("nacht")),
    ("20180114", Set("nacht")),
    ("20180115", Set("nacht")),
    ("20180116", Set("nacht")),
    ("20180117", Set("nacht")),
    ("20180118", Set("nacht")),
    ("20180119", Set("nacht")),
    ("20180120", Set("nacht")),
    ("20180121", Set("nacht")),
    ("20180122", Set("nacht")),
    ("20180123", Set("nacht")),
    ("20180124", Set("nacht")),
    ("20180125", Set("nacht")),
    ("20180126", Set("nacht")),
    ("20180127", Set("nacht")),
    ("20180128", Set("nacht")),
    ("20180129", Set("nacht")),
    ("20180204", Set("nacht")),
    ("20180205", Set("nacht")),
    ("20180206", Set("nacht")),
    ("20180207", Set("nacht")),
    ("20180210", Set("nacht")),
    ("20180211", Set("nacht")),
    ("20180212", Set("nacht")),
    ("20180213", Set("nacht")),
    ("20180214", Set("nacht")),
    ("20180215", Set("nacht")),
    ("20180218", Set("nacht")),
    ("20180219", Set("nacht")),
    ("20180220", Set("nacht")),
    ("20180221", Set("nacht")),
    ("20180222", Set("nacht")),
    ("20180225", Set("nacht")),
    ("20180226", Set("nacht")),
    ("20180227", Set("nacht")),
    ("20180228", Set("nacht")),
    ("20180301", Set("nacht")),
    ("20180303", Set("nacht")),
    ("20180304", Set("nacht")),
    ("20180305", Set("nacht")),
    ("20180306", Set("nacht")),
    ("20180307", Set("nacht")),
    ("20180308", Set("nacht")),
    ("20180309", Set("nacht")),
    ("20180312", Set("nacht")),
    ("20180313", Set("nacht")),
    ("20180314", Set("nacht")),
    ("20180315", Set("nacht")),
    ("20180316", Set("nacht")),
    ("20180317", Set("nacht")),
    ("20180318", Set("nacht")),
    ("20180319", Set("nacht")),
    ("20180325", Set("nacht")),
    ("20180326", Set("nacht")),
    ("20180327", Set("nacht")),
    ("20180328", Set("nacht")),
    ("20180329", Set("nacht")),
    ("20180330", Set("nacht")),
    ("20180331", Set("nacht")),
    ("20180401", Set("nacht")),
    ("20180402", Set("nacht")),
    ("20180407", Set("nacht")),
    ("20180408", Set("nacht")),
    ("20180409", Set("nacht")),
    ("20180410", Set("nacht")),
    ("20180411", Set("nacht")),
    ("20180412", Set("nacht")),
    ("20180413", Set("nacht")),
    ("20180414", Set("nacht")),
    ("20180415", Set("nacht")),
    ("20180416", Set("nacht")),
    ("20180417", Set("nacht")),
    ("20180418", Set("nacht")),
    ("20180419", Set("nacht")),
    ("20180420", Set("nacht")),
    ("20180421", Set("nacht")),
    ("20180422", Set("nacht")),
    ("20180423", Set("nacht")),
    ("20180429", Set("nacht")),
    ("20180430", Set("nacht")),
    ("20180501", Set("nacht")),
    ("20180502", Set("nacht")),
    ("20180503", Set("nacht")),
    ("20180504", Set("nacht")),
    ("20180505", Set("nacht")),
    ("20180506", Set("nacht")),
    ("20180507", Set("nacht")),
    ("20180508", Set("nacht")),
    ("20180509", Set("nacht")),
    ("20180510", Set("nacht")),
    ("20180516", Set("nacht")),
    ("20180517", Set("nacht")),
    ("20180520", Set("nacht")),
    ("20180521", Set("nacht")),
    ("20180522", Set("nacht")),
    ("20180524", Set("nacht")),
    ("20180525", Set("nacht")),
    ("20180526", Set("nacht")),
    ("20180527", Set("nacht")),
    ("20180528", Set("nacht")),
    ("20180529", Set("nacht")),
    ("20180530", Set("nacht")),
    ("20180603", Set("nacht")),
    ("20180604", Set("nacht")),
    ("20180605", Set("nacht")),
    ("20180606", Set("nacht")),
    ("20180607", Set("nacht")),
    ("20180608", Set("nacht")),
    ("20180609", Set("nacht")),
    ("20180610", Set("nacht")),
    ("20180611", Set("nacht")),
    ("20180612", Set("nacht")),
    ("20180613", Set("nacht")),
    ("20180614", Set("nacht")),
    ("20180615", Set("nacht")),
    ("20180616", Set("nacht")),
    ("20180617", Set("nacht")),
    ("20180619", Set("nacht")),
    ("20180620", Set("nacht")),
    ("20180621", Set("nacht")),
    ("20180623", Set("nacht")),
    ("20180624", Set("nacht")),
    ("20180625", Set("nacht")),
    ("20180626", Set("nacht")),
    ("20180627", Set("nacht")),
    ("20180701", Set("nacht")),
    ("20180702", Set("nacht")),
    ("20180703", Set("nacht")),
    ("20180704", Set("nacht")),
    ("20180705", Set("nacht")),
    ("20180706", Set("nacht")),
    ("20180707", Set("nacht")),
    ("20180708", Set("nacht")),
    ("20180709", Set("nacht")),
    ("20180710", Set("nacht")),
    ("20180711", Set("nacht")),
    ("20180712", Set("nacht")),
    ("20180717", Set("nacht")),
    ("20180718", Set("nacht")),
    ("20180719", Set("nacht")),
    ("20180720", Set("nacht")),
    ("20180721", Set("nacht")),
    ("20180722", Set("nacht")),
    ("20180723", Set("nacht")),
    ("20180729", Set("nacht")),
    ("20180730", Set("nacht")),
    ("20180731", Set("nacht")),
    ("20180801", Set("nacht")),
    ("20180802", Set("nacht")),
    ("20180803", Set("nacht")),
    ("20180804", Set("nacht")),
    ("20180808", Set("nacht")),
    ("20180809", Set("nacht")),
    ("20180810", Set("nacht")),
    ("20180811", Set("nacht")),
    ("20180812", Set("nacht")),
    ("20180813", Set("nacht")),
    ("20180814", Set("nacht")),
    ("20180815", Set("nacht")),
    ("20180816", Set("nacht")),
    ("20180817", Set("nacht")),
    ("20180818", Set("nacht")),
    ("20180819", Set("nacht")),
    ("20180825", Set("nacht")),
    ("20180826", Set("nacht")),
    ("20180827", Set("nacht")),
    ("20180828", Set("nacht")),
    ("20180829", Set("nacht")),
    ("20180901", Set("nacht")),
    ("20180902", Set("nacht")),
    ("20180903", Set("nacht")),
    ("20180904", Set("nacht")),
    ("20180905", Set("nacht")),
    ("20180906", Set("nacht")),
    ("20180907", Set("nacht")),
    ("20180908", Set("nacht")),
    ("20180909", Set("nacht")),
    ("20180910", Set("nacht")),
    ("20180916", Set("nacht")),
    ("20180917", Set("nacht")),
    ("20180918", Set("nacht")),
    ("20180919", Set("nacht")),
    ("20180920", Set("nacht")),
    ("20180921", Set("nacht")),
    ("20180925", Set("nacht")),
    ("20180926", Set("nacht")),
    ("20180927", Set("nacht")),
    ("20180928", Set("nacht")),
    ("20180929", Set("nacht")),
    ("20180930", Set("nacht")),
    ("20181001", Set("nacht")),
    ("20181002", Set("nacht")),
    ("20181003", Set("nacht")),
    ("20181004", Set("nacht")),
    ("20181005", Set("nacht")),
    ("20181012", Set("nacht")),
    ("20181013", Set("nacht")),
    ("20181014", Set("nacht")),
    ("20181015", Set("nacht")),
    ("20181016", Set("nacht")),
    ("20181017", Set("nacht")),
    ("20181018", Set("nacht")),
    ("20181019", Set("nacht")),
    ("20181020", Set("nacht")),
    ("20181021", Set("nacht")),
    ("20181022", Set("nacht")),
    ("20181023", Set("nacht")),
    ("20181024", Set("nacht")),
    ("20181025", Set("nacht")),
    ("20181026", Set("nacht")),
    ("20181027", Set("nacht")),
    ("20181028", Set("nacht")),
    ("20181029", Set("nacht")),
    ("20181030", Set("nacht")),
    ("20181031", Set("nacht")),
    ("20181101", Set("nacht")),
    ("20181102", Set("nacht")),
    ("20181104", Set("nacht")),
    ("20181105", Set("nacht")),
    ("20181106", Set("nacht")),
    ("20181107", Set("nacht")),
    ("20181112", Set("nacht")),
    ("20181113", Set("nacht")),
    ("20181114", Set("nacht")),
    ("20181115", Set("nacht")),
    ("20181116", Set("nacht")),
    ("20181117", Set("nacht")),
    ("20181118", Set("nacht")),
    ("20181119", Set("nacht")),
    ("20181125", Set("nacht")),
    ("20181126", Set("nacht")),
    ("20181127", Set("nacht")),
    ("20181128", Set("nacht")),
    ("20181129", Set("nacht")),
    ("20181130", Set("nacht")),
    ("20181201", Set("nacht")),
    ("20181202", Set("nacht")),
    ("20181203", Set("nacht")),
    ("20181204", Set("nacht")),
    ("20181205", Set("nacht")),
    ("20181206", Set("nacht")),
    ("20181207", Set("nacht")),
    ("20181208", Set("nacht")),
    ("20181209", Set("nacht")),
    ("20181212", Set("nacht")),
    ("20181213", Set("nacht")),
    ("20181214", Set("nacht")),
    ("20181215", Set("nacht")),
    ("20181216", Set("nacht")),
    ("20181217", Set("nacht")),
    ("20181218", Set("nacht")),
    ("20181219", Set("nacht")),
    ("20181220", Set("nacht")),
    ("20181221", Set("nacht")),
    ("20181225", Set("nacht")),
    ("20181226", Set("nacht")),
    ("20181227", Set("nacht")),
    ("20181228", Set("nacht")),
    ("20181229", Set("nacht")),
    ("20181230", Set("nacht")),
    ("20190101", Set("nacht")),
    ("20190102", Set("nacht")),
    ("20190103", Set("nacht")),
    ("20190104", Set("nacht")),
    ("20190105", Set("nacht")),
    ("20190106", Set("nacht")),
    ("20190107", Set("nacht")),
    ("20190107", Set("visite")),
    ("20190107", Set("consult"))
  )

  val counters    = (weekCounters ++ weekendCounters).toList
  val countersMap = Map("week" -> weekCounters, "weekend" -> weekendCounters)

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

  val weekDaysWithoutFriday      = WeekDaySelection(monday to thursday)
  val weekDays                   = WeekDaySelection(monday to friday)
  val weekendDaysIncludingFriday = WeekDaySelection(friday to sunday)
  val weekendDays                = WeekDaySelection(saturday to sunday)
  val holidaySelection = DayIdSelection(holidays.collect {
    case Holiday(id, label, _) ⇒ id
  }: _*)
  val holidayWholeDaySelection = DayIdSelection(holidays.collect {
    case Holiday(id, label, true) ⇒ id
  }: _*)
  val holidayPartDaySelection = DayIdSelection(holidays.collect {
    case Holiday(id, label, false) ⇒ id
  }: _*)
  val noHolidaySelection      = InverseSelection(holidaySelection)
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
    TaskGenerationInstruction("consult",
                              start = 17 :: 0,
                              end = 23 :: 0,
                              Set("weekend", "consult", "avond"),
                              Seq(WeekDaySelection(Seq(friday)), noHolidaySelection)),
    TaskGenerationInstruction("visite",
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
    TaskGenerationInstruction("visite",
                              start = 9 :: 0,
                              end = 16 :: 0,
                              Set("weekend", "visite", "ochtend"),
                              Seq(WeekDaySelection(Seq(saturday)), noWholeHolidaySelection)),
    TaskGenerationInstruction("visite",
                              start = 9 :: 0,
                              end = 16 :: 0,
                              Set("weekend", "visite", "ochtend"),
                              Seq(WeekDaySelection(Seq(sunday)), noWholeHolidaySelection)),
    TaskGenerationInstruction("visite",
                              start = 16 :: 0,
                              end = 23 :: 0,
                              Set("weekend", "visite", "avond"),
                              Seq(WeekDaySelection(Seq(saturday)), noHolidaySelection)),
    TaskGenerationInstruction("visite",
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

  val tasks = TaskGenerator.generate(calendar, instructions, ignoreTasks)
  val applicableTasks = tasks.filter(!_.tags.contains("ignore"))

  def calculateDesiredNumberOfTasks(
      tasks: Set[Task],
      counters: Seq[Counter],
      ratio: Float
  ): Map[Counter, Int] = counters.count(tasks).map {
    case (counter, size) => (counter, Math.round(size * ratio).toInt)
  }

  val totalPatients = resources.map(_.numberOfPatients).sum

  val resourceConstraints: Map[Resource, Seq[Constraint]] = {
    val first :: others            = resources
    val overlappingTasksConstraint = OverlappingTasksConstraint()
    val weekendGapConstraint =
      WeekendDistanceConstraint(desiredDistance = 2, calendar = calendar, hard = false)
    def absenceConstraint(resource: Resource) = {
      val absence: Set[DayId] = resource.id match {
        case "beelen" => Set.empty
        case _        => Set.empty
      }
      AbsenceConstraint(absence = absence)
    }
    def weekendTasksConstraint(resource: Resource) = {
      val desired = resource.id match {
        case "houppermans" => 3
        case "heho"        => 4
        case _             => 2
      }
      val excludeNight = resource.id match {
        case "heho"  => false
        case "baars" => false
        case _       => true
      }
      WeekendTasksConstraint(desiredTasksPerWeekend = desired, excludeNight)
    }
    def connectingConstraint(resource: Resource) = {
      val desired = (resource.id in Seq("baars", "houppermans", "heho", "dooren_van"))
      ConnectionConstraint(connectionDesired = desired, hard = desired == false)
    }

    val otherResourceConstraints = others.map {
      case resource @ Resource(_, _, nrOfPatients) =>
        val counterConstraints: List[CounterConstraint] =
          calculateDesiredNumberOfTasks(applicableTasks, counters, nrOfPatients * 1F / totalPatients).map {
            case (counter, desiredNumber) =>
              CounterConstraint(counter, desiredNumber)
          }.toList
        val constraints: List[Constraint] = counterConstraints ++ List(
          overlappingTasksConstraint,
          absenceConstraint(resource),
          weekendTasksConstraint(resource),
          connectingConstraint(resource),
          weekendGapConstraint
        )
        (resource, constraints)
    }.toMap

    val firstResourceConstraints = {
      val counterConstraints = calculateDesiredNumberOfTasks(
        applicableTasks,
        counters,
        1
      ).map {
        case (counter, totalDesiredNumber) =>
          val desiredNumber = totalDesiredNumber - otherResourceConstraints.values.flatten.collect {
            case CounterConstraint(c, number, _) if c == counter => number
          }.sum
          CounterConstraint(counter, desiredNumber)
      }.toList
      val constraints: List[Constraint] = List(overlappingTasksConstraint,
                                               connectingConstraint(first),
                                               absenceConstraint(first),
                                               weekendTasksConstraint(first),
                                               weekendGapConstraint
                                           ) ++ counterConstraints
      (first -> constraints)
    }

    otherResourceConstraints + firstResourceConstraints
  }

  // import scala.annotation.tailrec
  // def foo(): Future[Seq[Schedule]] = {
  //   import Task._
  //   val testTasks =
  //     tasks.toList.sortBy(!_.tags.contains("feest")).filter(_.is(Weekend))
  //   implicit val context = TaskContext(testTasks)
  //   Schedule.run(testTasks, calendar, counters, resourceConstraints, runs = 100, parallel = 4).flatMap {
  //     case ScheduleRunResult(_, Nil) =>
  //       println("No complete schedules this run, doing another round")
  //       foo()
  //     case ScheduleRunResult(incomplete, completes) =>
  //       Future.successful(completes.sortBy(_.totalScore).zipWithIndex.map {
  //         case (item, index) => item.copy(name = index.toString)
  //       })
  //   }
  // }
  // val schedules: Future[Seq[Schedule]] = foo()

  val testSchedule = Schedule(
    name = "Basis",
    calendar = calendar,
    assignments = Map.empty,
    resourceConstraints = resourceConstraints
  )(TaskContext(applicableTasks.toList))

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:gp-shifts",
    "app",
    ""
  )
  // Calendars.find("2018")

  // Calendars.find("2018").flatMap {
  //   case None =>
  //     for {
  //       calendarId <- Calendars.insert(calendarDescription)
  //     } yield calendarId
  //   case _ => ???
  // }
}
