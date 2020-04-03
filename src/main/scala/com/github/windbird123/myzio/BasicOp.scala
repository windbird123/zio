package com.github.windbird123.myzio

import zio._
import zio.console._
object BasicOp {
  def main(args: Array[String]): Unit = {
    IO.succeed(21).map(_ * 2)
    IO.fail("No").mapError[Exception](msg => new Exception(msg))

    // zip operates sequentially: the effect on the left side is executed before the effect on the right side.
    IO.succeed("4").zip(IO.succeed(2)) // UIO[(String, Int)]

    IO.succeed(4).zipWith(IO.succeed(2))(_ + _)  // zipWith 는 ZIO.mapN 과 같다.
    ZIO.mapN(IO.succeed(4), IO.succeed(2))(_ + _) // for comprehension 으로도 표현할 수 있다.

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
    Runtime.default.unsafeRun[Throwable, Int](
      zipR.provideLayer(console.Console.live)
    ) // 3

    val zipL: ZIO[Console, Throwable, String] = a <* c
    Runtime.default.unsafeRun[Throwable, String](
      zipL.provideLayer(console.Console.live)
    ) // A

    val zipF: ZIO[Console, Throwable, Int] = a *> b.either *> c
    Runtime.default.unsafeRun[Throwable, Int](
      zipF.provideLayer(console.Console.live)
    ) // a b c
  }
}
