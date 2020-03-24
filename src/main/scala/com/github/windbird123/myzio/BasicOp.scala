package com.github.windbird123.myzio

import zio._
import zio.console._
object BasicOp {
  def main(args: Array[String]): Unit = {
    IO.succeed(21).map(_ * 2)
    IO.fail("No").mapError[Exception](msg => new Exception(msg))

    // zip operates sequentially: the effect on the left side is executed before the effect on the right side.
    IO.succeed("4").zip(IO.succeed(2)) // UIO[(String, Int)]

    putStrLn("What is your name?").zipRight(getStrLn)
    putStrLn("What is your name?") *> getStrLn

    val a = Task.effect {
      println("a")
      "A"
    }
    val b = Task.effect {
      println("b")
      throw new Exception("b fail")
    }
    val c = Task.effect {
      println("c")
      3
    }

    val zipR: ZIO[Console, Throwable, Int] = a *> c
    Runtime.default.unsafeRun[Throwable, Int](zipR) // 3

    val zipL: ZIO[Console, Throwable, String] = a <* c
    Runtime.default.unsafeRun[Throwable, String](zipL) // A

    val zipF: ZIO[Console, Throwable, Int] = a *> b.either *> c
    Runtime.default.unsafeRun[Throwable, Int](zipF) // a b c
  }
}
