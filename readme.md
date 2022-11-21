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

```hocon
rules = [
  DisableCaseClassToString
]
```