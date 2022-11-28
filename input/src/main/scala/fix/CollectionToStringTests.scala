/*
rule = DisableCaseClassToString
*/
package fix

class CollectionToStringTests {

  def seqToString: String = {
    val foo = Foo("foo")
    /*
    Term.Select(
      Term.Apply(Term.Name("Seq"), List(Term.Name("foo"))),
      Term.Name("toString")
    )
    */
    Seq(foo).toString // assert: DisableCaseClassToString
  }

  def seqValToString: String = {
    val seq: Seq[Foo] = Seq(Foo("foo"))
    seq.toString // assert: DisableCaseClassToString
  }

  def seqToStringSafe: String = {
    val foo = "foo"
    Seq(foo).toString
  }

  //  def seqMkString: String = {
  //    val foo = Foo("foo")
  //    Seq(foo).mkString // assert: DisableCaseClassToString
  //  }
  //
  //  def seqMkStringSafe: String = {
  //    val foo = "foo"
  //    Seq(foo).mkString
  //  }
  //
  //  def mapToString: String = {
  //    val foo = Foo("foo")
  //    Map("foo" -> foo).toString // assert: DisableCaseClassToString
  //  }
  //
  //  def mapValToString: String = {
  //    val map = Map("foo" -> Foo("foo"))
  //    map.toString // assert: DisableCaseClassToString
  //  }
  //
  //  def mapToStringSafe: String = {
  //    val foo = "foo"
  //    Map("foo" -> foo).toString
  //  }

}
