package address.repo

import address.domain.Address
import com.google.inject.ImplementedBy

import scala.concurrent.Future

@ImplementedBy(classOf[PostcodeDataAddressRepository])
trait AddressRepository {
  def get(postalCode: String, houseNumber: Int): Future[Option[Address]]
}


