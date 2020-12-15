package com.github.windbird123.encoding.declarative

import simulacrum.typeclass
@typeclass trait HasLength[A] {
  def getLength(a: A): Int
}

case class Email(subject: String)
object Email {
  implicit val hasLengthImpl: HasLength[Email] = new HasLength[Email] {
    override def getLength(a: Email): Int = a.subject.length
  }
}

import com.github.windbird123.encoding.declarative.HasLength.ops._

sealed trait EmailFilter { self =>
  def matches(email: Email): Boolean = self match {
    case And(left, right)     => left.matches(email) && right.matches(email)
    case ContainFilter(term)  => email.subject.contains(term)
    case LengthFilter(minLen) => email.getLength > minLen
  }

  def &&(that: EmailFilter): EmailFilter = And(self, that)
}

final case class And(left: EmailFilter, right: EmailFilter) extends EmailFilter
final case class ContainFilter(term: String)                extends EmailFilter
final case class LengthFilter(minLen: Int)                  extends EmailFilter

object MyEncoding {
  def main(args: Array[String]): Unit = {
    val email = Email("My Subject")

    val myFilter = ContainFilter("My") && ContainFilter("Subject") && LengthFilter(3)
    val out         = Seq(email).filter(email => myFilter.matches(email))
    println(out)
  }
}
