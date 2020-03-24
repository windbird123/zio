package com.github.windbird123.myzio

import zio._

// App provides a DefaultRuntime, which contains a Console
object BasicConcurrency extends App {
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

    // fib100Fiber 결과를 출력할려고 했는데 잘 안된다...

    // join fiber
    val awaitFiber = for {
      fiber <- IO.succeed("Hi").fork
      message <- fiber.join
    } yield message

    val program = for {
      msg <- awaitFiber
      _ <- console.putStrLn(msg)
    } yield ()

    // Parallelism
    // ZIO.collectAll ==>  Future.sequence 와 비슷
    // ZIO.foreach ==> Future.traverse 와 비슷
    // ZIO.reduceAll ==> reduce 와 비슷
    // ZIO.mergeAll ==> foldLeft 와 비슷

    // racing
    for {
      winner <- IO.succeed("Hello").race(IO.succeed("Goodbye"))
    } yield winner

    program *> IO.succeed(0)
  }
}
