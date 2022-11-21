package fix

import scalafix.v1._
import scala.meta._

object code {
  def apply(s: String): String = s"`$s`"
}

case class ToString(term: Term) extends Diagnostic {
  override def position: Position = term.pos
  override def message: String =
    s"Case classes cannot use ${code(s"${term.syntax}")}}"
}

case class Interp(term: Term) extends Diagnostic {
  override def position: Position = term.pos
  override def message: String =
    s"Case classes cannot use toString in interpolation."
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
      case _ =>
        Patch.empty
    }.asPatch
  }

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

    term.symbol.info.get.signature match {
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
