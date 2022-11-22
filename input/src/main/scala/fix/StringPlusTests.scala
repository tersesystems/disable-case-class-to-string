/*
rule = DisableCaseClassToString
 */
package fix

class StringPlusTests {

  def lit2Add: Unit = {
    val foo = Foo("name")
    val s = "" + foo // assert: DisableCaseClassToString
    println(s)
  }

  def method2Add: Unit = {
    val foo = Foo("name")
    val s = stringMethod + foo // assert: DisableCaseClassToString
    println(s)
  }

  def stringMethod: String = ""

}
