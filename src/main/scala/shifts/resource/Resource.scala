package shifts
package resource

case class Resource private (id: ResourceId, name: String, numberOfPatients: Int)
object Resource {
  def apply(name: String, numberOfPatients: Int): Resource = {
    val id = name.replace(",", "").replace(" ", "_").toLowerCase
    Resource(id, name, numberOfPatients)
  }
}
