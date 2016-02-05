package address.controllers

import address.domain.Address
import play.api.libs.json.Json

object AddressJsonProtocol {
  implicit val addressFormat = Json.format[Address]

}
