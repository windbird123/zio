package com.github.windbird123.demo

import zio._
import zio.console.Console

import java.io.IOException

object QueueFiberDemo extends zio.App {
  val logic: ZIO[Console, IOException, Unit] = for {
    queue <- Queue.bounded[Int](1)
    _     <- queue.offer(1)
    fiber <- queue.offer(2).fork
    v     <- queue.take
    _     <- console.putStrLn(v.toString)
    _     <- fiber.join
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = logic.exitCode
}
