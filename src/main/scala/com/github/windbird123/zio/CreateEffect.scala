package com.github.windbird123.zio

import java.io.IOException
import java.net.ServerSocket

import zio.{App, Task, UIO, ZIO}

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.Try

object CreateEffect extends App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    ZIO.succeed(10)
    Task.succeed("a")

    lazy val bigList = (0 to 1000).toList
    ZIO.effectTotal(bigList) // lazy, bigList should be successful effect

    val zOption = ZIO.fromOption(Some(2)) // ZIO[Any, Unit, Int]
    zOption.mapError(_ => "empty") // ZIO[Any, String, Int]

    ZIO.fromEither(Right("S")) // IO[Nothing, String]
    ZIO.fromEither(Left(1)) // IO[Int, Nothing]

    ZIO.fromTry(Try(1 / 0))
    ZIO.fromFunction((i: Int) => i * i) // ZIO[Int, Nothing, Int]

    lazy val future = Future.successful("Hello")
    // Task[String]
    ZIO.fromFuture { implicit ec =>
      future.map(_ => "Good")
    }

    ZIO.effect(StdIn.readLine()) // Task[String]
    ZIO
      .effect(StdIn.readLine())
      .refineToOrDie[IOException] // if fatal => die, else refineTo IOException
    ZIO.effectTotal(println("Hi")) // UIO[Unit]

    val blockingEffect = zio.blocking.effectBlocking(Thread.sleep(1000L))
    zio.blocking.effectBlockingInterrupt(blockingEffect)

    // interrupt 할려면 cancelable 로 만들어야 함
    def accept(socket: ServerSocket) =
      zio.blocking.effectBlockingCancelable(socket.accept())(
        UIO.effectTotal(socket.close())
      )

    val task = Task.effect(io.Source.fromURL("http://..."))
    zio.blocking.blocking(task) // effect will be executed on the blocking thread pool

    ZIO.succeed(0)
  }
}
