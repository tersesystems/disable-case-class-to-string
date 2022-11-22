/*
rule = DisableCaseClassToString
*/
package fix

class InterpolateTests {

  def interpolateMethod: String = {
    s"${fooMethod}" // assert: DisableCaseClassToString
  }

  def interpolateValue: String = {
    val foo = Foo("foo")
    s"${foo}" // assert: DisableCaseClassToString
  }

  def interpolateApply: String = {
    s"${Foo("foo")}" // assert: DisableCaseClassToString
  }

  def interpolateAndToString: String = {
    s"${Foo("foo").toString}" // assert: DisableCaseClassToString
  }

  def fooMethod: Foo = Foo("foo")
}
