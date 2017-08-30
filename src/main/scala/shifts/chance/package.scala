package shifts

import task._
import resource._
import schedule._
import counter._
import calendar._

package object chance {
  //type ChanceInfluencer = Task => Float
}

trait ChanceInfluencer {
  def chance(task: Task): Float
}
