package address.controllers


import java.net.ConnectException

import address.controllers.AddressJsonProtocol._
import address.domain.Address
import address.repo.AddressRepository
import base.AbstractProjectSpec
import org.mockito.Mockito._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

// Unit testing Controllers
// see https://www.playframework.com/documentation/2.4.x/ScalaTestingWithScalaTest#Unit-Testing-Controllers
class AddressControllerSpec extends AbstractProjectSpec {
  val pc = "3439LM"
  val houseNo = 1
  implicit val timeout = 500

  "AddressController#address" should {
    "return the address that was found" in {
      val address: Address =
        Address("Ringwade", houseNo, pc, "Nieuwegein", "Nieuwegein", "Utrecht")

      val repo = mock[AddressRepository]
      when(repo.get(pc, houseNo)) thenReturn
        Future.successful(Some(address))

      val sut = new AddressController(repo)

      val actual = sut.getAddress(pc, houseNo).apply(FakeRequest())

      contentAsJson(actual).as[Address] mustBe address
      status(actual) must be(OK)
    }

    "return 404 when no address was found" in {
      val repo = mock[AddressRepository]
      when(repo.get(pc, houseNo)) thenReturn
        Future.successful(None)

      val sut = new AddressController(repo)

      val actual = sut.getAddress(pc, houseNo)
                      .apply(FakeRequest())

      status(actual) mustBe NOT_FOUND
    }

    "return internal server error when not connecting to web service" in {
      val repo = mock[AddressRepository]
      when(repo.get(pc, houseNo)) thenReturn Future.failed(new ConnectException)

      val sut = new AddressController(repo)

      val actual = sut.getAddress(pc, houseNo).apply(FakeRequest())

      status(actual) mustBe INTERNAL_SERVER_ERROR
      contentAsString(actual) mustBe
        """{"error":"Error connecting to backend"}"""
      contentAsJson(actual) mustBe
        Json.parse("""{"error":"Error connecting to backend"}""")
      contentAsJson(actual) mustBe
        Json.toJson(Json.obj("error" -> "Error connecting to backend"))
    }
  }
}