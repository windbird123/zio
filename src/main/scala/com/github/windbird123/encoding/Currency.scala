package com.github.windbird123.encoding

/*
 "Programming in Scala" 20 장 추상멤버 내용

  Currency 에 Dollar 와 Won 이 있다.
  Dollar + Dollar, Won + Won 은 가능해도
  Dollar + Won 은 불가능하도록 해야 한다.
 */
sealed trait Currency[T <: Currency[T]] { self =>
  def amount: Long
  def make(x: Long): Currency[T]
  def +(that: Currency[T]): Currency[T] = make(self.amount + that.amount)
}

case class Dollar(amount: Long) extends Currency[Dollar] {
  override def make(x: Long): Currency[Dollar] = Dollar(x)
}

case class Won(amount: Long) extends Currency[Won] {
  override def make(x: Long): Currency[Won] = Won(x)
}

object CurrencyTest {
  def main(args: Array[String]): Unit = {
    val d1 = Dollar(3)
    val d2 = Dollar(4)
    val d  = d1 + d2
    println("dollar: " + d.amount)

    val w1 = Won(2)
    val w2 = Won(6)
    val w  = w1 + w2

    println("won: " + w.amount)

    // 아래와 같이 dollar 와 won 의 덧셈은 실패해야 한다.
//    val wrong = d + w
//    println(wrong)
  }
}
