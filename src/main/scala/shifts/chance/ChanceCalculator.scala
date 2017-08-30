package shifts
package chance

import resource._
import task._
import schedule._
import counter._
import calendar._

case class ChanceCalculator(chances: Map[Resource, Float],
                            influencers: Map[Resource, Seq[(ChanceInfluencer, Float)]]) {
  override def toString(): String =
    chances
      .map {
        case (resource, chance) =>
          val influences = influencers(resource)
            .map {
              case (influencer, chance) => s"\t$influencer -> $chance"
            }
            .mkString("\n")
          s"${resource.id} has a chance of $chance, based on:\n${influences}"
      }
      .mkString("\n\n")
}

object ChanceCalculator {
  def apply(influencers: Map[Resource, Seq[ChanceInfluencer]])(
      task: Task): ChanceCalculator = {
    val individualChances = influencers.map{ case (resource, chanceInfluencers) =>
      val result = chanceInfluencers.map{ case influencer =>
        (influencer, influencer.chance(task))
      }
      (resource -> result)
    }
    val total = individualChances.map{ case (resource, chances) =>
      val product = chances.foldLeft(1F) {
        case (acc, (_, chance)) => acc * chance
      }
      (resource -> product)
    }
    // val influencersMap = influencers.toList.map {
    //   case (name, infl) => (name -> infl(task))
    // }.toMap
    // val total = influencersMap.values.foldLeft(Map[Resource, Float]()) {
    //   case (result, influence) =>
    //     influence.map {
    //       case (resource, chance) =>
    //         (resource -> result.getOrElse(resource, 1F) * chance)
    //     }
    // }
    ChanceCalculator(total, individualChances)
  }
}
