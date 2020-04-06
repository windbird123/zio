package com.github.windbird123.datatype

import zio._
import zio.stream._

// https://dev.to/gurghet/10-days-with-the-zio-2-10-4jpg 를 참고하자
object StreamTest extends zio.App {
  // create stream
  val stream: Stream[Nothing, Int]             = Stream(1, 2, 3)
  val streamFromIteralbe: Stream[Nothing, Int] = Stream.fromIterable(0 to 100)
  val sink                                     = Sink.await[Int]
  streamFromIteralbe.run(sink)

  // consume a stream
  Stream.fromIterable(0 to 100).foreach(i => console.putStrLn(i.toString))
  Stream(1, 2, 3).run(Sink.foldLeft(0)((acc: Int, x: Int) => acc + x))

  // working on several streams
  val merged: ZStream[Any, Nothing, Int]        = Stream(1, 2, 3).merge(Stream(4, 5, 6))
  val zipped: ZStream[Any, Nothing, (Int, Int)] = Stream(1, 2, 3).zip(Stream(4, 5, 6))

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val myProg = streamFromIteralbe.foreach((x: Int) => console.putStrLn(x.toString)) // 순서대로 출력됨
//    val myProg = merged.foreach( (x: Int) => console.putStrLn(x.toString))  // 1,2,3,4,5,6 출력이 순서대로 보장되는 것은 아님 !!!
    myProg.as(0)
  }
}
