package shifts
package chance
package influencer

import task._
import resource._
import schedule._
import counter._
import calendar._

trait ChanceInfluencer {
  def chance(task: Task): Float
}
