package com.github.windbird123.encoding

/*
 "Programming in Scala" 20 장 추상멤버 내용을 변형해서 작성해 봄

  Currency 에 Dollar 와 Won 이 있다.
  Dollar + Dollar, Won + Won 은 가능해도
  Dollar + Won 은 불가능하도록 해야 한다.
 */


object CurrencyTestV1 {
  sealed trait Currency[T <: Currency[T]] { self =>
    def amount: Long
    def make(x: Long): T
    def +(that: T): T = make(self.amount + that.amount)
  }

  case class Dollar(amount: Long) extends Currency[Dollar] {
    override def make(x: Long): Dollar = Dollar(x)
  }

  case class Won(amount: Long) extends Currency[Won] {
    override def make(x: Long): Won = Won(x)
  }


  def main(args: Array[String]): Unit = {
    val d1 = Dollar(3)
    val d2 = Dollar(4)
    val d  = d1 + d2
    println("dollar: " + d.amount)

    val w1 = Won(2)
    val w2 = Won(6)
    val w  = w1 + w2

    println("won: " + w.amount)

    // 아래와 같이 dollar 와 won 의 덧셈은 실패해야 한다. (컴파일 할 때 실패)
//    val wrong = d + w
//    println(wrong)
  }
}

object CurrencyTestV2 {
  sealed trait Currency {
    def amount: Long
    def make(x: Long): Currency
  }

  case class Dollar(amount: Long) extends Currency {
    override def make(x: Long): Currency = Dollar(x)
  }
  case class Won(amount: Long) extends Currency {
    override def make(x: Long): Currency = Won(x)
  }


  def main(args: Array[String]): Unit = {
    val d1 = Dollar(3)
    val d2 = Dollar(4)

    val w1 = Won(2)
    val w2 = Won(6)

    // How?
    ???
  }
}
