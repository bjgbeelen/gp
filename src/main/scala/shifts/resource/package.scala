package shifts

package object resource {
  type ResourceId = String

  implicit class MapResourceFloatExtension(map: Map[Resource, Float]) {
    def normalize() = {
      val total = map.foldLeft(0F) {
        case (result, (_, chance)) => result + chance
      }
      val result = map.map {
        case (resource, chance) => (resource -> chance / total)
      }
      if (total > 0F)
        assert(result.values.sum > 0.9999, s"${result.values.sum} is not 1.0")
      result
    }
  }
}
