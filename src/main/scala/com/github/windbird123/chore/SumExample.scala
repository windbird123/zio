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

// 1. What's wrong imperativeSum? using var ?
// 2. functionalSum blow stack ?
// 3. which is faster ?
// 4. which approach more maintainable ?
// 5. if I need parallel version of sum, which on is better ?