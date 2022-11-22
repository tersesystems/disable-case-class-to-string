/*
rule = DisableCaseClassToString
 */
package fix

// tests/testOnly *Suite -- -z Any2StringTests
class Any2StringTests {

  def implicitAny2stringAdd: Unit = {
    val foo = Foo("name")
    // any2stringadd(foo)
    val s = foo + "" // assert: DisableCaseClassToString
    println(s)
  }

  def explicitAny2StringAdd: Unit = {
    val foo = Foo("name")
      val s = any2stringadd(foo) + "" // assert: DisableCaseClassToString
    println(s)
  }

}
