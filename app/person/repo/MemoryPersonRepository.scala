package person.repo

import person.domain.{Pagination, Person}

trait PersonRepository {
  def getAll: Seq[Person]

  def get(id: Long): Option[Person]

  def saveNew(person: Person): Person

  def update(person: Person): Unit

  def delete(id: Long): Int

  def getPaged(range: Range): (Seq[Person], Pagination)
}


object MemoryPersonRepository extends PersonRepository {
  var persons = DefaultData.availablePersons.zipWithIndex.map { case (person, i) => person.copy(id = i)}
  var counter = persons.length

  override def getAll = persons

  override def saveNew(person: Person): Person = {
    counter = counter + 1
    val newPerson = person.copy(id = counter)
    persons = persons :+ newPerson
    newPerson
  }

  override def update(person: Person): Unit = {
    println(s"Updating person $person")
    get(person.id).map(p => {
      persons = persons.filterNot(_.id == person.id) :+ person
      ()
    }).getOrElse(throw new IllegalArgumentException(s"No person with id ${person.id} found."))
  }


  override def get(id: Long) = persons.find(_.id == id)

  override def delete(id: Long) = {
    if (persons.exists(_.id == id)) {
      persons = persons.filterNot(_.id == id)
      1
    }
    else 0
  }

  override def getPaged(range: Range) = ???
}