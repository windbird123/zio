package com.github.windbird123.overview

import java.io.InputStream

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

    // Managed
    val managedResource = Managed.make(IO.effect(new FileInputStream("..")))(stream => UIO(stream.close()))
    val userResource    = managedResource.use(stream => IO.unit)

    // Combining Managed
    val managed1: Managed[Nothing, Queue[Int]] = Managed.make(Queue.bounded[Int](10))(_.shutdown)
    val managed2: Managed[Throwable, FileInputStream] =
      Managed.make(IO.effect(new FileInputStream("..")))(stream => UIO(stream.close()))

    val combined: Managed[Throwable, (Queue[Int], InputStream)] = for {
      queue  <- managed1
      stream <- managed2
    } yield (queue, stream)

    combined.use { case (queue, stream) => IO.unit }

    // return
    ZIO.succeed(0)
  }
}
