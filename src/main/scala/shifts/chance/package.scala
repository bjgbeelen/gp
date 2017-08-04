package shifts

import task._
import resource._
import schedule._
import counter._
import calendar._

package object chance {
  type ChanceInfluencer = Task => Map[Resource, Float]
}
