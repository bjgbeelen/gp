package shifts
package schedule

import scala.annotation.tailrec

import chance._
import resource._

case class ResourcePicker(chanceCalculator: ChanceCalculator) {
  def pick(): Option[Resource] = {
    val applicableResources = chanceCalculator.chances.filter {
      case (resource, chance) => chance > 0F
    }
    val randomNumber = scala.util.Random.nextFloat
    ResourcePicker.pickByChance(randomNumber, applicableResources.normalize.toList)
  }
}

object ResourcePicker {
  @tailrec
  def pickByChance(chance: Float, items: List[(Resource, Float)]): Option[Resource] =
    items match {
      case Nil => None
      case (resource, resourceChance) :: tail =>
        if (chance <= resourceChance)
          Some(resource)
        else pickByChance(chance - resourceChance, tail)
    }
}
