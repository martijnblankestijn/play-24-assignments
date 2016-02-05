package person.controllers

import person.controllers.PersonJsonProtocol._
import person.domain.Person
import person.repo.MemoryPersonRepository
import play.api.http.HeaderNames
import play.api.libs.json.Json.toJson
import play.api.libs.json._
import play.api.mvc.{Action, BodyParsers, Controller}


class PersonController extends Controller {
  val personRepo = MemoryPersonRepository

  def persons() = Action {
    Ok(toJson(personRepo.getAll))
  }

  def get(id: Long) = Action {
    personRepo.get(id)
      .map(p => Ok(toJson(p)))
      .getOrElse(NotFound)
  }

  def saveNew() = Action(BodyParsers.parse.json) { request =>
    val result: JsResult[Person] = request.body.validate[Person]
    result.fold(
      errors => InternalServerError(JsError.toJson(errors)),
      p => {
        val newPerson: Person = personRepo.saveNew(p)
        Created(toJson(newPerson))
          .withHeaders(
            HeaderNames.LOCATION -> person.controllers.routes.PersonController.get(newPerson.id).url)
      }
    )
  }

  def update(id: Long) = Action(BodyParsers.parse.json) { request =>
    val result: JsResult[Person] = request.body.validate[Person]
    result.fold(
      errors => InternalServerError(JsError.toJson(errors)),
      person => {
        personRepo.update(person.copy(id = id))
        NoContent
      }
    )
  }

  def delete(id: Long) = Action {
    personRepo.delete(id)
    NoContent
  }
}


