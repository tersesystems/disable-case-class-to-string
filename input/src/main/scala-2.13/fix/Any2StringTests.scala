/*
rule = DisableCaseClassToString
 */
package fix

class Any2StringTests {

  def lit2Add: String = {
    val foo = Foo("name")
    "" + foo // assert: DisableCaseClassToString
  }

  def method2Add: String = {
    val foo = Foo("name")
    stringMethod + foo // assert: DisableCaseClassToString
  }

  def stillString: String = {
    val foo = Foo("name")
    // any2stringadd(foo)
    foo + (stringMethod) // assert: DisableCaseClassToString
  }

  def stringMethod: String = ""

}
