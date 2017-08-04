package shifts

import calendar._
import schedule._

package object task {
  type Hour = Int
  type Minute = Int
  type Tag = String

  implicit class IntAsTime(minutes: Int) {
    def ::(hours: Int) = hours * 60 + minutes
  }

  type TaskId = String
}
