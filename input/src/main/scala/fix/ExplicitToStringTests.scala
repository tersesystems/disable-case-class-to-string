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

  def seqMkString: String = {
    val foo = Foo("foo")
    Seq(foo).mkString // assert: DisableCaseClassToString
  }

  def seqToString: String = {
    val foo = Foo("foo")
    Seq(foo).toString // assert: DisableCaseClassToString
  }

  def mapToString: String = {
    val foo = Foo("foo")
    Map("foo" -> foo).toString // assert: DisableCaseClassToString
  }

  def fooMethod: Foo = Foo("foo")
}

case class Foo(name: String)
