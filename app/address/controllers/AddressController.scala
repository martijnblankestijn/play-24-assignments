package address.controllers

import javax.inject.Inject

import address.repo.AddressRepository
import play.api.mvc.Controller


class AddressController @Inject()(addressRepo: AddressRepository) extends Controller {

  def getAddress(postalCode: String, houseNumber: Int) =  TODO
}