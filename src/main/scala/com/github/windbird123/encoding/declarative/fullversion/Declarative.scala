package com.github.windbird123.encoding.declarative.fullversion

// https://degoes.net/articles/functional-design

case class Email(subject: String)

sealed trait EmailFilter { self =>
  def &&(that: EmailFilter): EmailFilter = And(self, that)
  def ||(that: EmailFilter): EmailFilter = Or(self, that)
  def unary_! : EmailFilter              = Not(self)
}

final case class SubjectContains(phrase: String)            extends EmailFilter
final case class And(left: EmailFilter, right: EmailFilter) extends EmailFilter
final case class Or(left: EmailFilter, right: EmailFilter)  extends EmailFilter
final case class Not(value: EmailFilter)                    extends EmailFilter

object Declarative {
  def subjectContains(phrase: String): EmailFilter = SubjectContains(phrase)

  def matches(filter: EmailFilter, email: Email): Boolean = filter match {
    case And(l, r) => matches(l, email) && matches(r, email)
    case Or(l, r) => matches(l, email) || matches(r, email)
    case Not(v) => !matches(v, email)
    case SubjectContains(phrase) => email.subject.contains(phrase)
  }

  // matches 이외의 method 를 또 추가할 때 유리..
  def describe(filter: EmailFilter): String = filter match {
    case And(l, r) => describe(l) + " && " + describe(r)
    case Or(l, r) => describe(l) + " || " + describe(r)
    case Not(v) => "! " +  describe(v)
    case SubjectContains(phrase) => s"subject contains $phrase"
  }

  def main(args: Array[String]): Unit = {
    val sampleFilter = And(
      Or(SubjectContains("discount"), SubjectContains("clearance")),
      Not(SubjectContains("liquidation"))
    )
  }
}
