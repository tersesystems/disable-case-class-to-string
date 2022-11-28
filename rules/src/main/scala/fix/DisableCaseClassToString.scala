package fix

import scalafix.v1._
import scala.meta._

case class ToString(term: Term) extends Diagnostic {
  override def position: Position = term.pos
  override def message: String = {
    s"Case classes cannot call `${term.syntax}.toString` explicitly"
  }
}

case class Interp(term: Term) extends Diagnostic {
  override def position: Position = term.pos
  override def message: String =
    s"Case class ${term} cannot use toString in interpolation."
}

case class Any2String(term: Term) extends Diagnostic {
  override def position: Position = term.pos

  override def message: String =
    s"Case class ${term} cannot use implicit any2stringadd."
}

// https://github.com/vovapolu/scaluzzi/blob/master/scalafix/rules/src/main/scala/scalafix/internal/scaluzzi/Disable.scala
class DisableCaseClassToString extends SemanticRule("DisableCaseClassToString") {

  private val verbose = false

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case select@Term.Select(term, Term.Name("toString")) =>
        term match {
          case name: Term.Name =>
            // Term.Select(Term.Name("seq"), Term.Name("toString"))
            // if the toString is on a relevant type, then warn.
            if (isMatchingTerm(name)) {
              Patch.lint(ToString(name))
            } else {
              Patch.empty
            }

          case apply: Term.Apply =>
            if (isMatchingTerm(apply)) {
              Patch.lint(ToString(apply))
            } else {
              Patch.empty
            }
          case _ =>
            Patch.empty
        }

      case Term.Interpolate(Term.Name("s"), _, args) =>
        args.map { a =>
          if (isMatchingTerm(a)) Patch.lint(Interp(a)) else Patch.empty
        }.asPatch

      case Term.ApplyInfix(_, Term.Name("+"), _, args) =>
        args.map { a =>
            if (isMatchingTerm(a)) Patch.lint(Interp(a)) else Patch.empty
        }.asPatch

      case other: Term =>
        if (other.synthetics.nonEmpty) {
          other.synthetics.head match {
            case ApplyTree(TypeApplyTree(SelectTree(_, id), List(typeRef: TypeRef)), _) =>
              if (any2stringaddPlusString.matches(id.info.symbol) && typeRef.symbol.info.exists(_.isCase)) {
                Patch.lint(Any2String(other))
              } else {
                Patch.empty
              }
            case _ =>
              Patch.empty
          }
        } else {
          Patch.empty
        }

      case _ =>
        Patch.empty

    }.asPatch
  }

  private val any2stringaddPlusString: SymbolMatcher = SymbolMatcher.exact("scala/Predef.any2stringadd().")

  def isMatchingTerm(term: Term)(implicit doc: SemanticDocument): Boolean = {
    def isCase(tpe: SemanticType) = {
      tpe match {
        case TypeRef(_, symbol, _) =>
          symbol.info.get.isCase
        case other =>
          // println(s"isCaseClass tpe = $tpe other = $other")
          false
      }
    }

    def isIterable(tpe: SemanticType) = {
      tpe match {
        case TypeRef(_, symbol, typeArguments) =>
          symbol.info.exists { info =>
            if (verbose) {
              println(s"isIterable typeArguments = $typeArguments")
            }
            info.signature match {
              case TypeSignature(_, TypeRef(_, symbol, _), _) =>
                // Seq[Foo] is matched by being an Iterable with List(Foo)
                getParentSymbols(symbol).exists(iterableMatcher.matches) && typeArguments.exists(isCase)
              case _ =>
                false
            }
          }
        case _ =>
          false
      }
    }

    def isName(name: Term.Name) = {
      name.symbol.info.exists { info =>
        info.signature match {
          case ValueSignature(tpe) =>
            val caseClassResult = isCase(tpe)
            val iterResult = isIterable(tpe)
            if (verbose) {
              println(s"isCaseClass ValueSignature tpe = $tpe isCaseClass = $caseClassResult iterResult = $iterResult")
            }
            caseClassResult || iterResult
          case MethodSignature(typeParameters, parameterLists, tpe) =>
            if (verbose) {
              println(s"isCaseClass methodSignature typeParameters = $typeParameters parameterLists = ${parameterLists}")
            }
            isCase(tpe)
          case ClassSignature(_, parents, _, _) =>
            if (verbose) {
              println(s"isCaseClass classSignature ${parents.head}")
            }
            parents.head match {
              case TypeRef(_, _, typeArguments) =>
                isCase(typeArguments.last)
              case _ =>
                false
            }
          case other =>
            // println(s"isCaseClass: other structure = ${other.structure}")
            false
        }
      }
    }

    def isApply(fun: Term) = {
      if (verbose) {
        // fun = Seq
        //
        // https://scalacenter.github.io/scalafix/docs/developers/semantic-tree.html#look-up-inferred-type-parameter
        println(s"isCaseClass apply fun.synthetics = ${fun.structure}")
        println(s"isCaseClass apply fun.synthetics.structure = ${fun.synthetics.structure}")
      }

      if (fun.synthetics.nonEmpty) {
        fun.synthetics.head match {
          case TypeApplyTree(selectTree, List(TypeRef(_, symbol, _))) =>
            if (verbose) {
              println(s"isCaseClass apply selectTree = ${selectTree} isCase = ${symbol.info.get.isCase}")
            }
            (symbol.info.get.isCase)
          case other =>
            if (verbose) {
              println(s"isCaseClass apply is not type apply tree = ${other}")
            }
            false
        }
      } else {
        // Term.Apply(Term.Name("Seq"), List(Term.Name("foo")))
        isMatchingTerm(fun)
      }
    }

    term match {
      case Term.Apply(fun, args) =>
        isApply(fun)

      case name: Term.Name =>
        isName(name)
    }
  }

  def getParentSymbols(symbol: Symbol)(implicit doc: SemanticDocument): Set[Symbol] =
    symbol.info.get.signature match {
      case ClassSignature(_, parents, _, _) =>
        Set(symbol) ++ parents.collect {
          case TypeRef(_, symbol, _) => getParentSymbols(symbol)
        }.flatten
    }

  private val iterableMatcher: SymbolMatcher = SymbolMatcher.normalized("scala.collection.Iterable")
}
