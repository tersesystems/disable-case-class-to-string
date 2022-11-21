/*
rule = DisableCaseClassToString
 */
package fix

object DisableCaseClassToString_Test {
  def explicitToString: String = {
    val foo = Foo("foo")
    foo.toString // assert: DisableCaseClassToString
  }

  def applyToString: String = {
    Foo("foo").toString // assert: DisableCaseClassToString
  }

  def methodToString: String = {
    fooMethod.toString // assert: DisableCaseClassToString
  }

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

  def plus2Add: String = {
    val foo = Foo("name")
    "" + foo  // assert: DisableCaseClassToString
  }
  def fooMethod: Foo = Foo("foo")
}

case class Foo(name: String)
