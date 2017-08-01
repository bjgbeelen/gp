package shifts

import task._
import resource._
import schedule._
import counter._

package object chance {
  type ChanceInfluencer = (Task,
                           Seq[Counter],
                           Map[Resource, ResourceConstraints],
                           Map[Resource, Set[Task]]) => Map[Resource, Float]
}
