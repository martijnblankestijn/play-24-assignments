package person.controllers

import person.domain.Person
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object PersonJsonProtocol {
  //  implicit val personFormat = Json.format[Person]

  implicit val personWrites = Json.writes[Person]

  private def between(min: Int, max:Int): Reads[String] =
     minLength[String](min) keepAnd maxLength[String](max)

  //Person(firstName: String, postalCode: String, houseNumber: Int, id: Long)
  implicit val personReads = (
    (__ \ "firstName").read[String](between(2,30)) and
      (__ \ "postalCode").read[String](pattern("^[0-9]{4}\\s*[a-zA-Z]{2}$".r)) and
      (__ \ "houseNumber").read[Int](min(0) keepAnd max(99999)) and
      ( __ \ "id").read[Long]
    )(Person.apply _)
}