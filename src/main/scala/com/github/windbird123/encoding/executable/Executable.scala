package com.github.windbird123.encoding.executable

// https://degoes.net/articles/functional-design

case class Email(subject: String)

final case class EmailFilter(matches: Email => Boolean) { self =>
  def &&(that: EmailFilter): EmailFilter = EmailFilter(email => self.matches(email) && that.matches(email))

  def ||(that: EmailFilter): EmailFilter =
    EmailFilter(email => self.matches(email) || that.matches(email))

  def unary_! : EmailFilter =
    EmailFilter(email => !self.matches(email))
}

object Executable {
  def subjectContains(phrase: String): EmailFilter = EmailFilter(_.subject.contains(phrase))
  def main(args: Array[String]): Unit = {
    val filter: EmailFilter =
      (subjectContains("discount") || subjectContains("clear")) && !subjectContains("linquidation")

    val email: Email  = Email("discount here")
    val email2: Email = Email("kjm")

    val out = Seq(email, email2).filter(filter.matches)
    println(out)
  }
}
