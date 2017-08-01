package shifts
package resource

case class Resource private (id: ResourceId,
                             name: String,
                             numberOfPatients: Int)
object Resource {
  def apply(name: String, numberOfPatients: Int): Resource =
    Resource(name.toLowerCase, name, numberOfPatients)
}
