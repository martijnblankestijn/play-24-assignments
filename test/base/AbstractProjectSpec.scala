package base

import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Results

/**
  * As suggested <a href="http://scalatest.org/user_guide/defining_base_classes">here</a> a base class
  * for all tests in this project
  */
abstract class AbstractProjectSpec extends PlaySpec with MockitoSugar with Results {
}
