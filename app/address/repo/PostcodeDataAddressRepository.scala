package address.repo

import javax.inject.Inject

import address.domain.Address
import play.api.Logger
import play.api.http.{ContentTypes, HeaderNames}
import play.api.libs.json.{JsArray, JsLookupResult}
import play.api.libs.ws.{WSClient, WSRequest}

/**
  * Documentation for API is http://www.postcodedata.nl/api/request/
  *
  * @param wsClient
  */
class PostcodeDataAddressRepository @Inject()(wsClient: WSClient) extends AddressRepository {
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
    Logger.debug(s"Retrieving address details from postal code $postalCode and house number $houseNumber")
    val req: WSRequest = wsClient.url(postcodeUrl)
      .withHeaders(HeaderNames.ACCEPT -> ContentTypes.JSON)
      .withQueryString(
        "streetnumber" -> houseNumber.toString,
        "postcode" -> postalCode,
        "ref" -> "somewhere.nl")

    req.get()
      .map(r =>
        (r.json \ "status").as[String] match {
          case "ok" =>
            val firstAddress = (r.json \ "details").as[JsArray].head
            Some(convert(firstAddress, houseNumber))
          case "error" =>
            Logger.info(s"Postal code $postalCode and $houseNumber returns error: ${r.json}")
            (r.json \ "errormessage").as[String] match {
              case "no results" => None
              case _ => throw new IllegalStateException
            }
        }
      )
  }
}
