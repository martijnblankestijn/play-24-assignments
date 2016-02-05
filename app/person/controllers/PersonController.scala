package person.controllers

import java.net.{ConnectException, SocketException}
import javax.inject.Inject

import address.controllers.AddressJsonProtocol._
import address.domain.Address
import address.repo.AddressRepository
import person.controllers.PersonJsonProtocol._
import person.domain.{Pagination, Person}
import person.repo.PersonRepository
import play.api.Logger
import play.api.http.HeaderNames
import play.api.libs.json.Json.toJson
import play.api.libs.json._
import play.api.mvc.{Action, BodyParsers, Controller, Result}


import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future


class PersonController @Inject()(personRepo: PersonRepository) extends Controller {


  def persons() = Action.async { request =>
    personRepo.getAll.map(p => Ok(toJson(p)))
  }

  def get(id: Long) = Action.async {
    personRepo.get(id)
      .map(
        _.map(p => Ok(toJson(p)))
          .getOrElse(NotFound))
  }

  def saveNew() = Action.async(BodyParsers.parse.json) { request =>
    val result: JsResult[Person] = request.body.validate[Person]
    result.fold(
      errors => Future.successful(InternalServerError(JsError.toJson(errors))),
      p => personRepo.saveNew(p)
        .map(p => Created(toJson(p))
          .withHeaders(
            HeaderNames.LOCATION -> person.controllers.routes.PersonController.get(p.id).url))
    )
  }

  def update(id: Long) = Action.async(BodyParsers.parse.json) { request =>
    val result: JsResult[Person] = request.body.validate[Person]
    result.fold(
      errors => Future.successful(InternalServerError(JsError.toJson(errors))),
      person => personRepo.update(person.copy(id = id)).map(_ => NoContent)
    )
  }

  def delete(id: Long) = Action.async {
    personRepo.delete(id)
      .map(_ => NoContent)
  }


  private def createResponse(p: Person, o: Option[Address]): Result = {
    val result = o.map(a => Json.obj(
      ("person", toJson(p)),
      ("address", toJson(a))
    )).getOrElse(
      Json.obj(("person", toJson(p)))
    )
    Ok(result)
  }

}


