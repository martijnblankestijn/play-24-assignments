package person.repo

import javax.inject.Inject

import person.domain.{Pagination, Person}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class SlickPersonRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) extends PersonRepository {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.driver.api._

  val persons = TableQuery[Persons]

  // save some persons to the database
  DefaultData.availablePersons.foreach(saveNew)

  private def filterOnId(id: Long) = persons.filter(_.id === id)
  private def deleteAction(id: Long) = filterOnId(id).delete

  override def getAll = {
    persons.schema.create.statements.foreach(println)
    dbConfig.db.run(persons.result)
  }

  override def getPaged(range: Range) = {
    for {
      count <- dbConfig.db.run(persons.length.result)
      persons <- dbConfig.db.run(persons.drop(range.start).take(range.length).result)
    } yield (persons, Pagination(range.start, persons.size, Some(count)))
  }


  override def saveNew(person: Person) = dbConfig.db.run(persons += person).map(_ => person)
  override def update(person: Person) = dbConfig.db.run(filterOnId(person.id).update(person)).map(_ => ())
  override def get(id: Long) = dbConfig.db.run(filterOnId(id).result.headOption)
  override def delete(id: Long) = dbConfig.db.run(deleteAction(id))
}

class Persons(tag: Tag) extends Table[Person](tag, "person") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("first_name")
  def postalCode = column[String]("postal_code")
  def houseNumber = column[Int]("house_no")

  def * = (name, postalCode, houseNumber, id) <> ((Person.apply _).tupled, Person.unapply)
}