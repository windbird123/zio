package com.github.windbird123.stream

import java.nio.file.{ Files, Path, Paths }

import zio._
import zio.stream._

// https://dev.to/gurghet/10-days-with-the-zio-2-10-4jpg

object WordCount extends zio.App {
  val someText: String = "How much wood would a woodpecker peck if a woodpecker would peck wood?"

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val prog = for {
      chunk <- zio.stream.Stream(someText).run(ZSink.splitOn(" "))
      counts <- zio.stream.Stream
                 .fromChunk(chunk)
                 .groupByKey(identity) {
                   case (word, stream) => stream.aggregate(ZSink.collectAll[String]).map(list => (list.size, word))
                 }
                 .runCollect

      _ <- console.putStrLn(counts.mkString(", "))
    } yield ()

    prog.as(0)
  }
}

//object WordCountFromFile extends zio.App {
//  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
//    val prog = for {
//      inputStream <- IO(Files.newInputStream(Paths.get("d:/now/a.txt")))
//      counts <- ZStream
//                 .fromInputStream(inputStream)
//                 .chunks
//                 .aggregate(ZSink.utf8DecodeChunk)
//                 .aggregate(ZSink.splitOn(" "))
//                 .flatMap(ZStream.fromChunk)
//                 .filter(_.matches("[\\w]+"))
//                 .groupByKey(identity) {
//                   case (word, stream) => stream.aggregate(ZSink.collectAll[String]).map(list => (list.size, word))
//                 }
//                 .runCollect
//
//      _ <- console.putStrLn(counts.sortBy(_._1).reverse.mkString(", "))
//    } yield ()
//
//    prog
//      .onError(failure => console.putStrLn(failure.toString))
//      .fold(_ => 1, _ => 0)
//  }
//}
