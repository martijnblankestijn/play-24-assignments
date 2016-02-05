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


class PersonController @Inject()(personRepo: PersonRepository, addressRepo: AddressRepository) extends Controller {


  def persons() = Action.async { request =>
    val range: Range = request.headers.get(RANGE).flatMap(Pagination(_)).getOrElse(Range(0, 99))
    personRepo.getPaged(range)
      .map(persons => {
        val result = if (persons._2.hasNoContent) NoContent
        else PartialContent(Json.toJson(persons._1))
        result
          .withHeaders(
            CONTENT_RANGE -> persons._2.toContentRange,
            "Range-Unit" -> "items")
      })
    // without the extra assignment.
    // personRepo.getAll.map(p => Ok(toJson(p)))
  }

  def get(id: Long) = Action.async {
    personRepo.get(id)
      .map(
        _.map(p => Ok(toJson(p)))
          .getOrElse(NotFound))
  }

  def getPersonDetails(id: Long) = Action.async {

    def getAddress(p: Person): Future[Option[Address]] = {
      addressRepo.get(p.postalCode, p.houseNumber)
    }

    personRepo.get(id)
      .flatMap {
        optionalPerson => optionalPerson.map {
          person =>
            addressRepo.get(person.postalCode, person.houseNumber)
              .map(optionAddress => createResponse(person, optionAddress))
              .recoverWith {
                case e: ConnectException =>
                  Logger.warn(s"Error connecting to endpoint")
                  Future.successful(createResponse(person, None))
              }
        }
          .getOrElse(Future.successful(NotFound))
      }
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


