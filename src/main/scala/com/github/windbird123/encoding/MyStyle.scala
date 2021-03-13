package com.github.windbird123.encoding

import com.github.windbird123.encoding.declarative.Email

case class MyEmail(subject: String)

sealed trait MyEmailFilter { self =>
  def matches(email: Email): Boolean = self match {
    case MyLengthFilter(len: Int)       => email.subject.length >= len
    case MyContainsFilter(term: String) => email.subject.contains(term)
  }

  def &&(that: MyEmailFilter): MyEmailFilter = new MyEmailFilter {
    override def matches(email: Email): Boolean = self.matches(email) && that.matches(email)
  }
}

final case class MyLengthFilter(len: Int)       extends MyEmailFilter
final case class MyContainsFilter(term: String) extends MyEmailFilter

object MyStyle {
  def main(args: Array[String]): Unit = {
    val lengthFilter   = MyLengthFilter(5)
    val containsFilter = MyContainsFilter("kjm")
    val filter         = lengthFilter && containsFilter

    val myEmail = Email("kjm hi")
    val out     = filter.matches(myEmail)
    println(out)
  }
}
