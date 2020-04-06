package com.github.windbird123.overview

import zio._

object ResourceHandle extends App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {

    // The finalizer is not allowed to fail,
    val finalizer = UIO.effectTotal(println("Finalizing!"))
    IO.fail("Failed").ensuring(finalizer)

    // Bracket
    import java.io.FileInputStream
    Task
      .effect(new FileInputStream("/naver/a.txt"))
      .bracket(stream => URIO.effectTotal(stream.close())) { stream =>
        Task.effect(scala.io.Source.fromInputStream(stream, scala.io.Codec.UTF8.name))
      }

    ZIO.succeed(0)
  }
}
