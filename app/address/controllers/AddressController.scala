package address.controllers

import java.net.SocketException
import javax.inject.Inject

import address.controllers.AddressJsonProtocol._
import address.repo.AddressRepository
import play.api.libs.json.Json.toJson
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future


class AddressController @Inject()(addressRepo: AddressRepository) extends Controller {

  def getAddress(postalCode: String, houseNumber: Int) = Action.async {
    addressRepo.get(postalCode, houseNumber)
      .map(
        _.map(address => Ok(toJson(address)))
          .getOrElse(NotFound))
      .recoverWith {
        case e: SocketException =>
          Future.successful(InternalServerError(Json.toJson(Json.obj("error" -> "Error connecting to backend"))))
      }
  }
}