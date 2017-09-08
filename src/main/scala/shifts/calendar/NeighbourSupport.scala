package shifts
package calendar

trait NeighbourSupport[C, P <: CalendarNode[C]] {
  val parent: () ⇒ P

  private lazy val parentHasNeighhbourSupport =
    parent().isInstanceOf[NeighbourSupport[P, _]]
  private lazy val parentsParent: NeighbourSupport[P, _] =
    if (parentHasNeighhbourSupport)
      parent().asInstanceOf[NeighbourSupport[P, _]]
    else null

  lazy val previous: Option[C] = parent().children
    .sliding(2)
    .find {
      case x :: y :: z if y == this ⇒ true
      case _                        ⇒ false
    }
    .map {
      case x :: y ⇒ x.asInstanceOf[C]
    } match {
    case result @ Some(_) ⇒ result
    case None if parentHasNeighhbourSupport ⇒
      parentsParent.previous.flatMap(_.children.lastOption)
    case _ ⇒ None
  }

  lazy val next: Option[C] = parent().children
    .sliding(2)
    .find {
      case x :: y :: z if x == this ⇒ true
      case _                        ⇒ false
    }
    .map {
      case x :: y :: z ⇒ y.asInstanceOf[C]
    } match {
    case result @ Some(_) ⇒ result
    case None if parentHasNeighhbourSupport ⇒
      parentsParent.next.flatMap(_.children.headOption)
    case _ ⇒ None
  }
}

trait CalendarNode[C] {
  val children: Seq[C]

  lazy val days: Seq[Day] = children.foldLeft(Seq[Day]()) {
    case (seq: Seq[Day], child: CalendarNode[_]) ⇒ seq ++ child.days
    case (seq: Seq[Day], child: Day)             ⇒ seq :+ child
  }
}
