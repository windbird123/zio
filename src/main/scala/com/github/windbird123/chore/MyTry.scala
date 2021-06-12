package com.github.windbird123.chore

trait MyTry[+A] {
  def flatMap[B](f: A => MyTry[B]): MyTry[B]

  def map[B](f: A => B): MyTry[B] = flatMap(x => MyTry(f(x)))
}

case class Success[+A](a: A) extends MyTry[A] {
  override def flatMap[B](f: A => MyTry[B]): MyTry[B] =
    try {
      f(a)
    } catch {
      case e: Throwable => Fail(e)
    }
}

case class Fail(e: Throwable) extends MyTry[Nothing] {
  override def flatMap[B](f: Nothing => MyTry[B]): MyTry[B] = this
}

object MyTry {
  def apply[A](a: => A): MyTry[A] =
    try {
      Success(a)
    } catch {
      case e: Throwable => Fail(e)
    }
}

object Impl {
  def main(args: Array[String]): Unit = {

    val t1 = MyTry("a")
//    val t2 = MyTry("b")
    val t2 = MyTry(throw new Exception("kk")) // NOTE !!!

    val t: MyTry[String] = for {
      x <- t1
      y <- t2
    } yield s"$x  + $y"

    println(t)
  }
}
