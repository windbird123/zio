package com.github.windbird123.datatype

import java.io.{ File, PrintWriter }

import zio._
import zio.stream._

import scala.io.{ BufferedSource, Codec, Source }
import zio.duration._

// https://dev.to/gurghet/10-days-with-the-zio-2-10-4jpg 를 참고하자
object StreamTest extends zio.App {
  // create stream
  val stream: Stream[Nothing, Int]             = Stream(1, 2, 3)
  val streamFromIteralbe: Stream[Nothing, Int] = Stream.fromIterable(0 to 100)

  // map
  val stringStream: Stream[Nothing, String] = streamFromIteralbe.map(_.toString)

  // partition
  val partitionResult: ZManaged[Any, Nothing, (ZStream[Any, Nothing, Int], ZStream[Any, Nothing, Int])] =
    Stream.fromIterable(0 to 100).partition(_ % 2 == 0, buffer = 50)

  // bounded queue, note toQueueUnbounded
  val queuedStream: ZManaged[Any, Nothing, Dequeue[Take[Nothing, Int]]] = streamFromIteralbe.toQueue(2)

  // with managed
  val readManaged: ZManaged[Any, Throwable, BufferedSource] =
    IO.effect(Source.fromFile("d:/now/ocr_jp.txt", Codec.UTF8.name)).toManaged(x => UIO(x.close()))
  val writeManaged: ZManaged[Any, Throwable, PrintWriter] =
    IO.effect(new PrintWriter(new File("d:/now/out.txt"), Codec.UTF8.name)).toManaged(x => UIO(x.close()))

  val rwResource: ZManaged[Any, Throwable, (BufferedSource, PrintWriter)] = for {
    r <- readManaged
    w <- writeManaged
  } yield (r, w)

  streamFromIteralbe.map { x =>
    rwResource.use {
      case (r, w) => UIO(3)
    }
  }

  val myTest = for {
    queue  <- Queue.bounded[Int](100)
    _      <- queue.offer(1)
    _      <- queue.offer(2)
    fiber  <- queue.offer(99).repeat(Schedule.fixed(1.second)).fork
    stream = ZStream.fromQueue(queue)
    _      <- stream.mapM(number => UIO(println(s"Number: $number"))).runDrain
    _      <- fiber.await
  } yield ()

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = myTest.exitCode
}
