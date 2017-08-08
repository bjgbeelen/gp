package shifts
package chance

import resource._
import task._
import schedule._
import counter._
import calendar._

case class ChanceCalculator(chances: Map[Resource, Float],
                            influencers: Map[String, Map[Resource, Float]]) {
  override def toString(): String =
    chances
      .map {
        case (resource, chance) =>
          val influences = influencers
            .map {
              case (name, influence) => s"\t$name -> ${influence(resource)}"
            }
            .mkString("\n")
          s"${resource.id} has a chance of $chance, based on:\n${influences}"
      }
      .mkString("\n\n")
}

object ChanceCalculator {
  def apply(influencers: (String, ChanceInfluencer)*)(
      task: Task): ChanceCalculator = {
    val influencersMap = influencers.toList.map {
      case (name, infl) => (name -> infl(task))
    }.toMap
    val total = influencersMap.values.foldLeft(Map[Resource, Float]()) {
      case (result, influence) =>
        influence.map {
          case (resource, chance) =>
            (resource -> result.getOrElse(resource, 1F) * chance)
        }
    }
    ChanceCalculator(total, influencersMap)
  }
}
