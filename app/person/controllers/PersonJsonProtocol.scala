package person.controllers

import person.domain.Person
import play.api.libs.json.Json

object PersonJsonProtocol {
  implicit val personFormat = Json.format[Person]
}
