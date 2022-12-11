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

  // Left/Right is a case class, so should Either work?
  def eitherToString: String = {
    val foo = Foo("foo")
    val either: Either[String, Foo] = Right(foo)
    either.toString // assert: DisableCaseClassToString
  }

  // Some/None are case classes...
  def optionToString: String = {
    val foo = Foo("foo")
    val opt = Option(foo)
    opt.toString // assert: DisableCaseClassToString
  }

  def valueOf: String = {
    val foo = Foo("foo")
    String.valueOf(foo) // assert: DisableCaseClassToString
  }

  def valueOfSafe: String = {
    val foo = "foo"
    String.valueOf(foo) // assert: DisableCaseClassToString
  }

  def fooMethod: Foo = Foo("foo")
}

case class Foo(name: String)
