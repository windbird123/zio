package com.github.windbird123.zlayer2

import zio.clock.Clock
import zio.console.Console
import zio.magic._
import zio.{ App, ExitCode, Has, Ref, UIO, URIO, URLayer, ZIO, ZLayer }

object Basic extends App {
  trait Logging {
    def log(line: String): UIO[Unit]
  }

  object Logging {
    // URIO 가 아닌 ZStream 일 경우 ZIO.serviceWith 대신에
    // ZStream.accessStream(_.get.XXX) 사용
    def log(line: String): URIO[Has[Logging], Unit] = ZIO.serviceWith[Logging](_.log(line))

    val refLayer: ZLayer[Any, Nothing, Has[Ref[Long]]] = Ref.make(0L).toLayer
    val fullLayer: URLayer[Has[Console.Service] with Has[Clock.Service] with Has[Ref[Long]], Has[Logging]] =
      LoggingLive.toLayer[Logging]

    val live: ZLayer[Has[Console.Service] with Has[Clock.Service], Nothing, Has[Logging]] =
      ZLayer.wireSome[Has[Console.Service] with Has[Clock.Service], Has[Logging]](refLayer, fullLayer)
  }

  case class LoggingLive(console: Console.Service, clock: Clock.Service, ref: Ref[Long]) extends Logging {
    override def log(line: String): UIO[Unit] =
      for {
        current <- clock.currentDateTime.orDie
        count   <- ref.modify(x => (x + 1, x + 1))
        _       <- console.putStrLn(current.toString + s": [$count] " + line).orDie
      } yield ()
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val program: URIO[Has[Logging], Unit] = Logging.log("Hi") *> Logging.log("Hello")
    program.injectSome[zio.ZEnv](Logging.live).exitCode
  }
}
