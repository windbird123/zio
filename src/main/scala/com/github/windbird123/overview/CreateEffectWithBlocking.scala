package com.github.windbird123.overview

import java.io.IOException
import java.net.ServerSocket

import zio._

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.Try

object CreateEffectWithBlocking extends App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    ZIO.succeed(10)
    Task.succeed("a")

    lazy val bigList = (0 to 1000).toList
    ZIO.effectTotal(bigList) // lazy, bigList should be successful effect

    val zOption = ZIO.fromOption(Some(2)) // ZIO[Any, Unit, Int]
    zOption.mapError(_ => "empty") // ZIO[Any, String, Int]

    ZIO.fromEither(Right("S")) // IO[Nothing, String]
    ZIO.fromEither(Left(1))    // IO[Int, Nothing]

    ZIO.fromTry(Try(1 / 0))

    // ZIO.access 와 동일하다. ZIO.access 를 사용하자
    ZIO.fromFunction((i: Int) => i * i) // ZIO[Int, Nothing, Int]
    ZIO.access((i: Int) => i * i)

    lazy val future = Future.successful("Hello")
    // Task[String]
    ZIO.fromFuture(implicit ec => future.map(_ => "Good"))

    ZIO.effect(StdIn.readLine()) // Task[String]
    ZIO
      .effect(StdIn.readLine())
      .refineToOrDie[IOException]  // if fatal => die, else refineTo IOException
    ZIO.effectTotal(println("Hi")) // UIO[Unit]

    // blocking thread pool 에서 실행된다는 의미이지 async 하다는 것은 아니다.
    val blockingEffect = zio.blocking.effectBlocking(Thread.sleep(1000L))
    zio.blocking.effectBlockingInterrupt(blockingEffect)

    // interrupt 할려면 cancelable 로 만들어야 함
    def accept(socket: ServerSocket) =
      zio.blocking.effectBlockingCancelable(socket.accept())(
        UIO.effectTotal(socket.close())
      )

    // zio.blocking.blocking 이걸 더 자주 사용하게 될 것 같다.. (이미 존재하는 zio obj 에 적용하는 ..)
    val task = Task.effect(scala.io.Source.fromURL("http://..."))
    zio.blocking.blocking(task) // effect will be executed on the blocking thread pool

    UIO(ExitCode.success)
  }
}

object MemoizeTest extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    val side: UIO[Int] = UIO.succeed {
      println("UIO ..")
      3
    }

    val a = for {
      m <- side.memoize
      x <- m
      y <- m
    } yield x + y

    val b = for {
      x <- side
      y <- side
    } yield x + y

    a.exitCode
  }
}
