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

  def rightToStringSafe: String = {
    val foo = "foo"
    val either: Right[Nothing, String] = Right(foo)
    either.toString
  }

  def leftToStringSafe: String = {
    val foo = "foo"
    val either: Left[String, Nothing] = Left(foo)
    either.toString
  }

  // Some/None are case classes...
  def optionToString: String = {
    val foo = Foo("foo")
    val opt = Option(foo)
    opt.toString // assert: DisableCaseClassToString
  }

  def optionToStringSafe: String = {
    val foo = "foo"
    val opt = Option(foo)
    opt.toString
  }

  def someToStringSafe: String = {
    val foo = "foo"
    val opt = Some(foo)
    opt.toString
  }

  def noneToStringSafe: String = {
    val opt = None
    opt.toString
  }

  def valueOf: String = {
    val foo = Foo("foo")
    String.valueOf(foo) // assert: DisableCaseClassToString
  }

  def valueOfSafe: String = {
    val foo = "foo"
    String.valueOf(foo) // assert: DisableCaseClassToString
  }

  def seqMkString: String = {
    val foo = Foo("foo")
    Seq(foo).mkString // assert: DisableCaseClassToString
  }

  def seqMkStringSafe: String = {
    val foo = "foo"
    Seq(foo).mkString
  }

  def seqToString: String = {
    val foo = Foo("foo")
    Seq(foo).toString // assert: DisableCaseClassToString
  }

  def seqToStringSafe: String = {
    val foo = "foo"
    Seq(foo).toString
  }

  def mapToString: String = {
    val foo = Foo("foo")
    Map("foo" -> foo).toString // assert: DisableCaseClassToString
  }

  def mapToStringSafe: String = {
    val foo = "foo"
    Map("foo" -> foo).toString // assert: DisableCaseClassToString
  }

  def fooMethod: Foo = Foo("foo")
}

case class Foo(name: String)
