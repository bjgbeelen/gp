package shifts
package resource

import calendar.Calendar

case class Resource private (id: ResourceId, name: String, numberOfPatients: Int)
object Resource {
  def apply(name: String, numberOfPatients: Int): Resource = {
    val id = name.replace(",", "").replace(" ", "_").toLowerCase
    Resource(id, name, numberOfPatients)
  }
}

case class ResourceView(id: ResourceId, name: String, numberOfPatients: Int, calendarName: String)

object ResourceView {
  def from(calendarName: String)(resource: Resource) =
    ResourceView(resource.id, resource.name, resource.numberOfPatients, calendarName)
}
