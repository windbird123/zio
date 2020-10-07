package com.github.windbird123.demo

import zio.Has

object HasDemo {
  case class A(name: String)
  case class B(age: Int)

  def main(args: Array[String]): Unit = {

    val hasA: Has[A] = Has(A("kjm"))
    val hasB: Has[B] = Has(B(22))

    val mixed: Has[A] with Has[B] = hasA ++ hasB
    val b: B                      = mixed.get[B]
    println(b.age)
  }
}
