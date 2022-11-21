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

  def lit2Add: String = {
    val foo = Foo("name")
    "" + foo  // assert: DisableCaseClassToString
  }

  def method2Add: String = {
    val foo = Foo("name")
    stringMethod + foo // assert: DisableCaseClassToString
  }

  def stillString: String = {
    val foo = Foo("name")
    // any2stringadd(foo)
    foo + stringMethod // assert: DisableCaseClassToString
  }

  def stringMethod: String = ""

  def fooMethod: Foo = Foo("foo")
}

case class Foo(name: String)
