/*
rule = DisableCaseClassToString
 */
package fix

object ExplicitToStringTests {
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

  def valueOf: String = {
    val foo = Foo("foo")
    String.valueOf(foo) // assert: DisableCaseClassToString
  }

  def fooMethod: Foo = Foo("foo")
}

case class Foo(name: String)
