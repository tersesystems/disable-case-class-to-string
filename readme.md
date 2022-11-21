# Scalafix rules for DisableCaseClassToString

This is a Scalafix rule that disables the `toString` method on case classes, preventing leaks of sensitive information.

Both explicit `toString` and string interpolation are disabled:

```scala
def explicitToString = {
  val foo = Foo("name")
  foo.toString // assert DisableCaseClassToString
}
```

asserts, as does:

```scala
def explicitToString = {
  val foo = Foo("name")
  s"$foo" // assert DisableCaseClassToString
}
```

Also `any2stringadd` and string synthetic addition should prevent an implicit `toString` on a case class:

```scala
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
```

## Configuration

```hocon
rules = [
  DisableCaseClassToString
]
```