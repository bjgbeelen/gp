package shifts
package chance

import resource._
import task._
import schedule._
import counter._

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
  def apply(influencers: ChanceInfluencer*)(
      task: Task,
      counters: Seq[Counter],
      constraints: Map[Resource, ResourceConstraints],
      assignments: Map[Resource, Set[Task]]): ChanceCalculator = {
    val influencersMap = influencers.toList.map {
      case infl =>
        (infl.getClass.getName -> infl(task,
                                       counters,
                                       constraints,
                                       assignments))
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
