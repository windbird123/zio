package com.github.windbird123.zlayer2

import zio.clock.Clock
import zio.console.Console
import zio.magic._
import zio.{App, ExitCode, Has, UIO, URIO, URLayer, ZIO}

object Basic extends App {
  trait Logging {
    def log(line: String): UIO[Unit]
  }

  object Logging {
    def log(line: String): URIO[Has[Logging], Unit]                               = ZIO.serviceWith[Logging](_.log(line))
    val live: URLayer[Has[Console.Service] with Has[Clock.Service], Has[Logging]] = LoggingLive.toLayer[Logging]
  }

  case class LoggingLive(console: Console.Service, clock: Clock.Service) extends Logging {
    override def log(line: String): UIO[Unit] =
      for {
        current <- clock.currentDateTime.orDie
        _       <- console.putStrLn(current.toString + ": " + line).orDie
      } yield ()
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val program: URIO[Has[Logging], Unit] = Logging.log("Hi")
    program.injectSome[zio.ZEnv](Logging.live).exitCode
  }
}
