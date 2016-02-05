package address.repo

import javax.inject.Inject

import address.domain.Address
import play.api.Logger
import play.api.http.{ContentTypes, HeaderNames}
import play.api.libs.json.{JsArray, JsLookupResult}
import play.api.libs.ws.{WSClient, WSRequest}

import scala.concurrent.Future

/**
  * Documentation for API is http://www.postcodedata.nl/api/request/
  */
class PostcodeDataAddressRepository extends AddressRepository {
  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
  private val postcodeUrl: String = "http://api.postcodedata.nl/v1/postcode/"


  private def convert(result: JsLookupResult, houseNumber: Int): Address = {
    Address(
      streetName = (result \ "street").as[String],
      houseNumber = houseNumber,
      postalCode = (result \ "postcode").as[String],
      city = (result \ "city").as[String],
      municipality = (result \ "municipality").as[String],
      province = (result \ "province").as[String]
    )

  }

  override def get(postalCode: String, houseNumber: Int) = {
    Future.successful(None)
  }
}
