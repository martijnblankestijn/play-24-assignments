package person.controllers

import person.domain.Person
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.util.matching.Regex

object PersonJsonProtocol {
  implicit val personWrites = Json.writes[Person]
  private val postalCodeR = "^[0-9]{4}\\s*[a-zA-Z]{2}$".r

  implicit val personReads = (
      (__ \ "firstName").read[String](
        between(2, 30)) and
      (__ \ "postalCode").read[String](
        pattern(postalCodeR)) and
      (__ \ "houseNumber").read[Int](
        min(0) keepAnd max(99999)) and
      (__ \ "id").read[Long]
    ) (Person.apply _)

  private def between(min: Int, max: Int): Reads[String] =
    minLength[String](min) keepAnd maxLength[String](max)
}