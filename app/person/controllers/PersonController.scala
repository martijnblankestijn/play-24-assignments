package person.controllers

import person.repo.{PersonRepository, MemoryPersonRepository}


class PersonController {
  val personRepo: PersonRepository = MemoryPersonRepository

}


