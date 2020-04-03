package com.github.windbird123.myzio

import zio._

// App provides a DefaultRuntime, which contains a Console
object ConcurrencyFiber extends App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    def fib(n: Long): UIO[Long] =
      UIO.effectTotal {
        if (n <= 1) UIO.succeed(n)
        else fib(n - 1).zipWith(fib(n - 2))(_ + _)
      }.flatten

    // creates a single fiber, which executes fib(100)
    val fib100Fiber: UIO[Fiber[Nothing, Long]] = for {
      fiber <- fib(100).fork
    } yield fiber

    // fib100Fiber 결과를 출력할려고 하면 계산하는데 시간이 너무 오래걸린다.

    // join fiber
    val awaitFiber = for {
      fiber <- IO.succeed("Hi").fork
      message <- fiber.join
    } yield message

    val program = for {
      msg <- awaitFiber
      _ <- console.putStrLn(msg)
    } yield ()

    /////////////////////////////////////////////////////////////////////////////
    // Parallelism
    /////////////////////////////////////////////////////////////////////////////

    // ZIO.collectAll ==>  Future.sequence 와 비슷
    // ZIO.foreach ==> Future.traverse 와 비슷
    // ZIO.reduceAll ==> reduce 와 비슷
    // ZIO.mergeAll ==> foldLeft 와 비슷
    // ZIO.mapN ==> mapWith 와 비슷

    // racing
    for {
      winner <- IO.succeed("Hello").race(IO.succeed("Goodbye"))
    } yield winner

    program *> IO.succeed(0)
  }
}


object FibTest extends zio.App {
  def fib(n: Long): UIO[Long] = if (n <= 1) UIO.effectTotal(n) else fib(n-1).zipWith(fib(n-2))(_ + _)

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val p = for {
      f <- fib(40)  // fib(50) 만 되어도 힘들어 한다.
      _ <- zio.console.putStrLn(f.toString)
    } yield ()

    p.as(0)
  }
}


object FactorialTest extends zio.App {
  def fact(n: BigInt): UIO[BigInt] = if (n <= 1) UIO.effectTotal(1) else UIO.effectTotal(n).zipWith(fact(n-1))(_ * _)

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val p = for {
       f <- fact(50000)
       _ <- zio.console.putStrLn(f.toString)
    } yield ()

    p.as(0)
  }
}
