/*
rule = DisableProductToString
 */
package fix

object DisableProductToString_Test {
  def explicitToString: String = {
    val foo = Foo("foo")
    foo.toString // assert: DisableProductToString
  }

  def applyToString: String = {
    Foo("foo").toString // assert: DisableProductToString
  }

  def methodToString: String = {
    fooMethod.toString // assert: DisableProductToString
  }

  def interpolateMethod: String = {
    s"${fooMethod}" // assert: DisableProductToString
  }

  def interpolateValue: String = {
    val foo = Foo("foo")
    s"${foo}" // assert: DisableProductToString
  }

  def interpolateApply: String = {
    s"${Foo("foo")}" // assert: DisableProductToString
  }

  def interpolateAndToString: String = {
    s"${Foo("foo").toString}" // assert: DisableProductToString
  }

  def fooMethod: Foo = Foo("foo")
}

case class Foo(name: String)
