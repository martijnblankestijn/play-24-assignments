package person.controllers

import address.repo.AddressRepository
import base.AbstractProjectSpec
import person.domain.{Pagination, Person}
import person.repo.PersonRepository
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.Future
import org.mockito.Mockito._
import PersonJsonProtocol._
import org.mockito.Matchers.{eq => eqTo, _}

class PersonControllerSpec extends AbstractProjectSpec {


  "PersonController" should {
    "return all persons" in {

      val personRepo = mock[PersonRepository]
      val addressRepo = mock[AddressRepository]

      val expected = Seq(Person("Martin", "1000AA", 25, 125L))
      val range = Range(0,99)
      val pagination = new Pagination(1, 1, Some(1))
      when(personRepo.getPaged(range)) thenReturn Future.successful((expected, pagination))

      val sut = new PersonController(personRepo, addressRepo)

      val result: Future[Result] = sut.persons().apply(FakeRequest())

      status(result) mustBe PARTIAL_CONTENT
      header(CONTENT_RANGE, result) mustBe Some("1-1/1")
      contentAsJson(result) mustBe Json.toJson(expected)
      // or
      contentAsJson(result).as[List[Person]] mustBe expected

    }

    "save a new person to the repository" in {
      val personRepo = mock[PersonRepository]
      val addressRepo = mock[AddressRepository]

      val newPerson = Person("Martin", "1000AA", 125, 0L)
      when(personRepo.saveNew(eqTo(newPerson))) thenReturn
        Future.successful(newPerson.copy(id = 129L))
      // or
      // when(personRepo.saveNew(any[Person])) thenReturn Future.successful(newPerson.copy(id = 129L))

      val sut = new PersonController(personRepo, addressRepo)

      val request = FakeRequest("POST", "/api/persons")
        .withJsonBody(Json.parse(
            """{ "firstName": "Martin",
                | "postalCode": "1000AA",
                | "houseNumber": 125,
                | "id": 0 }""".stripMargin)
        )
      val actual = call(sut.saveNew, request)

      status(actual) mustBe CREATED

    }
  }
}
