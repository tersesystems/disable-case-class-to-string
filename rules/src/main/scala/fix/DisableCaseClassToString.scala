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

class DisableCaseClassToString extends SemanticRule("DisableCaseClassToString") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Select(term, Term.Name("toString")) =>
        term match {
          case name: Term.Name =>
            if (isCaseClass(name)) Patch.lint(ToString(name)) else Patch.empty
          case apply: Term.Apply =>
            if (isCaseClass(apply)) Patch.lint(ToString(apply)) else Patch.empty
          case _ =>
            Patch.empty
        }

      case Term.Interpolate(Term.Name("s"), _, args) =>
        args.map { a =>
          if (isCaseClass(a)) Patch.lint(Interp(a)) else Patch.empty
        }.asPatch

      case infixPlus@Term.ApplyInfix(lhs, Term.Name("+"), _, args) =>
        args.map { a =>
            if (isCaseClass(a)) Patch.lint(Interp(a)) else Patch.empty
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
    }.asPatch
  }

  private val any2stringaddPlusString: SymbolMatcher = SymbolMatcher.exact("scala/Predef.any2stringadd().")

  def isCaseClass(term: Term)(implicit doc: SemanticDocument): Boolean = {
    def isCase(tpe: SemanticType) = {
      tpe match {
        case TypeRef(_, symbol, _) =>
          symbol.info.get.isCase
        case other =>
          // println(s"isCaseClass tpe = $tpe other = $other")
          false
      }
    }

    term.symbol.info.exists { info =>
      info.signature match {
        case ValueSignature(tpe) =>
          // println(s"isCaseClass ValueSignature tpe = $tpe")
          isCase(tpe)
        case MethodSignature(_, _, tpe) =>
          // println(s"isCaseClass methodSignature value = $value case = ${value.isCase}")
          isCase(tpe)
        case ClassSignature(_, parents, _, _) =>
          // println(s"isCaseClass classSignature ${parents.head}")
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
}
