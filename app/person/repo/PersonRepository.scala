package person.repo

import com.google.inject.ImplementedBy
import person.domain.{Pagination, Person}

import scala.concurrent.Future

/**
  * Created by mblankestijn on 22/01/16.
  */
@ImplementedBy(classOf[SlickPersonRepository])
trait PersonRepository {
  def getAll: Future[Seq[Person]]
  def get(id: Long): Future[Option[Person]]
  def saveNew(person: Person): Future[Person]
  def update(person: Person): Future[Unit]
  def delete(id: Long): Future[Int]
  def getPaged(range: Range): Future[(Seq[Person], Pagination)]
}
