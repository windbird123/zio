package com.github.windbird123.chore

object SumExample {
  def imperativeSum(ints: List[Int]): Int = {
    var sum = 0
    for (i <- ints) {
      sum += i
    }
    sum
  }

  def functionalSum(xs: List[Int]): Int = xs match {
    case Nil       => 0
    case x :: tail => x + functionalSum(tail)
  }
}
