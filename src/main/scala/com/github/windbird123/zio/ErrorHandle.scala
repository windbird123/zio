package com.github.windbird123.zio

import java.io.{File, FileNotFoundException}

import zio._
object ErrorHandle extends App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    // either or option
    val zEither: UIO[Either[String, Int]] = IO.fail("Failed").either

    // opposite of either
    val zAbsolve: IO[String, Int] = zEither.absolve

    // catchAll
    val catchAllTest: Task[File] = Task
      .effect(new File("first.txt"))
      .catchAll(_ => Task.effect(new File("second.txt")))

    // catchSome
    val catchSomeTest: Task[String] =
      Task.fail(new Exception("fail")).catchSome {
        case _: IndexOutOfBoundsException => IO.effect("fail")
      }

    // fallback
    val fallbackTask: UIO[Int] = Task.effect(1 / 0).orElse(UIO.succeed(3))

    // fold, foldM
    Task.fail(throw new Exception("")).fold(_ => 0, x => x)

    // retry
    val retriedOpenFile: ZIO[clock.Clock, Throwable, Array[Byte]] = {
      val task: Task[Array[Byte]] = Task.fail(new FileNotFoundException("not"))
      task.retry(Schedule.recurs(3))
    }

    // retryOrElse, retryOrElseEither
    retriedOpenFile.retryOrElse(
      Schedule.recurs(5),
      (_: Throwable, _: Int) => Task.succeed(Array.empty[Byte])
    )

    ZIO.succeed(0)
  }
}
