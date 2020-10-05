package com.github.windbird123.encoding.declarative

case class Email(subject: String)

sealed trait EmailFilter { self =>
  def &&(that: EmailFilter) : EmailFilter = And(self, that)
}

final case class And(left: EmailFilter, right: EmailFilter) extends EmailFilter
final case class ContainFilter(term: String) extends EmailFilter

object EmailFilter {
  def matches(filter: EmailFilter, email: Email) : Boolean = filter match {
    case And(left, right) => matches(left, email) && matches(right, email)
    case ContainFilter(term) => email.subject.contains(term)
  }
}

object MyEncoding {
  def main(args: Array[String]): Unit = {
    val email = Email("My Subject")

    val filter = ContainFilter("My") && ContainFilter("Subject")
    Seq(email).filter(email => EmailFilter.matches(filter, email))
  }
}
