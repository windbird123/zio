package com.github.windbird123.practice.module

import zio._

object WriteModule {
  trait Service {
    def write(box: Box): IO[StepError, Unit]
  }

  def write(box: Box): ZIO[Has[Service], StepError, Unit] =
    ZIO.accessM(_.get[Service].write(box))

  val live: ZLayer[Has[CuveConnection], Nothing, Has[Service]] =
    ZLayer.fromFunction { env =>
      new Service {
        override def write(box: Box): IO[StepError, Unit] =
          UIO.effectTotal(println("hi"))
      }
    }
}
