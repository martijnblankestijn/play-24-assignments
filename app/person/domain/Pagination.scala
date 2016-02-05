package person.domain

case class Pagination(start: Int, size: Int, count: Option[Int] = None) {
  val hasNoContent: Boolean = size == 0

  def toContentRange: String =
    if(size == 0) s"*/0"
    else s"$start-${start + size -1}/" + count.getOrElse("*")
}

object Pagination {
  def apply(value: String): Option[Range] = {
    val parts: Array[Int] = value.split('-').map(_.toInt)
    val range = Range.inclusive(parts(0), parts(1))
    if (range.start >= 0 && range.end >= range.start) Some(range)
    else None
  }
}
