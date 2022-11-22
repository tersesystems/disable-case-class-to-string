/*
rule = DisableCaseClassToString
 */
package fix

class StringPlusTests {

  def lit2Add: String = {
    val foo = Foo("name")
    "" + foo // assert: DisableCaseClassToString
  }

  def method2Add: String = {
    val foo = Foo("name")
    stringMethod + foo // assert: DisableCaseClassToString
  }

  def stringMethod: String = ""

}
